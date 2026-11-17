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
import ma.youcode.driveSpace.dto.ConductorRequest;
import ma.youcode.driveSpace.dto.ConductorResponse;
import ma.youcode.driveSpace.service.ConductorService;

@RestController
@RequestMapping("/api/conductors")
@RequiredArgsConstructor
public class ConductorController {

  private final ConductorService conductorService;

  @GetMapping
  public List<ConductorResponse> findAll() {
    return conductorService.findAll();
  }

  @GetMapping("/{id}")
  public ConductorResponse findById(@PathVariable Long id) {
    return conductorService.findById(id);
  }

  @PostMapping
  public ResponseEntity<ConductorResponse> create(@Valid @RequestBody ConductorRequest request) {
    ConductorResponse created = conductorService.create(request);
    return ResponseEntity.created(URI.create("/api/conductors/" + created.id())).body(created);
  }

  @PutMapping("/{id}")
  public ConductorResponse update(@PathVariable Long id, @Valid @RequestBody ConductorRequest request) {
    return conductorService.update(id, request);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    conductorService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
