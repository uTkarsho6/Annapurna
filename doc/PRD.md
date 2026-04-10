


PRODUCT REQUIREMENTS DOCUMENT
Smart Food Waste Management System
Connecting Restaurants, Hotels & NGOs
Version 1.0  |  Academic Year 2026-27
Walchand College of Engineering, Sangli
Department of Information Technology




1. Product Overview
The Smart Food Waste Management System is an Android application that acts as a real-time bridge between food donors (restaurants, hotels, banquet halls) and food recipients (NGOs, orphanages). The platform addresses two parallel problems in developing nations: food wastage by commercial establishments and food scarcity among underprivileged communities.

Attribute	Details
Project Name	Smart Food Waste Management System
Platform	Android (Mobile Application)
Document Type	Product Requirements Document (PRD)
Version	1.0
Team	Utkarsh Patil, Ganesh Mirgane, Anushka Patil
Guide	Smt. Bharati S. Shetty
Institution	Walchand College of Engineering, Sangli
Academic Year	2026-27

2. Problem Statement
In developing nations, tons of surplus food from restaurants, hotels, and catering services are discarded daily while a significant population struggles to find adequate meals. The core challenge is not a lack of resources but the absence of an efficient real-time system connecting food donors with recipients.

2.1 Key Pain Points
    • No centralized platform for food donors to post surplus food availability.
    • NGOs and orphanages lack real-time visibility into available food donations nearby.
    • Perishable food often expires before it can be redistributed due to communication gaps.
    • No accountability mechanism to track claimed vs. unclaimed donations.
    • Duplicate claiming of the same donation leads to inefficiency.

3. Goals & Objectives
3.1 Primary Goals
    1. Reduce commercial food waste by enabling real-time donation posting.
    2. Connect food donors with NGOs/orphanages efficiently through a mobile platform.
    3. Provide location-based discovery of food donations to minimize travel and spoilage.
    4. Ensure transparency via real-time status updates on donations.

3.2 Success Metrics
Metric	Target
Donor registration	50+ donors onboarded within 3 months
NGO registration	20+ NGOs onboarded within 3 months
Donation claim rate	>70% of posted donations claimed within 2 hours
App crash rate	<1% per session
Notification delivery	>95% of push notifications delivered within 30 seconds

4. User Personas
4.1 Donor (Restaurant / Hotel Manager)
    • Posts surplus food after events or at end-of-day.
    • Wants a quick, minimal-effort way to list food with quantity and location.
    • Needs confirmation when food is claimed to plan logistics.

4.2 Receiver (NGO / Orphanage Coordinator)
    • Actively looks for available food donations nearby.
    • Needs real-time alerts so food can be picked up before it spoils.
    • Wants a map view to plan the most efficient pickup route.

4.3 Admin (System Administrator)
    • Monitors platform activity and manages user accounts.
    • Resolves disputes between donors and receivers.
    • Views analytics on food saved, donations made, and active users.

5. Feature Requirements
5.1 Authentication Module
Feature ID	Feature	Priority	Description
AUTH-01	User Registration	P0	New users can register as Donor, Receiver, or Admin with email/password.
AUTH-02	User Login	P0	Existing users log in via Firebase Authentication.
AUTH-03	Role-Based Access	P0	Post-login, users are redirected to role-specific dashboards.
AUTH-04	Logout	P0	Users can securely log out from any screen.
AUTH-05	Password Reset	P1	Users can reset password via email link.

5.2 Donor Module
Feature ID	Feature	Priority	Description
DONOR-01	Post Food Donation	P0	Donor enters food name, quantity, pickup time, and contact details.
DONOR-02	Auto Location Fetch	P0	App fetches donor's GPS location via FusedLocationProviderClient.
DONOR-03	View My Donations	P1	Donor can see list of their past and active donations with status.
DONOR-04	Edit / Delete Donation	P1	Donor can update or remove a posted donation before it is claimed.
DONOR-05	Claim Confirmation	P1	Donor is notified when an NGO claims their donation.

