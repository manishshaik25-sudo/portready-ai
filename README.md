# PortReady AI — Maritime Readiness Workflow Prototype

**Independent educational proof of concept.** Not affiliated with LISCR. All vessels, ports, inspection findings, rules, and scores are fictional. **Not** an official maritime compliance determination. It does not claim to reproduce LISCR's Dynamic Prevention Program.

## What it does

- Displays a small fleet with upcoming arrivals, certificates and open findings.
- Applies **explainable, deterministic demo rules** to suggest a *workflow priority*, not a vessel safety/compliance score.
- Shows why each vessel was prioritized, and suggested follow-up actions.
- Lets you resolve/reopen a demo inspection finding; calculations update immediately.
- Exposes five REST endpoints and persists demo records in an in-memory H2 SQL database.
- Provides a responsive React/Vite dashboard. No login, external APIs, AI key or proprietary data required.

## Prerequisites

Java **21**, Maven **3.9+**, Node.js **20+**, npm. On Windows, open two PowerShell terminals.

## Start the backend (terminal 1)

```powershell
cd backend
mvn spring-boot:run
```

Backend: http://localhost:8080 ; H2 console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:portready`, username `sa`, empty password).

## Start the frontend (terminal 2)

```powershell
cd frontend
npm install
npm run dev
```

Open http://localhost:5173 . Vite proxies `/api` to `http://localhost:8080`.

### Quick API checks

```powershell
Invoke-RestMethod http://localhost:8080/api/vessels
Invoke-RestMethod http://localhost:8080/api/vessels/1/readiness
Invoke-RestMethod http://localhost:8080/api/vessels/1/certificates
Invoke-RestMethod http://localhost:8080/api/vessels/1/recommendations
Invoke-RestMethod -Method Patch -Uri http://localhost:8080/api/findings/1 -ContentType 'application/json' -Body '{"status":"RESOLVED"}'
```

The H2 database resets on backend restart, letting you repeat the demo.

## Backend endpoints

| Endpoint | Description |
| --- | --- |
| GET `/api/vessels` | Fleet list with calculated priorities |
| GET `/api/vessels/{id}/readiness` | Per-vessel transparent priority assessment |
| GET `/api/vessels/{id}/certificates` | Certificates and expiry dates |
| GET `/api/vessels/{id}/recommendations` | Rule-generated tasks |
| PATCH `/api/findings/{id}` | Set inspection finding `OPEN` or `RESOLVED` |

## How to demonstrate it in an interview

1. Open the dashboard and select **Atlantic Pioneer** (fictional vessel).
2. Explain the arrival window, certificate warning, and two open findings.
3. Read the reason breakdown and action recommendations.
4. Select **Resolve** for one finding and observe the updated assessment.
5. Explain how the frontend calls the REST API and how `JdbcTemplate` reads/writes H2.
6. State clearly that the scores are **illustrative workflow triage**, not real inspection-risk models.

## Architecture

`React UI → /api REST controller → ReadinessService → VesselRepository (JdbcTemplate) → H2 SQL`

There is no LLM in this MVP. The term "AI" in the conceptual name is aspirational: **the current implementation is rule-based**, which makes its outputs testable and easy to justify. Optional future extensions: approved document ingestion, retrieval-assisted explanations, audit trails, authentication, real domain validation and human review. Do not suggest autonomous compliance judgments.

## Testing and learning

```powershell
cd backend
mvn test
```

Read `LEARNING_GUIDE.md` for a file-by-file walkthrough, code reading order, interview talking points, and exercises. The unit tests cover risk boundary behavior and the API integration test covers state changes.

## Troubleshooting

- **Port 8080 busy:** stop the other Java app, or set `SERVER_PORT=8081` and update `frontend/vite.config.js` proxy target.
- **`mvn` not recognized:** install Maven and add its `bin` folder to PATH; restart PowerShell.
- **CORS/network error:** make sure the backend is running and that Vite's `/api` proxy points to it.
- **Node version:** use Node 20 or newer.
- **Data disappears:** expected; demo uses in-memory H2.

## License

MIT. This independent portfolio/demo project does not use LISCR branding, systems, or confidential information.
