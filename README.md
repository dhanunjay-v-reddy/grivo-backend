# Grivo Backend — Rental Move-In/Move-Out Evidence & Deposit Dispute Platform

Spring Boot REST API helping Indian tenants and landlords document rental
property condition with tamper-evident photo evidence, resolve deposit
disputes, and generate a ready-to-file complaint letter when needed.

## Why this exists

Security deposit disputes are reportedly the most common tenant complaint
in India, and the usual advice everywhere is entirely manual: "take your
own photos," "send a written demand," "go to consumer court." Grivo is
the actual tool for that first, most important step — provable evidence,
captured at the right time, that can't be quietly edited later.

Built as a deliberate adaptation of an existing US app category (RentProof,
RentCheck, etc. — same core workflow: photo evidence → comparison → report),
localized for India's realities: informal agreements, WhatsApp-negotiated
terms, and a legal-complaint-assistant feature the US apps don't have.

## What's built

- **Auth** — JWT-based, role-based (TENANT / LANDLORD / ADMIN)
- **Properties & Agreements** — landlords create properties, link tenants
  via email, record rent/deposit/move-in terms
- **Photo evidence** — real Cloudinary storage (not just a hash like an
  earlier project of mine skipped) + SHA-256 hash computed at capture time,
  so any later tampering with the image is detectable
- **Dispute workflow** — tenant raises a dispute, landlord can respond,
  either side can request a full PDF evidence report
- **PDF report generation** (OpenPDF) — bundles agreement details, dispute
  description, and every move-in/move-out photo with its hash, embedded
  directly in the document
- **India-specific complaint letter generator** — auto-fills a formal
  demand letter using the agreement's real details, pointing to the
  Consumer Protection Act, 2019 / District Consumer Disputes Redressal
  Commission as the nationally-valid route (deliberately avoids unverified
  state-specific tenancy-law claims — see `ComplaintTemplateService` for
  the reasoning)
- **Tests** — `HashUtilTest` verifies the actual tamper-evidence guarantee
  the whole photo feature depends on (same input → same hash, different
  input → different hash)

## Not built yet

- Frontend (React, same as before — next step)
- Admin role isn't wired into any real workflow yet (just exists as an enum value)
- No email notifications (e.g., tenant invited to an agreement doesn't get emailed)
- No rate limiting on photo uploads
- Deployment (Render backend + Vercel frontend + Neon DB — planned, same
  stack as before)

## Running locally

1. Create a local Postgres DB `grivo_db`, or point `application.properties`
   at a cloud one via environment variables
2. Get a free Cloudinary account (cloudinary.com) and set
   `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`
   as environment variables
3. `mvn spring-boot:run`

## Legal/liability note

The generated complaint letter is explicitly labeled informational, not
legal advice, both in the code comments and in the letter itself. This
is intentional — giving specific state-by-state legal citations without
verifying each one against current law would be both inaccurate and a
real liability risk. Worth keeping that framing if this ever gets
extended.
