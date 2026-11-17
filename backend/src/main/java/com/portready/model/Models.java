package com.portready.model;
import java.time.LocalDate;
import java.util.List;
public final class Models {
 private Models() {}
 public record Vessel(long id, String name, String imoNumber, String destinationPort, LocalDate arrivalDate) {}
 public record Certificate(long id, long vesselId, String certificateType, LocalDate expiryDate) {}
 public record Finding(long id, long vesselId, String description, String status) {}
 public record VesselSummary(long id, String name, String imoNumber, String destinationPort, LocalDate arrivalDate, int score, String priority, int openFindings) {}
 public record Readiness(long vesselId, String vesselName, int score, String priority, long daysToArrival, int openFindings, List<String> reasons, List<Certificate> certificates, List<Finding> findings) {}
 public record Recommendations(long vesselId, List<String> actions) {}
 public record UpdateFindingRequest(@jakarta.validation.constraints.NotNull Status status) {}
 public enum Status { OPEN, RESOLVED }
}
