package ma.youcode.driveSpace.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.youcode.driveSpace.dto.PassengerRequest;
import ma.youcode.driveSpace.dto.PassengerResponse;
import ma.youcode.driveSpace.service.PassengerService;

@RestController
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
public class PassengerController {

  private final PassengerService passengerService;

  @GetMapping
  public List<PassengerResponse> findAll() {
    return passengerService.findAll();
  }

  @GetMapping("/{id}")
  public PassengerResponse findById(@PathVariable Long id) {
    return passengerService.findById(id);
  }

  @PostMapping
  public ResponseEntity<PassengerResponse> create(@Valid @RequestBody PassengerRequest request) {
    PassengerResponse created = passengerService.create(request);
    return ResponseEntity.created(URI.create("/api/passengers/" + created.id())).body(created);
  }

  @PutMapping("/{id}")
  public PassengerResponse update(@PathVariable Long id, @Valid @RequestBody PassengerRequest request) {
    return passengerService.update(id, request);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    passengerService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
