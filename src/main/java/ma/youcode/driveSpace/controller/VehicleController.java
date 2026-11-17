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
import ma.youcode.driveSpace.dto.VehicleRequest;
import ma.youcode.driveSpace.dto.VehicleResponse;
import ma.youcode.driveSpace.service.VehicleService;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

  private final VehicleService vehicleService;

  @GetMapping
  public List<VehicleResponse> findAll() {
    return vehicleService.findAll();
  }

  @GetMapping("/{id}")
  public VehicleResponse findById(@PathVariable Long id) {
    return vehicleService.findById(id);
  }

  @PostMapping
  public ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleRequest request) {
    VehicleResponse created = vehicleService.create(request);
    return ResponseEntity.created(URI.create("/api/vehicles/" + created.id())).body(created);
  }

  @PutMapping("/{id}")
  public VehicleResponse update(@PathVariable Long id, @Valid @RequestBody VehicleRequest request) {
    return vehicleService.update(id, request);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    vehicleService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
