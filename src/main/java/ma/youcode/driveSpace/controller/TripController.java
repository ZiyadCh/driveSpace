package ma.youcode.driveSpace.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
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

  @PostMapping()
  public ResponseEntity<TripResponse> createTrip(@Valid @RequestBody TripRequest tripRequest) {
    TripResponse created = tripService.create(tripRequest);
    return ResponseEntity.created(URI.create("api/trip" + created.id())).body(created);
  }

}
