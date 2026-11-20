package ma.youcode.driveSpace.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.youcode.driveSpace.dto.ReservationRequest;
import ma.youcode.driveSpace.dto.ReservationResponse;
import ma.youcode.driveSpace.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  @PostMapping
  public ResponseEntity<ReservationResponse> create(@Valid @RequestBody ReservationRequest request) {
    ReservationResponse created = reservationService.create(request);
    return ResponseEntity.created(URI.create("/api/reservations/" + created.id())).body(created);
  }

  @GetMapping("/{id}")
  public ReservationResponse findById(@PathVariable Long id) {
    return reservationService.findById(id);
  }

  @GetMapping("/passenger/{passengerId}")
  public List<ReservationResponse> findByPassenger(@PathVariable Long passengerId) {
    return reservationService.findByPassenger(passengerId);
  }

  @PatchMapping("/{id}/cancel")
  public ResponseEntity<Void> cancel(@PathVariable Long id, @RequestParam Long passengerId) {
    reservationService.cancel(id, passengerId);
    return ResponseEntity.noContent().build();
  }
}
