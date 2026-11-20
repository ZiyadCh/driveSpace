package ma.youcode.driveSpace.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import ma.youcode.driveSpace.dto.StopPointRequest;
import ma.youcode.driveSpace.dto.TripRequest;
import ma.youcode.driveSpace.dto.TripResponse;
import ma.youcode.driveSpace.entities.Conductor;
import ma.youcode.driveSpace.entities.StopPoint;
import ma.youcode.driveSpace.entities.Trip;
import ma.youcode.driveSpace.entities.Vehicle;
import ma.youcode.driveSpace.enums.TripStatus;
import ma.youcode.driveSpace.exception.BusinessRuleException;
import ma.youcode.driveSpace.exception.ForbiddenActionException;
import ma.youcode.driveSpace.exception.ResourceNotFoundException;
import ma.youcode.driveSpace.mapper.StopPointMapper;
import ma.youcode.driveSpace.mapper.TripMapper;
import ma.youcode.driveSpace.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.*;

@ExtendWith(MockitoExtension.class)
class TripServiceImplTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private StopPointRepository stopPointRepository;

    @Mock
    private ConductorRepository conductorRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private TripMapper mapper;

    @Mock
    private StopPointMapper stopPointMapper;

    @InjectMocks
    private TripServiceImpl tripService;

    private Trip trip;
    private Conductor conductor;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        trip = new Trip();
        trip.setId(1L);
        trip.setStatus(TripStatus.PUBLIE);

        conductor = new Conductor();
        conductor.setId(10L);

        vehicle = new Vehicle();
        vehicle.setId(20L);
    }

    @Test
    void findAll_shouldReturnAllTrips() {
        List<Trip> trips = List.of(trip);
        List<TripResponse> responses = List.of(mock(TripResponse.class));

        when(tripRepository.findAll()).thenReturn(trips);
        when(mapper.toResponseList(trips)).thenReturn(responses);

        List<TripResponse> result = tripService.findAll();

        assertEquals(responses, result);
        verify(tripRepository).findAll();
        verify(mapper).toResponseList(trips);
    }

    @Test
    void findById_shouldReturnTrip() {
        TripResponse response = mock(TripResponse.class);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(mapper.toResponse(trip)).thenReturn(response);

        TripResponse result = tripService.findById(1L);

        assertEquals(response, result);
    }

    @Test
    void findById_shouldThrowWhenTripNotFound() {
        when(tripRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
            ResourceNotFoundException.class,
            () -> tripService.findById(1L)
        );
    }

    @Test
    void create_shouldCreateTripWithStopPoints() {
        TripRequest request = mock(TripRequest.class);
        StopPointRequest stopRequest = mock(StopPointRequest.class);

        TripResponse response = mock(TripResponse.class);
        StopPoint stopPoint = new StopPoint();

        when(mapper.toEntity(request)).thenReturn(trip);
        when(conductorRepository.findById(10L)).thenReturn(Optional.of(conductor));
        when(vehicleRepository.findById(20L)).thenReturn(Optional.of(vehicle));
        when(request.conductorId()).thenReturn(10L);
        when(request.vehicleId()).thenReturn(20L);
        when(request.stopPoints()).thenReturn(List.of(stopRequest));

        when(tripRepository.saveAndFlush(trip)).thenReturn(trip);
        when(stopPointMapper.toEntity(stopRequest)).thenReturn(stopPoint);
        when(stopPointRepository.save(stopPoint)).thenReturn(stopPoint);
        when(mapper.toResponse(trip)).thenReturn(response);

        TripResponse result = tripService.create(request);

        assertEquals(response, result);
        assertEquals(conductor, trip.getConductor());
        assertEquals(vehicle, trip.getVehicle());
        assertEquals(trip, stopPoint.getTrip());

        verify(tripRepository).saveAndFlush(trip);
        verify(stopPointRepository).save(stopPoint);
    }

    @Test
    void create_shouldThrowWhenConductorNotFound() {
        TripRequest request = mock(TripRequest.class);

        when(mapper.toEntity(request)).thenReturn(trip);
        when(request.conductorId()).thenReturn(10L);
        when(conductorRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(
            ResourceNotFoundException.class,
            () -> tripService.create(request)
        );

        verify(tripRepository, never()).saveAndFlush(any());
    }

    @Test
    void create_shouldThrowWhenVehicleNotFound() {
        TripRequest request = mock(TripRequest.class);

        when(mapper.toEntity(request)).thenReturn(trip);
        when(request.conductorId()).thenReturn(10L);
        when(conductorRepository.findById(10L)).thenReturn(Optional.of(conductor));
        when(request.vehicleId()).thenReturn(20L);
        when(vehicleRepository.findById(20L)).thenReturn(Optional.empty());

        assertThrows(
            ResourceNotFoundException.class,
            () -> tripService.create(request)
        );

        verify(tripRepository, never()).saveAndFlush(any());
    }

    @Test
    void search_shouldReturnPaginatedTrips() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Trip> page = new PageImpl<>(List.of(trip));
        TripResponse response = mock(TripResponse.class);

        when(tripRepository.search(
            null, null, null, null, pageable
        )).thenReturn(page);

        when(mapper.toResponse(trip)).thenReturn(response);

        Page<TripResponse> result =
            tripService.search(null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(response, result.getContent().get(0));
    }

    @Test
    void search_shouldConvertDateIntoDayRange() {
        LocalDateTime date = LocalDateTime.of(2026, 11, 20, 15, 30);
        LocalDateTime from = date.toLocalDate().atStartOfDay();
        LocalDateTime to = from.plusDays(1);

        Pageable pageable = PageRequest.of(0, 10);

        when(tripRepository.search(
            null, null, from, to, pageable
        )).thenReturn(Page.empty(pageable));

        tripService.search(null, null, date, pageable);

        verify(tripRepository).search(
            null, null, from, to, pageable
        );
    }

    @Test
    void delete_shouldDeleteTripWithoutReservations() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(reservationRepository.existsByTripId(1L)).thenReturn(false);

        tripService.delete(1L);

        verify(tripRepository).delete(trip);
    }

    @Test
    void delete_shouldThrowWhenTripHasReservations() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(reservationRepository.existsByTripId(1L)).thenReturn(true);

        assertThrows(
            BusinessRuleException.class,
            () -> tripService.delete(1L)
        );

        verify(tripRepository, never()).delete(any());
    }

    @Test
    void cancel_shouldCancelTrip() {
        trip.setConductor(conductor);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        tripService.cancel(1L, 10L);

        assertEquals(TripStatus.ANNULE, trip.getStatus());
        verify(tripRepository).save(trip);
    }

    @Test
    void cancel_shouldThrowWhenConductorIsDifferent() {
        trip.setConductor(conductor);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        assertThrows(
            ForbiddenActionException.class,
            () -> tripService.cancel(1L, 99L)
        );

        verify(tripRepository, never()).save(any());
    }

    @Test
    void cancel_shouldThrowWhenAlreadyCancelled() {
        trip.setConductor(conductor);
        trip.setStatus(TripStatus.ANNULE);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        assertThrows(
            BusinessRuleException.class,
            () -> tripService.cancel(1L, 10L)
        );

        verify(tripRepository, never()).save(any());
    }
}