5.3 Receiver (NGO) Module
Feature ID	Feature	Priority	Description
RCV-01	Browse Donations	P0	NGO sees all available donations in a list sorted by distance.
RCV-02	Food Map View	P0	Donations displayed on Google Map as markers.
RCV-03	Claim Donation	P0	NGO can claim a donation; status changes to 'Claimed' immediately.
RCV-04	Navigation to Donor	P1	App opens Google Maps navigation to the donor's location after claim.
RCV-05	Claim History	P1	NGO can view list of previously claimed donations.

5.4 Notification Module
Feature ID	Feature	Priority	Description
NOTIF-01	New Donation Alert	P0	Push notification sent to all NGOs within radius when new donation is posted.
NOTIF-02	Claim Confirmation Alert	P0	Donor notified via push when their food is claimed.
NOTIF-03	Expiry Warning	P2	Donor notified if their donation has been unclaimed for >2 hours.

5.5 Admin Module
Feature ID	Feature	Priority	Description
ADMIN-01	User Management	P1	Admin can view, approve, or deactivate user accounts.
ADMIN-02	Donation Analytics	P1	Dashboard showing total donations, claimed rate, food saved (kg).
ADMIN-03	Dispute Management	P2	Admin can resolve disputes between donors and receivers.

6. Non-Functional Requirements
Category	Requirement
Performance	App should load dashboard within 2 seconds on 4G connection.
Reliability	Firebase Realtime Database ensures 99.9% uptime for data sync.
Security	All user data encrypted in transit (HTTPS/TLS). Firebase Security Rules enforced.
Scalability	Firebase scales automatically; no manual server management needed.
Usability	UI should be operable by non-tech-savvy restaurant managers and NGO workers.
Offline Handling	App shows meaningful error when offline; queues actions if possible.
Compatibility	Supports Android 8.0 (API 26) and above.

7. User Stories
7.1 Donor Stories
    5. As a restaurant manager, I want to post surplus food in under 2 minutes so that I can offer it before it spoils.
    6. As a donor, I want to see when my donation is claimed so that I can prepare it for pickup.
    7. As a donor, I want my GPS location auto-filled so that I do not have to type my address every time.

7.2 Receiver Stories
    8. As an NGO coordinator, I want to receive instant push notifications when food is available nearby so that I can act quickly.
    9. As an NGO worker, I want to see donations on a map so that I can plan the most efficient pickup route.
    10. As a receiver, I want to claim a donation with one tap so that another NGO cannot claim the same food.

8. Out of Scope (v1.0)
    • iOS version of the application.
    • Payment or financial transactions of any kind.
    • Food quality verification or rating system.
    • Volunteer driver dispatch or logistics management.
    • Multi-language support (English only in v1.0).

9. Assumptions & Constraints
9.1 Assumptions
    • All users have Android smartphones with GPS and internet access.
    • Donors are willing to provide accurate food quantity and pickup time.
    • NGOs are registered organizations, not individual users.

9.2 Constraints
    • Academic mini-project timeline: one semester.
    • No dedicated backend server; all backend handled via Firebase.
    • No budget for paid APIs beyond Firebase free tier and Google Maps free quota.

10. High-Level Timeline
Phase	Deliverable	Duration
Phase 1 — Setup	Firebase project, Android project scaffold, Auth screens	Week 1-2
Phase 2 — Core Modules	Donor posting, Receiver listing, Firestore integration	Week 3-5
Phase 3 — Maps & Notifications	Google Maps integration, FCM push notifications	Week 6-7
Phase 4 — Admin & Polish	Admin dashboard, UI polish, bug fixes	Week 8-9
Phase 5 — Testing	Unit testing, UAT, performance testing	Week 10
Phase 6 — Submission	Final documentation, demo, submission	Week 11-12

