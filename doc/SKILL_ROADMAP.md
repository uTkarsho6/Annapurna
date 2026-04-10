# Smart Food Waste Management System — Skill Activation Roadmap
This document serves as your playbook for when and how to call specific AI skills during the development of this mini-project.

**How to use this:**
Whenever you are ready to start a new phase, simply prompt me (the AI) with the exact text provided in the **"How to call it"** column. I will automatically load the skill and start working on those tasks.

---

## Phase 1: Project Setup & Architecture
**Goal:** Initialize the project, set up Firebase, and configure the base security rules.

| Skill to Call | When to Call It | What it will do | How to call it (Prompt to AI) |
| :--- | :--- | :--- | :--- |
| `@gsd-security-auditor` | Right after creating the Firebase project. | Sets up strict Firestore database rules (SEC-DATA-01) and custom claims for role-based access. | *"Load the `@gsd-security-auditor` skill and let's set up the initial Firestore Security Rules based on my SECURITY.md file."* |
| `@firebase` | Setting up the NoSQL schema and Custom Claims. | Helps define the NoSQL collections (`users`, `donations`, `claims`) efficiently. | *"Load the `@firebase` skill. Let's design the Firestore schema for the donations and users collections."* |

---

## Phase 2, 3, & 4: Core Modules (Auth, Donor, Receiver)
**Goal:** Build the Android views, wire up Logic, and implement the atomic donation claiming.

| Skill to Call | When to Call It | What it will do | How to call it (Prompt to AI) |
| :--- | :--- | :--- | :--- |
| `@gsd-executor` | When building the standard Android UI and logic. | Quickly grinds through creating your Android `Activities`, `RecyclerViews`, and layout XMLs based on the TRD. | *"Load the `@gsd-executor` skill. Let's execute phase 3 (Donor Module) from the TRD."* |
| `@firebase` | When implementing the 'Claim Donation' button. | Writes the complex `FirebaseTransaction` to prevent race conditions when two NGOs click claim at the same time. | *"Load the `@firebase` skill. Help me write the Firestore Transaction for the 'Claim Donation' logic (T-32)."* |
| `@java-pro` | When writing FusedLocation logic or Listeners. | Ensures the Java Android code is clean, doesn't leak memory, and handles GPS cleanly. | *"Load the `@java-pro` skill. Let's write the Java logic for fetching the Donor's GPS location."* |

---

## Phase 5 & 6: Maps, Notifications & Cloud Functions
**Goal:** Add Google Maps markers and write backend push notifications.

| Skill to Call | When to Call It | What it will do | How to call it (Prompt to AI) |
| :--- | :--- | :--- | :--- |
| `@gsd-executor` | When implementing the Map View. | Sets up the `SupportMapFragment`, handles map permissions, and places markers. | *"Load the `@gsd-executor` skill. Let's implement the Map View and place markers for available donations."* |
| `@firebase` | When setting up Push Notifications (FCM). | Writes the Node.js Cloud Functions that trigger when a new donation is posted or claimed. | *"Load the `@firebase` skill. Let's write the Firebase Cloud Functions to send FCM push notifications."* |

---

## Phase 8 & 9: Polish, Security & Testing
**Goal:** Secure the Android app (SharedPrefs, API Keys) and test the flow.

| Skill to Call | When to Call It | What it will do | How to call it (Prompt to AI) |
| :--- | :--- | :--- | :--- |
| `@mobile-security-coder` | When polishing the Android app client. | Sets up `EncryptedSharedPreferences`, disables cleartext traffic, and hides your Google Maps API key out of the `AndroidManifest.xml`. | *"Load the `@mobile-security-coder` skill. Let's implement the client-side security fixes from SEC-APP-01 and SEC-NET-01."* |
| `@gsd-verifier` | When preparing for your final college submission. | Writes the Espresso UI flows and Mockito tests to prove the app is stable. | *"Load the `@gsd-verifier` skill. Let's write Espresso UI tests for the Donor posting flow."* |

---

### Pro-Tip for the Developer (You):
You don't need to load multiple skills at once. We will tackle this step-by-step. When you sit down to work on a specific feature, just check this roadmap, copy the prompt, and we'll get it done!
