package com.portready.controller;
import com.portready.model.Models.*;
import com.portready.service.ReadinessService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class VesselController {
 private final ReadinessService service;
 public VesselController(ReadinessService service) { this.service=service; }
 @GetMapping("/vessels") public List<VesselSummary> fleet() { return service.fleet(); }
 @GetMapping("/vessels/{id}/readiness") public Readiness readiness(@PathVariable long id) { return service.readiness(id); }
 @GetMapping("/vessels/{id}/certificates") public List<Certificate> certificates(@PathVariable long id) { return service.certificates(id); }
 @GetMapping("/vessels/{id}/recommendations") public Recommendations recommendations(@PathVariable long id) { return service.recommendations(id); }
 @PatchMapping("/findings/{id}") public Finding changeFinding(@PathVariable long id,@Valid @RequestBody UpdateFindingRequest body) {
  return service.changeFinding(id,body.status());
 }
}
