package ma.youcode.driveSpace.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import ma.youcode.driveSpace.dto.ReservationRequest;
import ma.youcode.driveSpace.dto.ReservationResponse;
import ma.youcode.driveSpace.entities.Conductor;
import ma.youcode.driveSpace.entities.Passenger;
import ma.youcode.driveSpace.entities.Reservation;
import ma.youcode.driveSpace.entities.StopPoint;
import ma.youcode.driveSpace.entities.Trip;
import ma.youcode.driveSpace.enums.ReservationStatus;
import ma.youcode.driveSpace.enums.TripStatus;
import ma.youcode.driveSpace.exception.BusinessRuleException;
import ma.youcode.driveSpace.exception.ForbiddenActionException;
import ma.youcode.driveSpace.exception.ResourceNotFoundException;
import ma.youcode.driveSpace.mapper.ReservationMapper;
import ma.youcode.driveSpace.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private ReservationMapper mapper;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Trip trip;
    private StopPoint pickup;
    private StopPoint dropoff;
    private Passenger passenger;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        pickup = new StopPoint();
        pickup.setCity("Tanger");
        pickup.setStopOrder(1);
        pickup.setPriceFromDeparture(new BigDecimal("10.00"));

        dropoff = new StopPoint();
        dropoff.setCity("Ouarzazate");
        dropoff.setStopOrder(2);
        dropoff.setPriceFromDeparture(new BigDecimal("40.00"));

        trip = new Trip();
        trip.setId(1L);
        trip.setStatus(TripStatus.PUBLIE);
        trip.setOfferedSeats(4);
        trip.setDepartureTime(LocalDateTime.now().plusDays(3));
        trip.getStopPoints().addAll(List.of(pickup, dropoff));

        Conductor conductor = new Conductor();
        conductor.setId(20L);
        trip.setConductor(conductor);

        passenger = new Passenger();
        passenger.setId(10L);

        reservation = new Reservation();
        reservation.setId(5L);
        reservation.setPassenger(passenger);
        reservation.setTrip(trip);
        reservation.setStatus(ReservationStatus.CONFIRMEE);
    }

    @Test
    void findAll_shouldReturnAllReservations() {
        List<Reservation> reservations = List.of(reservation);
        List<ReservationResponse> responses = List.of(mock(ReservationResponse.class));

        when(reservationRepository.findAll()).thenReturn(reservations);
        when(mapper.toResponseList(reservations)).thenReturn(responses);

        List<ReservationResponse> result = reservationService.findAll();

        assertEquals(responses, result);
    }

    @Test
    void findById_shouldReturnReservation() {
        ReservationResponse response = mock(ReservationResponse.class);

        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));
        when(mapper.toResponse(reservation)).thenReturn(response);

        ReservationResponse result = reservationService.findById(5L);

        assertEquals(response, result);
    }

    @Test
    void findById_shouldThrowWhenReservationNotFound() {
        when(reservationRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(
            ResourceNotFoundException.class,
            () -> reservationService.findById(5L)
        );
    }

    @Test
    void findByPassenger_shouldReturnReservations() {
        List<Reservation> reservations = List.of(reservation);
        List<ReservationResponse> responses = List.of(mock(ReservationResponse.class));

        when(passengerRepository.existsById(10L)).thenReturn(true);
        when(reservationRepository.findByPassengerIdOrderByReservedAtDesc(10L)).thenReturn(reservations);
        when(mapper.toResponseList(reservations)).thenReturn(responses);

        List<ReservationResponse> result = reservationService.findByPassenger(10L);

        assertEquals(responses, result);
    }

    @Test
    void findByPassenger_shouldThrowWhenPassengerNotFound() {
        when(passengerRepository.existsById(10L)).thenReturn(false);

        assertThrows(
            ResourceNotFoundException.class,
            () -> reservationService.findByPassenger(10L)
        );
    }

    @Test
    void findByTripForConductor_shouldThrowWhenConductorIsDifferent() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        assertThrows(
            ForbiddenActionException.class,
            () -> reservationService.findByTripForConductor(1L, 99L)
        );
    }

    @Test
    void create_shouldCreateReservationWithCalculatedPrice() {
        ReservationRequest request = new ReservationRequest(10L, 1L, "Tanger", "Ouarzazate", 2);
        ReservationResponse response = mock(ReservationResponse.class);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(passengerRepository.findById(10L)).thenReturn(Optional.of(passenger));
        when(reservationRepository.existsByPassengerIdAndTripIdAndStatus(
            10L, 1L, ReservationStatus.CONFIRMEE)).thenReturn(false);
        when(reservationRepository.sumSeatsByTripAndStatus(1L, ReservationStatus.CONFIRMEE)).thenReturn(0);
        when(mapper.toEntity(request)).thenReturn(reservation);
        when(reservationRepository.saveAndFlush(reservation)).thenReturn(reservation);
        when(mapper.toResponse(reservation)).thenReturn(response);

        ReservationResponse result = reservationService.create(request);

        assertEquals(response, result);
        assertEquals(new BigDecimal("60.00"), reservation.getTotalAmount());
        assertEquals(ReservationStatus.CONFIRMEE, reservation.getStatus());
        assertEquals(passenger, reservation.getPassenger());
        assertEquals(pickup, reservation.getPickupPoint());
        assertEquals(dropoff, reservation.getDropoffPoint());
        verify(reservationRepository).saveAndFlush(reservation);
    }

    @Test
    void create_shouldMarkTripCompletWhenSeatsAreFull() {
        ReservationRequest request = new ReservationRequest(10L, 1L, "Tanger", "Ouarzazate", 4);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(passengerRepository.findById(10L)).thenReturn(Optional.of(passenger));
        when(reservationRepository.existsByPassengerIdAndTripIdAndStatus(
            10L, 1L, ReservationStatus.CONFIRMEE)).thenReturn(false);
        when(reservationRepository.sumSeatsByTripAndStatus(1L, ReservationStatus.CONFIRMEE)).thenReturn(0, 4);
        when(mapper.toEntity(request)).thenReturn(reservation);
        when(reservationRepository.saveAndFlush(reservation)).thenReturn(reservation);

        reservationService.create(request);

        assertEquals(TripStatus.COMPLET, trip.getStatus());
        verify(tripRepository).save(trip);
    }

    @Test
    void create_shouldThrowWhenTripNotFound() {
        ReservationRequest request = new ReservationRequest(10L, 1L, "Tanger", "Ouarzazate", 1);

        when(tripRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
            ResourceNotFoundException.class,
            () -> reservationService.create(request)
        );

        verify(reservationRepository, never()).saveAndFlush(any());
    }

    @Test
    void create_shouldThrowWhenPassengerNotFound() {
        ReservationRequest request = new ReservationRequest(10L, 1L, "Tanger", "Ouarzazate", 1);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(passengerRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(
            ResourceNotFoundException.class,
            () -> reservationService.create(request)
        );

        verify(reservationRepository, never()).saveAndFlush(any());
    }

    @Test
    void create_shouldThrowWhenTripIsNotPublished() {
        ReservationRequest request = new ReservationRequest(10L, 1L, "Tanger", "Ouarzazate", 1);
        trip.setStatus(TripStatus.ANNULE);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(passengerRepository.findById(10L)).thenReturn(Optional.of(passenger));

        assertThrows(
            BusinessRuleException.class,
            () -> reservationService.create(request)
        );

        verify(reservationRepository, never()).saveAndFlush(any());
    }

    @Test
    void create_shouldThrowWhenCityIsNotAStop() {
        ReservationRequest request = new ReservationRequest(10L, 1L, "Paris", "Ouarzazate", 1);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(passengerRepository.findById(10L)).thenReturn(Optional.of(passenger));

        assertThrows(
            ResourceNotFoundException.class,
            () -> reservationService.create(request)
        );
    }

    @Test
    void create_shouldThrowWhenStopOrderIsWrong() {
        ReservationRequest request = new ReservationRequest(10L, 1L, "Tanger", "Ouarzazate", 1);
        pickup.setStopOrder(2);
        dropoff.setStopOrder(1);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(passengerRepository.findById(10L)).thenReturn(Optional.of(passenger));

        assertThrows(
            BusinessRuleException.class,
            () -> reservationService.create(request)
        );
    }

    @Test
    void create_shouldThrowWhenPassengerAlreadyBooked() {
        ReservationRequest request = new ReservationRequest(10L, 1L, "Tanger", "Ouarzazate", 1);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(passengerRepository.findById(10L)).thenReturn(Optional.of(passenger));
        when(reservationRepository.existsByPassengerIdAndTripIdAndStatus(
            10L, 1L, ReservationStatus.CONFIRMEE)).thenReturn(true);

        assertThrows(
            BusinessRuleException.class,
            () -> reservationService.create(request)
        );

        verify(reservationRepository, never()).saveAndFlush(any());
    }

    @Test
    void create_shouldThrowWhenNotEnoughSeats() {
        ReservationRequest request = new ReservationRequest(10L, 1L, "Tanger", "Ouarzazate", 2);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(passengerRepository.findById(10L)).thenReturn(Optional.of(passenger));
        when(reservationRepository.existsByPassengerIdAndTripIdAndStatus(
            10L, 1L, ReservationStatus.CONFIRMEE)).thenReturn(false);
        when(reservationRepository.sumSeatsByTripAndStatus(1L, ReservationStatus.CONFIRMEE)).thenReturn(3);

        assertThrows(
            BusinessRuleException.class,
            () -> reservationService.create(request)
        );

        verify(reservationRepository, never()).saveAndFlush(any());
    }

    @Test
    void cancel_shouldCancelReservation() {
        trip.setStatus(TripStatus.COMPLET);

        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.sumSeatsByTripAndStatus(1L, ReservationStatus.CONFIRMEE)).thenReturn(2);

        reservationService.cancel(5L, 10L);

        assertEquals(ReservationStatus.ANNULEE, reservation.getStatus());
        assertEquals(TripStatus.PUBLIE, trip.getStatus());
        verify(reservationRepository).save(reservation);
        verify(tripRepository).save(trip);
    }

    @Test
    void cancel_shouldThrowWhenLessThan24HoursBeforeTrip() {
        trip.setDepartureTime(LocalDateTime.now().plusHours(23));

        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));

        assertThrows(
            BusinessRuleException.class,
            () -> reservationService.cancel(5L, 10L)
        );

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void cancel_shouldThrowWhenPassengerIsDifferent() {
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));

        assertThrows(
            ForbiddenActionException.class,
            () -> reservationService.cancel(5L, 99L)
        );

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void cancel_shouldThrowWhenAlreadyCancelled() {
        reservation.setStatus(ReservationStatus.ANNULEE);

        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));

        assertThrows(
            BusinessRuleException.class,
            () -> reservationService.cancel(5L, 10L)
        );

        verify(reservationRepository, never()).save(any());
    }
}
