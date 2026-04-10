


TECHNICAL REQUIREMENTS DOCUMENT
Smart Food Waste Management System
Android Application — Full Task Breakdown
Version 1.0  |  Walchand College of Engineering, Sangli  |  2026-27




1. Technology Stack
Layer	Technology	Purpose
Frontend	Android (Java/Kotlin)	UI, user interaction, camera, GPS
Authentication	Firebase Authentication	Email/password login, role management
Database	Cloud Firestore	Storing users, donations, claim records
Real-time Sync	Firestore onSnapshot listeners	Live donation status updates
Push Notifications	Firebase Cloud Messaging (FCM)	Alerts to NGOs and donors
Maps	Google Maps SDK for Android	Donation map view, marker display
Location	FusedLocationProviderClient	Auto-fetch donor GPS coordinates
Navigation	Google Maps Intent	Turn-by-turn nav to donor location
Backend Logic	Go (Golang) Notification Server	Real-time Firestore listener; sends FCM push notifications on new donation & claim events; hosted free on Render.com
Analytics	Firebase Analytics (optional)	Usage tracking, donor impact score

2. System Architecture
2.1 Architecture Overview
The application follows a client-server architecture where the Android app is the client and Firebase acts as the entire backend (BaaS — Backend as a Service). There is no custom REST API server.

2.2 Firestore Data Model
Collection: users
Field	Type	Description
uid	String	Firebase Auth UID (document ID)
name	String	Full name of user
email	String	Email address
role	String	'donor' | 'receiver' | 'admin'
fcmToken	String	Device token for push notifications
createdAt	Timestamp	Account creation time

Collection: donations
Field	Type	Description
donationId	String	Auto-generated document ID
donorUid	String	UID of the posting donor
foodName	String	Name/description of food
quantity	String	Quantity (e.g., '10 kg' or '50 plates')
pickupTime	Timestamp	Latest time for pickup
latitude	Number	GPS latitude of donor
longitude	Number	GPS longitude of donor
contactNumber	String	Donor's contact number
status	String	'available' | 'claimed' | 'expired'
claimedBy	String	UID of claiming NGO (null if unclaimed)
claimedAt	Timestamp	Time of claim (null if unclaimed)
createdAt	Timestamp	Time donation was posted

Collection: claims
Field	Type	Description
claimId	String	Auto-generated document ID
donationId	String	Reference to donations collection
receiverUid	String	UID of the claiming NGO
donorUid	String	UID of the original donor
claimedAt	Timestamp	Time of claim
handoverConfirmed	Boolean	True after donor confirms handover

3. Complete Task Breakdown
Phase 1 — Project Setup & Firebase Configuration
Task ID	Task	Sub-tasks / Details	Est. Hours	Dependencies
T-01	Create Android Project	New Android project in Android Studio, set minSdk=26, add Kotlin support	2h	None
T-02	Add Firebase to Android	Add google-services.json, update build.gradle with Firebase BoM, sync project	1h	T-01
T-03	Add Dependencies	Add Firebase Auth, Firestore, FCM, Google Maps, FusedLocation to build.gradle	1h	T-02
T-04	Create Firebase Project	Set up Firebase project on console.firebase.google.com, enable Auth, Firestore, FCM	1h	None
T-05	Setup Firestore Rules	Write security rules: users can only read/write their own data; donations readable by all authenticated users	2h	T-04
T-06	Enable Google Maps API	Enable Maps SDK for Android in Google Cloud Console, add API key to AndroidManifest.xml	1h	T-01
T-07	Setup Project Package Structure	Create packages: auth, donor, receiver, admin, models, utils, notifications	1h	T-01

Phase 2 — Authentication Module
Task ID	Task	Sub-tasks / Details	Est. Hours	Dependencies
T-08	RegisterActivity UI	XML layout: Name, Email, Password fields, Role selection spinner (Donor/Receiver), Register button	2h	T-07
T-09	RegisterActivity Logic	Firebase createUserWithEmailAndPassword(), save user doc to Firestore users collection with role	3h	T-08, T-05
T-10	LoginActivity UI	XML layout: Email, Password fields, Login button, link to Register	1h	T-07
T-11	LoginActivity Logic	Firebase signInWithEmailAndPassword(), fetch user role from Firestore, redirect to correct dashboard	3h	T-10, T-09
T-12	Role-Based Redirect	Post-login: if role='donor' open DonorDashboard, if 'receiver' open ReceiverDashboard, if 'admin' open AdminDashboard	1h	T-11
T-13	Logout Functionality	FirebaseAuth.signOut(), clear local session, redirect to LoginActivity from any screen	1h	T-11
T-14	Password Reset	Firebase sendPasswordResetEmail(), show success/error toast	1h	T-10
T-15	Input Validation	Validate non-empty fields, valid email format, password length >= 6 chars, show inline errors	2h	T-08, T-10
T-16	Auto Login Check	On app start, check FirebaseAuth.getCurrentUser(); if not null, skip login and redirect to dashboard	1h	T-12

