
# 🎣 Bobber

**Bobber** is a self-hosted webhook ingestion, inspection, and replay service built for local development, debugging, and backend testing.

It allows developers to capture real-time webhook traffic, inspect request data with precision, and replay events to any target with full control over retries and overrides.

---

## 🚀 Why Bobber?

Debugging webhooks is often frustrating because:
- **Asynchronous Nature:** Requests happen in the background, making them hard to track.
- **Payload Integrity:** Payloads can be malformed, binary, or specific to proprietary formats.
- **Header Sensitivity:** Security signatures and custom headers are easily lost or mishandled.
- **Reproducibility:** Hard to trigger the exact same event from a third-party provider twice.
- **Unpredictable Retries:** External services retry on their own schedules, not yours.

Bobber solves this by recording incoming requests **exactly** as they arrived, providing a deterministic environment for testing and debugging.

---

## ✨ Features

### 📥 Webhook Ingestion
Create unique endpoints to receive incoming HTTP requests. Every event captures:
- **Full Context:** Method, path, query parameters, and timestamps.
- **Metadata:** Headers, Content-Type, and user agents.
- **Lossless Body:** The raw request body is stored without mutation or transformation.

### 🔍 Event Inspection
Inspect captured requests through authenticated APIs.
- Debug provider integrations (Stripe, GitHub, Shopify, etc.).
- View payload structures and check security signatures.
- Compare multiple retries from the same source.

### 🔄 Replay Engine
Replay captured events to any target URL (e.g., your local dev server) with optional overrides:
- **Headers & Query Params:** Modify or add data on the fly.
- **Body Overrides:** Fix a payload manually to test how your consumer handles it.
- **Auth Forwarding:** Opt-in to pass through original authorization tokens.

### 📈 Delivery & Retries
Track the lifecycle of every replay job:
- **Attempts:** Numbered attempts with specific status codes and durations.
- **Retry Policies:** Configure bounded retries for failed delivery attempts.
- **Auditing:** Detailed error messages for failed deliveries.

---

## 🏗️ Architecture & Concepts

[Image of a flowchart showing Webhook -> Event -> ReplayJob -> DeliveryAttempt]

| Component | Description |
| :--- | :--- |
| **Hook** | A generated endpoint with an associated secret for ingest. |
| **Event** | A single captured inbound HTTP request. |
| **ReplayJob** | A request to resend a specific Event to a target destination. |
| **DeliveryAttempt** | An individual execution of a ReplayJob, including the result. |

---

## 🛡️ Security Model
- **Public Ingest:** Endpoints are public by design to receive third-party traffic.
- **Secret Auth:** Inspection and Replay APIs require hook-specific secret authentication.
- **Transport Safety:** Unsafe transport headers are stripped during replay to prevent loops or spoofing.
- **Opt-in Forwarding:** Authorization headers are never forwarded unless explicitly requested.

---

## 🛠️ Tech Stack
- **Language:** Java
- **Framework:** Spring Boot (Spring Security, Spring Data JPA)
- **Database:** PostgreSQL
- **Migrations:** Liquibase
- **Build Tool:** Gradle

---

## 📋 Example Flow
1. **Initialize:** Create a new Hook via the API.
2. **Capture:** Point your provider (e.g., Stripe) to `/hook/{id}`.
3. **Inspect:** Fetch the `Event` list to see what arrived.
4. **Replay:** Trigger a `ReplayJob` pointing to `http://localhost:8080/api/webhook`.
5. **Monitor:** Check `DeliveryAttempts` to confirm success.

---

## 📄 License
Distributed under the MIT License. See `LICENSE` for more information.

---
**Bobber** — *Stop fishing for bugs. Catch them.*
