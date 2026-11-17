package com.portready.service;
import com.portready.model.Models.*;
import com.portready.repository.VesselRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.*;
import java.util.List;
class ReadinessServiceTest {
 private final Clock clock=Clock.fixed(Instant.parse("2026-10-28T12:00:00Z"),ZoneOffset.UTC);
 @Test void highPriorityHasTraceableReasons() {
  VesselRepository repo=mock(VesselRepository.class);
  Vessel v=new Vessel(1,"Demo Ship","DEMO-1","Port",LocalDate.of(2026,10,31));
  when(repo.certificates(1)).thenReturn(List.of(new Certificate(1,1,"Safety Certificate",LocalDate.of(2026,10,30))));
  when(repo.findings(1)).thenReturn(List.of(new Finding(1,1,"Open observation","OPEN")));
  Readiness r=new ReadinessService(repo,clock).assess(v);
  assertEquals(75,r.score()); assertEquals("HIGH",r.priority()); assertEquals(3,r.daysToArrival()); assertEquals(3,r.reasons().size());
 }
 @Test void resolvedFindingsDoNotRaisePriority() {
  VesselRepository repo=mock(VesselRepository.class);
  Vessel v=new Vessel(2,"Demo Ship","DEMO-2","Port",LocalDate.of(2026,11,15));
  when(repo.certificates(2)).thenReturn(List.of());
  when(repo.findings(2)).thenReturn(List.of(new Finding(2,2,"Closed observation","RESOLVED")));
  Readiness r=new ReadinessService(repo,clock).assess(v);
  assertEquals(0,r.score()); assertEquals("LOW",r.priority()); assertEquals(0,r.openFindings());
 }
 @Test void pointsAreCapped() {
  VesselRepository repo=mock(VesselRepository.class);
  Vessel v=new Vessel(3,"Demo Ship","DEMO-3","Port",LocalDate.of(2026,10,29));
  when(repo.certificates(3)).thenReturn(List.of(new Certificate(3,3,"Certificate",LocalDate.of(2026,10,28))));
  when(repo.findings(3)).thenReturn(List.of(new Finding(1,3,"A","OPEN"),new Finding(2,3,"B","OPEN"),new Finding(3,3,"C","OPEN")));
  Readiness r=new ReadinessService(repo,clock).assess(v);
  assertEquals(95,r.score());
 }
}
