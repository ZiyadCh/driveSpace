package ma.youcode.driveSpace.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
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
  public String createTrip(@RequestBody String entity) {

    return entity;
  }

}
