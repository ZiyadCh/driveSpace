package ma.youcode.driveSpace.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ma.youcode.driveSpace.dto.ReservationRequest;
import ma.youcode.driveSpace.dto.ReservationResponse;
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
import ma.youcode.driveSpace.repository.PassengerRepository;
import ma.youcode.driveSpace.repository.ReservationRepository;
import ma.youcode.driveSpace.repository.TripRepository;
import ma.youcode.driveSpace.service.ReservationService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

  private final ReservationRepository reservationRepository;
  private final TripRepository tripRepository;
  private final PassengerRepository passengerRepository;
  private final ReservationMapper mapper;

  @Override
  @Transactional
  public ReservationResponse create(ReservationRequest request) {
    Trip trip = tripRepository.findById(request.tripId())
        .orElseThrow(() -> new ResourceNotFoundException("Trip " + request.tripId() + " not found"));

    Passenger passenger = passengerRepository.findById(request.passengerId())
        .orElseThrow(() -> new ResourceNotFoundException("Passenger " + request.passengerId() + " not found"));

    checkTripCanBeBooked(trip);

    StopPoint pickup = findStopPoint(trip, request.pickupCity());
    StopPoint dropoff = findStopPoint(trip, request.dropoffCity());
    checkStopOrder(pickup, dropoff);
    checkPassengerHasNoConfirmedReservation(passenger, trip);
    checkSeatsAreAvailable(trip, request.seats());

    Reservation reservation = makeReservation(request, passenger, trip, pickup, dropoff);
    Reservation savedReservation = reservationRepository.saveAndFlush(reservation);

    updateTripStatusAfterBooking(trip);
    return mapper.toResponse(savedReservation);
  }

  @Override
  public List<ReservationResponse> findAll() {
    return mapper.toResponseList(reservationRepository.findAll());
  }

  @Override
  public ReservationResponse findById(Long id) {
    Reservation reservation = reservationRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Reservation " + id + " not found"));

    return mapper.toResponse(reservation);
  }

  @Override
  public List<ReservationResponse> findByPassenger(Long passengerId) {
    if (!passengerRepository.existsById(passengerId)) {
      throw new ResourceNotFoundException("Passenger " + passengerId + " not found");
    }

    return mapper.toResponseList(
        reservationRepository.findByPassengerIdOrderByReservedAtDesc(passengerId));
  }

  @Override
  public List<ReservationResponse> findByTripForConductor(Long tripId, Long conductorId) {
    Trip trip = tripRepository.findById(tripId)
        .orElseThrow(() -> new ResourceNotFoundException("Trip " + tripId + " not found"));

    if (!trip.getConductor().getId().equals(conductorId)) {
      throw new ForbiddenActionException("You can only see reservations of your own trips.");
    }

    return mapper.toResponseList(reservationRepository.findByTripIdOrderByReservedAtDesc(tripId));
  }

  @Override
  @Transactional
  public void cancel(Long reservationId, Long passengerId) {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new ResourceNotFoundException("Reservation " + reservationId + " not found"));

    Trip trip = reservation.getTrip();

    if (!reservation.getPassenger().getId().equals(passengerId)) {
      throw new ForbiddenActionException("You can only cancel your own reservations.");
    }

    if (LocalDateTime.now().isAfter(trip.getDepartureTime().minusHours(24))) {
      throw new BusinessRuleException("You can't cancel before 24 hours before the trip");
    }

    if (reservation.getStatus() != ReservationStatus.CONFIRMEE) {
      throw new BusinessRuleException("This reservation is already cancelled.");
    }

    reservation.setStatus(ReservationStatus.ANNULEE);
    reservationRepository.save(reservation);

    updateTripStatusAfterCancellation(trip);
  }

  private void checkTripCanBeBooked(Trip trip) {
    if (trip.getStatus() != TripStatus.PUBLIE) {
      throw new BusinessRuleException("Only published trips can be booked.");
    }
  }

  private StopPoint findStopPoint(Trip trip, String city) {
    return trip.getStopPoints().stream()
        .filter(stopPoint -> stopPoint.getCity().equalsIgnoreCase(city))
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException(
            "City '" + city + "' is not a stop on trip " + trip.getId()));
  }

  private void checkStopOrder(StopPoint pickup, StopPoint dropoff) {
    if (pickup.getStopOrder() >= dropoff.getStopOrder()) {
      throw new BusinessRuleException("The pick-up city must be before the drop-off city.");
    }
  }

  private void checkPassengerHasNoConfirmedReservation(Passenger passenger, Trip trip) {
    boolean alreadyBooked = reservationRepository.existsByPassengerIdAndTripIdAndStatus(
        passenger.getId(), trip.getId(), ReservationStatus.CONFIRMEE);

    if (alreadyBooked) {
      throw new BusinessRuleException("This passenger already has a confirmed reservation on this trip.");
    }
  }

  private void checkSeatsAreAvailable(Trip trip, int requestedSeats) {
    int reservedSeats = reservationRepository.sumSeatsByTripAndStatus(
        trip.getId(), ReservationStatus.CONFIRMEE);
    int remainingSeats = trip.getOfferedSeats() - reservedSeats;

    if (requestedSeats > remainingSeats) {
      throw new BusinessRuleException("Only " + remainingSeats + " seats are available on this trip.");
    }
  }

  private Reservation makeReservation(ReservationRequest request, Passenger passenger, Trip trip,
      StopPoint pickup, StopPoint dropoff) {
    Reservation reservation = mapper.toEntity(request);
    reservation.setPassenger(passenger);
    reservation.setTrip(trip);
    reservation.setPickupPoint(pickup);
    reservation.setDropoffPoint(dropoff);
    reservation.setTotalAmount(calculateTotalAmount(pickup, dropoff, request.seats()));
    reservation.setReservedAt(LocalDateTime.now());
    reservation.setStatus(ReservationStatus.CONFIRMEE);
    return reservation;
  }

  private BigDecimal calculateTotalAmount(StopPoint pickup, StopPoint dropoff, int seats) {
    return dropoff.getPriceFromDeparture()
        .subtract(pickup.getPriceFromDeparture())
        .multiply(BigDecimal.valueOf(seats))
        .setScale(2, RoundingMode.HALF_UP);
  }

  private void updateTripStatusAfterBooking(Trip trip) {
    int reservedSeats = reservationRepository.sumSeatsByTripAndStatus(
        trip.getId(), ReservationStatus.CONFIRMEE);

    if (reservedSeats >= trip.getOfferedSeats()) {
      trip.setStatus(TripStatus.COMPLET);
      tripRepository.save(trip);
    }
  }

  private void updateTripStatusAfterCancellation(Trip trip) {
    int reservedSeats = reservationRepository.sumSeatsByTripAndStatus(
        trip.getId(), ReservationStatus.CONFIRMEE);

    if (trip.getStatus() == TripStatus.COMPLET && reservedSeats < trip.getOfferedSeats()) {
      trip.setStatus(TripStatus.PUBLIE);
      tripRepository.save(trip);
    }
  }
}