Phase 3 — Donor Module
Task ID	Task	Sub-tasks / Details	Est. Hours	Dependencies
T-17	DonorDashboardActivity UI	XML layout: Welcome text, Post Donation button, View My Donations button, Logout button	2h	T-12
T-18	PostDonationActivity UI	XML layout: Food Name, Quantity, Pickup Time (DateTimePicker), Contact Number, Location (auto + manual override), Submit button	3h	T-17
T-19	Fetch GPS Location	FusedLocationProviderClient.getLastLocation(), request ACCESS_FINE_LOCATION permission at runtime, show lat/lng in location field	3h	T-18
T-20	Post Donation to Firestore	On Submit: create donation document in Firestore with status='available', donorUid, timestamp, all fields	3h	T-18, T-19
T-21	Trigger FCM on Post	After writing to Firestore, call Firebase Cloud Function (or write FCM logic) to send notification to all receivers	4h	T-20, T-34
T-22	My Donations List UI	RecyclerView showing donor's donations with food name, quantity, status badge (Available / Claimed)	3h	T-17
T-23	My Donations Firestore Query	Query donations where donorUid == currentUser.uid, ordered by createdAt descending, attach onSnapshot listener	2h	T-22
T-24	Edit Donation	Allow donor to edit foodName and quantity if status is still 'available'; update Firestore document	2h	T-23
T-25	Delete Donation	Allow donor to delete donation if status is 'available'; delete Firestore document with confirmation dialog	2h	T-23
T-26	Claim Notification for Donor	When donation status changes to 'claimed' via onSnapshot, show FCM notification to donor: 'Your food was claimed by [NGO]'	3h	T-23, T-34

Phase 4 — Receiver (NGO) Module
Task ID	Task	Sub-tasks / Details	Est. Hours	Dependencies
T-27	ReceiverDashboardActivity UI	XML layout: Browse Donations button, Food Map button, My Claims button, Logout button	2h	T-12
T-28	Browse Donations List UI	RecyclerView showing all available donations: food name, quantity, distance from user, pickup time	3h	T-27
T-29	Fetch Available Donations	Firestore query: donations where status == 'available', real-time onSnapshot listener, sort by createdAt	2h	T-28
T-30	Calculate Distance	Use android.location.Location.distanceBetween() to calculate distance between receiver and each donation, show in list	2h	T-28, T-29
T-31	Donation Detail Screen	On list item click: show full donation details (food name, quantity, pickup time, contact, donor distance), Claim button	2h	T-28
T-32	Claim Donation — Atomic Write	Use Firestore Transaction: check status == 'available', then set status='claimed', claimedBy=uid, claimedAt=now. Fail gracefully if already claimed.	4h	T-31
T-33	Open Navigation after Claim	After successful claim, open Google Maps Intent with donor lat/lng for turn-by-turn navigation	1h	T-32
T-35	My Claims History	RecyclerView showing claims made by this NGO; query claims collection where receiverUid == currentUser.uid	2h	T-27

Phase 5 — Maps Module
Task ID	Task	Sub-tasks / Details	Est. Hours	Dependencies
T-36	Food Map Activity	SupportMapFragment inside Activity, request location permission if not granted	2h	T-06
T-37	Load Donations as Markers	Fetch available donations from Firestore, place Marker at each donation's lat/lng with food name as title	3h	T-36, T-29
T-38	Marker Info Window	Custom InfoWindowAdapter showing food name, quantity, distance on marker tap	2h	T-37
T-39	Claim from Map	Tapping info window opens Donation Detail Screen (T-31) so user can claim directly from map	2h	T-38, T-31
T-40	Real-Time Map Updates	onSnapshot listener on donations collection; add/remove markers as donations appear, get claimed, or expire	3h	T-37
T-41	Show User Location	Enable setMyLocationEnabled(true) on map, center camera on user's location on map open	1h	T-36

Phase 6 — Push Notifications (Go Backend + FCM)
Task ID	Task	Sub-tasks / Details	Est. Hours	Dependencies
T-34	FCM Setup in Android	Add FirebaseMessagingService, override onMessageReceived(), show NotificationChannel for Android 8+	3h	T-03
T-42	Save FCM Token to Firestore	On app launch, get FCM token via FirebaseMessaging.getToken(), save to users/{uid}/fcmToken	2h	T-34, T-09
T-43	Go Server — Notify NGOs on New Donation	Go server listens to Firestore donations collection via real-time listener. On document creation, queries all Receiver fcmTokens and sends FCM multicast. Hosted on Render.com free tier.	5h	T-34, T-42
T-44	Go Server — Notify Donor on Claim	Go server detects donation status change to 'claimed', fetches the donorUid's fcmToken, sends individual FCM notification: 'Your food was claimed!'	3h	T-43
T-45	Go Server — Expiry Auto-Update	Go server runs a goroutine ticker every 30 minutes. Queries Firestore for donations where pickupTime < now AND status == 'available', bulk-updates status to 'expired'.	3h	T-43

