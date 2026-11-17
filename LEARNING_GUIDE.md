# Study guide — understand every part

## 1. The business story

A fictional fleet operator has several vessels scheduled to arrive at ports. Their compliance team needs a quick **workflow queue** showing which certificate dates and unresolved inspection findings deserve review. We do not determine whether a vessel is legally permitted to sail. We do not replicate a commercial flag state's risk assessment.

## 2. Suggested reading order

1. `backend/src/main/resources/schema.sql`: three database tables and foreign keys.
2. `backend/src/main/resources/data.sql`: synthetic sample records.
3. `backend/src/main/java/com/portready/model/*.java`: immutable response/request types.
4. `backend/src/main/java/com/portready/repository/VesselRepository.java`: parameterized SQL with `JdbcTemplate`.
5. `backend/src/main/java/com/portready/service/ReadinessService.java`: business rules and human-readable reasons.
6. `backend/src/main/java/com/portready/controller/VesselController.java`: REST routes, request validation, error responses.
7. `frontend/src/App.jsx`: UI state, API fetch, selection, and finding changes.
8. `frontend/src/styles.css`: styles and responsive layout.
9. `backend/src/test`: unit tests and integration test.

## 3. Demo workflow

- A vessel's upcoming port arrival is stored in `vessels.arrival_date`.
- Its certificates are in `certificates` with expiration dates.
- Its inspection findings are in `findings`, each `OPEN` or `RESOLVED`.
- Backend compares certificate expiry dates to arrival, counts open findings, and returns a priority plus reasons.
- Frontend renders the response and lets a reviewer update a finding.
- Backend saves the change with `UPDATE ... WHERE id = ?` and recalculates when frontend refetches.

## 4. Understand the example rules

**Demo workflow points, not scientific or official scores:**
- +45 if a certificate expires before arrival.
- Otherwise +25 if a certificate expires within 30 days after arrival.
- +20 per unresolved finding, capped at +40.
- +10 if arrival is within 3 days and there is any certificate/finding issue.
- Total capped at 100.
- 60+ HIGH, 30–59 MEDIUM, under 30 LOW.

Points summarize *workflow attention*; they are not port-state-control probabilities, inspection predictions, or safety ratings.

## 5. How REST works

For `GET /api/vessels/1/readiness` the controller receives `1`, calls the service, which loads rows via repository SQL and computes the response. Jackson serializes the Java record to JSON. The frontend reads it using `fetch()`.

`PATCH /api/findings/1` accepts JSON `{ "status": "RESOLVED" }`. The controller validates the enum, the repository updates H2, and the UI reloads the visible vessel. No real operation is performed outside this local demo.

## 6. Questions an interviewer may ask

**Why JDBC rather than JPA?** `JdbcTemplate` makes SQL queries explicit and lets me show understanding of joins, parameterized statements, and persistence. At a larger scale I'd evaluate JPA or another persistence approach based on team standards.

**Why not use AI for scoring?** Rules are deterministic and explainable. Any LLM should only help draft summaries from trusted structured facts, and a person should verify compliance-related claims.

**What would production require?** Authentication/RBAC, audit logs, fine-grained authorization, quality-controlled real inputs, deadlines and timezone rules, vessel-specific certificate applicability, monitoring, security testing, error handling, transactional workflows, and approved maritime domain validation.

**What is the enhancement relative to existing maritime systems?** The concept explores a unified review-and-action workflow, rather than claiming an existing registry lacks vessel risk monitoring or pre-arrival guidance.

**How would I measure success?** Review time per vessel, percentage of overdue actions, accuracy of input data, adoption, and time to close findings. These are proposed metrics, not measured results.

## 7. Practice modifications

1. Add `priority_reason` filters to `GET /api/vessels`.
2. Add a fourth synthetic vessel and verify the new score.
3. Add an API endpoint that reopens an inspection finding.
4. Replace H2 with PostgreSQL (maintain schema and prepared statements).
5. Add an audit table recording status changes and a test for it.
6. Implement pagination, input validation, and a simple demo login.
7. Make `ReadinessService` configurable without turning the scores into real compliance determinations.

## 8. Presentation script (60 seconds)

"This is PortReady AI, an independent educational prototype showing a potential vessel-readiness workflow. It combines certificate expiry, unresolved findings, and upcoming arrivals into a transparent review queue. A user can open a vessel, see why it was flagged, and update a finding; the assessment immediately refreshes. I built a Java Spring Boot REST API, JDBC persistence with H2, and a React dashboard. The demo uses synthetic data and illustrative rules, not LISCR internal systems or an official compliance model. My proposed next step would be to validate actual operational requirements with domain experts before implementing a production version."
