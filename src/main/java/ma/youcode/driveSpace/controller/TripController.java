package ma.youcode.driveSpace.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.youcode.driveSpace.dto.TripRequest;
import ma.youcode.driveSpace.dto.TripResponse;
import ma.youcode.driveSpace.service.TripService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/trip")
@RequiredArgsConstructor
public class TripController {

  private final TripService tripService;

  @GetMapping
  public List<TripResponse> findAll() {
    return tripService.findAll();
  }

  @GetMapping("/{id}")
  public TripResponse findById(@PathVariable Long id) {
    return tripService.findById(id);
  }

  @GetMapping("/search")
  public Page<TripResponse> searchTrips(@RequestParam String ville_depart, @RequestParam String ville_arrive,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam LocalDateTime date) {
    return tripService.search(ville_depart, ville_arrive, date, PageRequest.of(page, size));
  }

  @PostMapping()
  public ResponseEntity<TripResponse> createTrip(@Valid @RequestBody TripRequest tripRequest) {
    TripResponse created = tripService.create(tripRequest);
    return ResponseEntity.created(URI.create("api/trip" + created.id())).body(created);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    tripService.delete(id);
    return ResponseEntity.noContent().build();
  }

}
