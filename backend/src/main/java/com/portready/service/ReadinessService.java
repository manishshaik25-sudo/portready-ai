
package com.portready.service;

import com.portready.model.Models.*;
import com.portready.repository.VesselRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReadinessService {

    private final VesselRepository repository;
    private final Clock clock;

    // Production constructor used by Spring Boot
    @Autowired
    public ReadinessService(VesselRepository repository) {
        this(repository, Clock.systemDefaultZone());
    }

    // Package-private constructor for deterministic unit tests
    ReadinessService(VesselRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    private Vessel getVessel(long id) {
        return repository.vesselById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Unknown vessel"
                ));
    }

    // Retrieve all vessels with their readiness summaries
    public List<VesselSummary> fleet() {

        return repository.allVessels()
                .stream()
                .map(vessel -> {

                    Readiness readiness = assess(vessel);

                    return new VesselSummary(
                            vessel.id(),
                            vessel.name(),
                            vessel.imoNumber(),
                            vessel.destinationPort(),
                            vessel.arrivalDate(),
                            readiness.score(),
                            readiness.priority(),
                            readiness.openFindings()
                    );
                })
                .toList();
    }

    // Retrieve readiness assessment for a vessel
    public Readiness readiness(long id) {
        return assess(getVessel(id));
    }

    // Retrieve certificates for a vessel
    public List<Certificate> certificates(long id) {

        getVessel(id);

        return repository.certificates(id);
    }

    // Generate recommendations based on certificates and findings
    public Recommendations recommendations(long id) {

        Readiness readiness = readiness(id);

        List<String> actions = new ArrayList<>();

        LocalDate arrival = getVessel(id).arrivalDate();

        // Check certificate expiration
        for (Certificate certificate : readiness.certificates()) {

            if (certificate.expiryDate().isBefore(arrival)) {

                actions.add(
                        "Request human review: "
                                + certificate.certificateType()
                                + " expires before estimated arrival."
                );

            } else if (!certificate.expiryDate()
                    .isAfter(arrival.plusDays(30))) {

                actions.add(
                        "Check renewal schedule: "
                                + certificate.certificateType()
                                + " expires within 30 days after arrival."
                );
            }
        }

        // Check unresolved inspection findings
        for (Finding finding : readiness.findings()) {

            if (finding.status().equals("OPEN")) {

                actions.add(
                        "Review and document closure: "
                                + finding.description()
                );
            }
        }

        // Default recommendation
        if (actions.isEmpty()) {

            actions.add(
                    "Continue routine document and inspection "
                            + "record reviews before arrival."
            );
        }

        actions.add(
                "Confirm applicable requirements with an authorized "
                        + "maritime professional; these demo rules "
                        + "are not a compliance decision."
        );

        return new Recommendations(id, actions);
    }

    // Update an inspection finding status
    public Finding changeFinding(long findingId, Status status) {

        Finding existing = repository.findingById(findingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Unknown finding"
                ));

        repository.updateFinding(findingId, status);

        return new Finding(
                existing.id(),
                existing.vesselId(),
                existing.description(),
                status.name()
        );
    }

    // Calculate vessel readiness score and priority
    Readiness assess(Vessel vessel) {

        List<Certificate> certificates =
                repository.certificates(vessel.id());

        List<Finding> findings =
                repository.findings(vessel.id());

        LocalDate today = LocalDate.now(clock);

        long arrivalDays = ChronoUnit.DAYS.between(
                today,
                vessel.arrivalDate()
        );

        // Count unresolved findings
        long openFindings = findings.stream()
                .filter(finding -> finding.status().equals("OPEN"))
                .count();

        // Check if any certificate expires before arrival
        boolean expiredAtArrival = certificates.stream()
                .anyMatch(certificate ->
                        certificate.expiryDate()
                                .isBefore(vessel.arrivalDate())
                );

        // Check certificates expiring within 30 days after arrival
        boolean soonAfterArrival = certificates.stream()
                .anyMatch(certificate ->
                        !certificate.expiryDate()
                                .isBefore(vessel.arrivalDate())
                                &&
                        !certificate.expiryDate()
                                .isAfter(vessel.arrivalDate().plusDays(30))
                );

        int score = 0;

        List<String> reasons = new ArrayList<>();

        // Rule 1: Certificate expiration
        if (expiredAtArrival) {

            score += 45;

            reasons.add(
                    "At least one certificate expires before "
                            + "estimated arrival (+45)."
            );

        } else if (soonAfterArrival) {

            score += 25;

            reasons.add(
                    "A certificate expires within 30 days "
                            + "after arrival (+25)."
            );
        }

        // Rule 2: Unresolved findings
        if (openFindings > 0) {

            int findingPoints = (int) Math.min(
                    openFindings * 20,
                    40
            );

            score += findingPoints;

            reasons.add(
                    openFindings
                            + " unresolved finding(s) (+"
                            + findingPoints
                            + ")."
            );
        }

        // Rule 3: Upcoming arrival with unresolved issues
        if (arrivalDays >= 0
                && arrivalDays <= 3
                && (expiredAtArrival || soonAfterArrival || openFindings > 0)) {

            score += 10;

            reasons.add(
                    "Arrival in 3 days or less, "
                            + "with an identified issue (+10)."
            );
        }

        // No risk conditions identified
        if (reasons.isEmpty()) {

            reasons.add(
                    "No demo rule triggered; continue routine review."
            );
        }

        // Ensure maximum score is 100
        score = Math.min(score, 100);

        // Determine priority level
        String priority;

        if (score >= 60) {
            priority = "HIGH";
        } else if (score >= 30) {
            priority = "MEDIUM";
        } else {
            priority = "LOW";
        }

        return new Readiness(
                vessel.id(),
                vessel.name(),
                score,
                priority,
                arrivalDays,
                (int) openFindings,
                List.copyOf(reasons),
                certificates,
                findings
        );
    }
}