Phase 7 — Admin Module
Task ID	Task	Sub-tasks / Details	Est. Hours	Dependencies
T-46	AdminDashboardActivity UI	Tabs: Users, Donations, Analytics	2h	T-12
T-47	User Management List	RecyclerView of all users with name, role, email; option to deactivate account (set disabled field in Firestore)	4h	T-46
T-48	Donations Overview	RecyclerView of all donations with status filter (All / Available / Claimed / Expired)	3h	T-46
T-49	Analytics Dashboard	Display: Total Donations Posted, Total Claimed, Total Expired, Estimated Food Saved (kg) based on quantity field	4h	T-46

Phase 8 — UI Polish & Common Components
Task ID	Task	Sub-tasks / Details	Est. Hours	Dependencies
T-50	Splash Screen	App logo + loading spinner for 2 seconds while auth state is checked	1h	T-16
T-51	Navigation Drawer / Bottom Nav	Persistent navigation for each dashboard: Home, Map, History, About, Contact, Logout	3h	T-17, T-27
T-52	About Us Screen	Static screen with project info, team names, college name	1h	T-51
T-53	Contact Us Screen	Static screen with guide name and contact details	1h	T-51
T-54	Empty State Views	Show 'No donations available' illustration when list is empty	1h	T-28, T-22
T-55	Loading Indicators	Show ProgressBar while Firestore queries are in-flight; hide on completion	2h	All list screens
T-56	Error Handling	Show user-friendly Toasts / Snackbars for network errors, permission denials, auth failures	2h	All activities
T-57	Status Badges	Colored chips on donation cards: green=Available, orange=Claimed, grey=Expired	1h	T-22, T-28

Phase 9 — Testing
Task ID	Task	Sub-tasks / Details	Est. Hours	Dependencies
T-58	Unit Tests — Auth Logic	Test registration validation, login error states using JUnit + Mockito	3h	T-15
T-59	Unit Tests — Firestore Transaction	Test claim transaction: success case, already-claimed failure case	3h	T-32
T-60	Integration Test — Donation Flow	End-to-end: Donor posts -> Receiver sees in list -> Receiver claims -> Status updated -> Donor notified	4h	T-20, T-32, T-44
T-61	UI Testing with Espresso	Automate login, post donation, claim donation flows using Espresso test framework	4h	All UI screens
T-62	Performance Testing	Measure app startup time, list load time with 100+ donations in Firestore	2h	T-29, T-37
T-63	Device Compatibility Testing	Test on Android 8, 10, 12, 14 emulators and minimum 2 physical devices	3h	All modules
T-64	Notification Testing	Verify FCM delivery within 30 seconds on real device; test foreground and background notification handling	2h	T-43, T-44

4. Firestore Security Rules
The following rules should be deployed to Firebase Console under Firestore > Rules:

rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth.uid == userId;
    }
    match /donations/{donationId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null && request.resource.data.donorUid == request.auth.uid;
      allow update: if request.auth != null;  // Transactions need update permission
      allow delete: if request.auth != null && resource.data.donorUid == request.auth.uid;
    }
    match /claims/{claimId} {
      allow read, write: if request.auth != null;
    }
  }
}

5. Task Summary
Phase	Tasks	Total Est. Hours
Phase 1 — Project Setup	T-01 to T-07	9h
Phase 2 — Authentication	T-08 to T-16	15h
Phase 3 — Donor Module	T-17 to T-26	25h
Phase 4 — Receiver Module	T-27 to T-35	20h
Phase 5 — Maps Module	T-36 to T-41	13h
Phase 6 — Push Notifications	T-34, T-42 to T-45	16h
Phase 7 — Admin Module	T-46 to T-49	13h
Phase 8 — UI Polish	T-50 to T-57	12h
Phase 9 — Testing	T-58 to T-64	21h
TOTAL	64 tasks	~144 hours

6. Key Technical Risks & Mitigations
Risk	Impact	Mitigation
Two NGOs claim same donation simultaneously	High	Use Firestore Transaction (T-32) — atomic read-check-write prevents double claims
Firebase free tier quota exceeded	Medium	Monitor Firestore reads/writes in console; cache donation list locally with DiffUtil in RecyclerView
GPS unavailable indoors	Medium	Allow manual address input as fallback in PostDonationActivity (T-19)
FCM notification not delivered	Medium	Test foreground + background; store in-app notification log as fallback
Expired donations cluttering the list	Low	Cloud Function auto-expires (T-45); filter status=='available' in all queries

