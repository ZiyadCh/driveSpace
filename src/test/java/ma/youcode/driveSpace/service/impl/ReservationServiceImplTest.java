package ma.youcode.driveSpace.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import ma.youcode.driveSpace.dto.ReservationRequest;
import ma.youcode.driveSpace.dto.ReservationResponse;
import ma.youcode.driveSpace.entities.Conductor;
import ma.youcode.driveSpace.entities.Passenger;
import ma.youcode.driveSpace.entities.Reservation;
import ma.youcode.driveSpace.entities.StopPoint;
import ma.youcode.driveSpace.entities.Trip;
import ma.youcode.driveSpace.entities.Vehicle;
import ma.youcode.driveSpace.enums.ReservationStatus;
import ma.youcode.driveSpace.enums.TripStatus;
import ma.youcode.driveSpace.exception.BusinessRuleException;
import ma.youcode.driveSpace.mapper.ReservationMapper;
import ma.youcode.driveSpace.repository.PassengerRepository;
import ma.youcode.driveSpace.repository.ReservationRepository;
import ma.youcode.driveSpace.repository.TripRepository;

class ReservationServiceImplTest {

  private ReservationRepository reservationRepository;
  private TripRepository tripRepository;
  private PassengerRepository passengerRepository;

  private ReservationServiceImpl service;

  @BeforeEach
  void setUp() {
    reservationRepository = mock(ReservationRepository.class);
    tripRepository = mock(TripRepository.class);
    passengerRepository = mock(PassengerRepository.class);

    ReservationMapper mapper = Mappers.getMapper(ReservationMapper.class);

    service = new ReservationServiceImpl(
        reservationRepository,
        tripRepository,
        passengerRepository,
        mapper);
  }

  @Test
  void createCalculatesPriceFromCitiesAndMarksTripCompletWhenLastSeatsAreTaken() {
    Trip trip = buildTrip(2, LocalDateTime.now().plusDays(5));
    trip.setStopPoints(List.of(
        buildStopPoint(101L, trip, 1, "Casablanca", "40.00"),
        buildStopPoint(103L, trip, 3, "Marrakech", "120.00")));

    when(tripRepository.findById(10L)).thenReturn(Optional.of(trip));
    when(passengerRepository.findById(5L)).thenReturn(Optional.of(buildPassenger()));
    when(reservationRepository.existsByPassengerIdAndTripIdAndStatus(
        5L, 10L, ReservationStatus.CONFIRMEE)).thenReturn(false);
    when(reservationRepository.sumSeatsByTripAndStatus(10L, ReservationStatus.CONFIRMEE))
        .thenReturn(0, 2);
    when(reservationRepository.saveAndFlush(any(Reservation.class))).thenAnswer(invocation -> {
      Reservation saved = invocation.getArgument(0);
      saved.setId(1L);
      return saved;
    });

    ReservationRequest request = new ReservationRequest(5L, 10L, "Casablanca", "Marrakech", 2);
    ReservationResponse response = service.create(request);

    assertThat(response.totalAmount()).isEqualByComparingTo(new BigDecimal("160.00"));
    assertThat(response.pickupCity()).isEqualTo("Casablanca");
    assertThat(response.dropoffCity()).isEqualTo("Marrakech");
    assertThat(response.status()).isEqualTo(ReservationStatus.CONFIRMEE);
    assertThat(trip.getStatus()).isEqualTo(TripStatus.COMPLET);

    verify(reservationRepository).saveAndFlush(any(Reservation.class));
  }

  @Test
  void cancelIsRefusedLessThan24HoursBeforeDeparture() {
    Trip trip = buildTrip(3, LocalDateTime.now().plusHours(2));

    Reservation reservation = new Reservation();
    reservation.setId(1L);
    reservation.setTrip(trip);
    reservation.setPassenger(buildPassenger());
    reservation.setStatus(ReservationStatus.CONFIRMEE);

    when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

    assertThatThrownBy(() -> service.cancel(1L, 5L))
        .isInstanceOf(BusinessRuleException.class)
        .hasMessageContaining("24 hours");

    verify(reservationRepository, never()).save(any(Reservation.class));
  }

  private Trip buildTrip(int offeredSeats, LocalDateTime departureTime) {
    Trip trip = new Trip();
    trip.setId(10L);
    trip.setDepartureTime(departureTime);
    trip.setOfferedSeats(offeredSeats);
    trip.setStatus(TripStatus.PUBLIE);

    Conductor conductor = new Conductor();
    conductor.setId(1L);
    trip.setConductor(conductor);

    Vehicle vehicle = new Vehicle();
    vehicle.setId(20L);
    trip.setVehicle(vehicle);

    return trip;
  }

  private StopPoint buildStopPoint(Long id, Trip trip, int order, String city, String price) {
    StopPoint stopPoint = new StopPoint();
    stopPoint.setId(id);
    stopPoint.setTrip(trip);
    stopPoint.setStopOrder(order);
    stopPoint.setCity(city);
    stopPoint.setPriceFromDeparture(new BigDecimal(price));
    return stopPoint;
  }

  private Passenger buildPassenger() {
    Passenger passenger = new Passenger();
    passenger.setId(5L);
    return passenger;
  }
}
