


SECURITY & VULNERABILITY REPORT
Smart Food Waste Management System
Android Application — Security Analysis & Mitigation Plan
Version 1.0  |  Walchand College of Engineering, Sangli  |  2026-27




1. Severity Classification
All vulnerabilities in this document are rated using the following severity scale:

Severity	Definition	Action Required
CRITICAL	Immediate data breach, account takeover, or full system compromise possible	Must fix before first release
HIGH	Significant risk to user data or app integrity if exploited	Fix before production deployment
MEDIUM	Limited impact but could be chained with other issues	Fix within first update cycle
LOW	Minor issue, unlikely to be exploited in isolation	Fix in routine maintenance
INFO	Best practice improvement, no direct security risk	Address when convenient

2. Authentication Security
2.1 Vulnerability: Weak Password Policy
Attribute	Detail
Vulnerability ID	SEC-AUTH-01
Severity	HIGH
Component	RegisterActivity — Firebase Auth
Description	Firebase Auth allows passwords as short as 6 characters by default. Users could set '123456' or 'aaaaaa' as their password.
Attack Vector	Brute force attack or credential stuffing using common password lists
Impact	Unauthorized account access; donor or NGO data compromised

Mitigation — SEC-AUTH-01
    • Enforce password strength on the client side before calling Firebase: minimum 8 characters, at least 1 uppercase, 1 digit, 1 special character.
    • Show a real-time password strength indicator in the RegisterActivity UI.
    • Reject passwords that match a list of common passwords (e.g., 'password123').

// Client-side password validation
fun isPasswordStrong(password: String): Boolean {
    val regex = Regex("^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$]).{8,}$")
    return regex.matches(password)
}

2.2 Vulnerability: No Email Verification
Attribute	Detail
Vulnerability ID	SEC-AUTH-02
Severity	HIGH
Component	RegisterActivity — Firebase Auth
Description	Users can register with any email address (even one they do not own) and immediately access the app as a donor or receiver.
Attack Vector	Fake NGO accounts registered with fabricated emails to claim food fraudulently
Impact	Fraudulent food claims; genuine NGOs may miss donations

Mitigation — SEC-AUTH-02
    • After Firebase createUserWithEmailAndPassword(), immediately call user.sendEmailVerification().
    • Block dashboard access until emailVerified == true. Check this on every login.
    • Show a banner: 'Please verify your email to access donations.'

// Check email verified on login
val user = FirebaseAuth.getInstance().currentUser
if (user != null && !user.isEmailVerified) {
    showBanner("Please verify your email first.")
    FirebaseAuth.getInstance().signOut()
}

2.3 Vulnerability: No Rate Limiting on Login
Attribute	Detail
Vulnerability ID	SEC-AUTH-03
Severity	MEDIUM
Component	LoginActivity
Description	No client-side throttle on failed login attempts. Firebase does have server-side protection, but repeated attempts can cause account lockout for legitimate users.
Attack Vector	Automated credential stuffing scripts targeting donor / NGO accounts
Impact	Account lockout for real users; potential slow brute-force on weak passwords

Mitigation — SEC-AUTH-03
    • Track failed login attempts locally. After 5 failures, enforce a 30-second wait before allowing the next attempt.
    • Show a countdown timer in the UI: 'Too many attempts. Try again in 28s.'
    • Enable Firebase App Check (Play Integrity) to block automated scripts entirely.

2.4 Vulnerability: Role Stored Only in Firestore (Client-Readable)
Attribute	Detail
Vulnerability ID	SEC-AUTH-04
Severity	HIGH
Component	Firestore users collection
Description	User role (donor/receiver/admin) is stored in Firestore and read by the client app to decide which dashboard to show. A malicious user could modify their local app code to bypass the role check and access admin screens.
Attack Vector	Modified APK that skips role check and directly opens AdminDashboardActivity
Impact	Unauthorized access to admin analytics and user management screens

Mitigation — SEC-AUTH-04
    • Enforce role checks in Firestore Security Rules — not just in the Android app.
    • Admin-only Firestore collections (e.g., analytics aggregations) must have rules that verify the role server-side using Custom Claims.
    • Set Admin role via Firebase Custom Claims using a Cloud Function (not the client app).

// Cloud Function: set admin custom claim
exports.setAdminRole = functions.https.onCall(async (data, context) => {
  await admin.auth().setCustomUserClaims(data.uid, { role: 'admin' });
});

// Firestore rule using custom claim
allow read: if request.auth.token.role == 'admin';

3. Data Security
3.1 Vulnerability: Overly Permissive Firestore Rules
Attribute	Detail
Vulnerability ID	SEC-DATA-01
Severity	CRITICAL
Component	Firestore Security Rules
Description	Default Firestore rules or poorly written rules may allow any authenticated user to read all user documents — including FCM tokens, phone numbers, and emails of all other users.
Attack Vector	Any logged-in user queries /users collection and harvests contact details of all donors/NGOs
Impact	Mass PII data leak — names, emails, phone numbers of all registered users exposed

Mitigation — SEC-DATA-01
Deploy the following strict Firestore Security Rules:

rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Users can only read/write their own profile
    match /users/{userId} {
      allow read, write: if request.auth.uid == userId;
    }

    // Donations: any auth user can read, only donor can create/delete
    match /donations/{donationId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null
        && request.resource.data.donorUid == request.auth.uid
        && request.resource.data.status == 'available'
        && request.resource.data.keys().hasAll(['foodName','quantity','latitude','longitude']);
      allow update: if request.auth != null;  // needed for Transactions
      allow delete: if resource.data.donorUid == request.auth.uid
        && resource.data.status == 'available';
    }

    // Claims: participants only
    match /claims/{claimId} {
      allow read: if request.auth.uid == resource.data.donorUid
        || request.auth.uid == resource.data.receiverUid;
      allow create: if request.auth != null
        && request.resource.data.receiverUid == request.auth.uid;
    }
  }
}

3.2 Vulnerability: Contact Number Visible to All Receivers
Attribute	Detail
Vulnerability ID	SEC-DATA-02
Severity	MEDIUM
Component	Donation Detail Screen, Firestore donations collection
Description	Donor's phone number is stored directly in the donations document, readable by all authenticated receivers — even those who haven't claimed the donation.
Attack Vector	Any NGO account can harvest all donor phone numbers from donations collection
Impact	Privacy violation; donors may receive spam or unsolicited calls

Mitigation — SEC-DATA-02
    • Remove contactNumber from the donations document.
    • Only reveal contact details after a successful claim by reading from the users collection (which is protected by rules).
    • Show contact number only in the post-claim confirmation screen by fetching the donor's profile using donorUid.

3.3 Vulnerability: No Input Sanitization on Donation Fields
Attribute	Detail
Vulnerability ID	SEC-DATA-03
Severity	MEDIUM
Component	PostDonationActivity
Description	Text fields (foodName, quantity) are written to Firestore without sanitization. While Firestore is not SQL-injectable, unsanitized input can be used for stored XSS if data is ever rendered in a web admin panel.
Attack Vector	Malicious donor enters <script>alert('xss')</script> as food name; rendered in web dashboard
Impact	Stored XSS in any web-based admin panel; potential phishing content visible to NGOs

Mitigation — SEC-DATA-03
    • Strip HTML tags and special characters from all text inputs before writing to Firestore.
    • Limit field lengths: foodName max 100 chars, quantity max 50 chars.
    • Validate quantity field is numeric or a recognized pattern (e.g., '10 kg', '50 plates').

fun sanitize(input: String): String {
    return input.replace(Regex("[<>\"'&]"), "").trim().take(100)
}

3.4 Vulnerability: FCM Tokens Stored in Plaintext
Attribute	Detail
Vulnerability ID	SEC-DATA-04
Severity	LOW
Component	Firestore users collection — fcmToken field
Description	Device FCM tokens are stored as plaintext in the users collection. If Firestore rules are misconfigured (SEC-DATA-01), tokens could be harvested and used to send spoofed push notifications.
Attack Vector	Attacker reads FCM tokens via misconfigured rules, sends fake 'food available' notifications to NGOs
Impact	Notification spam; erosion of user trust

Mitigation — SEC-DATA-04
    • Ensure Firestore rules prevent other users from reading the fcmToken field (already covered by SEC-DATA-01 mitigation).
    • All FCM sends should originate from Firebase Cloud Functions (server-side) using the Firebase Admin SDK — never from client-side code.
    • Rotate FCM tokens: call FirebaseMessaging.getInstance().deleteToken() on logout and re-register on next login.

4. Network & Transport Security
4.1 Vulnerability: Cleartext Traffic Allowed
Attribute	Detail
Vulnerability ID	SEC-NET-01
Severity	HIGH
Component	AndroidManifest.xml
Description	If android:usesCleartextTraffic="true" is set (common in development), the app may transmit data over HTTP instead of HTTPS.
Attack Vector	Man-in-the-middle attack on public Wi-Fi — intercepts login credentials or donation data
Impact	Credential theft; donation data interception

Mitigation — SEC-NET-01
    • Ensure android:usesCleartextTraffic="false" in AndroidManifest.xml (default for API 28+).
    • Add a Network Security Config file to explicitly block all cleartext:

<!-- res/xml/network_security_config.xml -->
<network-security-config>
  <base-config cleartextTrafficPermitted="false">
    <trust-anchors>
      <certificates src="system"/>
    </trust-anchors>
  </base-config>
</network-security-config>

<!-- AndroidManifest.xml -->
<application android:networkSecurityConfig="@xml/network_security_config" ...>

4.2 Vulnerability: API Key Exposed in Source Code
Attribute	Detail
Vulnerability ID	SEC-NET-02
Severity	HIGH
Component	AndroidManifest.xml — Google Maps API Key
Description	The Google Maps API key is stored in AndroidManifest.xml and bundled into the APK. Anyone who decompiles the APK with apktool can extract the key and use it for their own apps at your expense.
Attack Vector	APK reverse engineering using apktool or jadx; key extracted and used to make Maps API calls billed to your project
Impact	Google Maps billing quota exhaustion; unexpected charges

Mitigation — SEC-NET-02
    • Restrict the Google Maps API key in Google Cloud Console: under API key restrictions, add your app's SHA-1 certificate fingerprint and package name. The key will only work when called from your signed APK.
    • Never commit the API key to a public GitHub repository. Use local.properties or a CI/CD secrets manager.

// local.properties (gitignored)
MAPS_API_KEY=AIza...

// build.gradle
android { defaultConfig { manifestPlaceholders = [mapsApiKey: MAPS_API_KEY] } }

// AndroidManifest.xml
<meta-data android:name="com.google.android.geo.API_KEY"
           android:value="${mapsApiKey}" />

5. Business Logic Vulnerabilities
5.1 Vulnerability: Race Condition on Donation Claiming
Attribute	Detail
Vulnerability ID	SEC-BIZ-01
Severity	CRITICAL
Component	ReceiverModule — Claim Donation (T-32)
Description	Without an atomic transaction, two NGOs can simultaneously read status='available', both pass the check, and both write status='claimed'. Result: one donation claimed by two organizations.
Attack Vector	Two NGO users tap 'Claim' within milliseconds of each other
Impact	Double claim confusion; one NGO travels to pick up food that's already been taken; donor harassed by two NGOs

Mitigation — SEC-BIZ-01
    • Use Firestore runTransaction() — atomic read-check-write that fails if the document was modified between read and write.
    • Show a clear error if the transaction fails: 'Sorry, this donation was just claimed by another NGO.'

db.runTransaction { transaction ->
    val snapshot = transaction.get(donationRef)
    if (snapshot.getString("status") != "available") {
        throw Exception("Already claimed")
    }
    transaction.update(donationRef, mapOf(
        "status" to "claimed",
        "claimedBy" to currentUid,
        "claimedAt" to FieldValue.serverTimestamp()
    ))
}

5.2 Vulnerability: Donor Can Post Unlimited Donations (Spam)
Attribute	Detail
Vulnerability ID	SEC-BIZ-02
Severity	MEDIUM
Component	PostDonationActivity, Firestore donations collection
Description	No limit on how many donations a single donor account can post. A malicious or careless user could flood the platform with fake donations, drowning out real ones.
Attack Vector	Script or repeated taps posting hundreds of fake donations in a short time
Impact	NGOs overwhelmed with fake listings; real donations buried; app unusable

Mitigation — SEC-BIZ-02
    • Limit donors to a maximum of 5 active (status='available') donations at any time. Check count before allowing a new post.
    • Enforce a cooldown: donors cannot post again within 10 minutes of their last post.
    • Implement this check in both the client (UX) and as a Firestore rule condition.

5.3 Vulnerability: Unauthorized Donation Deletion / Status Manipulation
Attribute	Detail
Vulnerability ID	SEC-BIZ-03
Severity	HIGH
Component	Firestore Security Rules — donations collection
Description	If Firestore update rules are too permissive (allow update: if request.auth != null), any authenticated user — including receivers — could update a donation's status back to 'available' after claiming it, effectively 'unclaiming' it to prevent others from benefiting.
Attack Vector	Malicious NGO claims donation, then sets status back to 'available' so the donor thinks no one wants it
Impact	Food waste; donor distrust of the platform

Mitigation — SEC-BIZ-03
    • Restrict update rules: donors can only update their own donations when status is 'available'. Status transitions must follow allowed paths only.
    • Use Firestore rules to enforce state machine: available -> claimed (by receiver), available -> expired (by Cloud Function only).

allow update: if (
  // Donor edits their own available donation
  (resource.data.donorUid == request.auth.uid && resource.data.status == 'available'
   && request.resource.data.status == 'available')
  ||
  // Receiver claims an available donation
  (resource.data.status == 'available' && request.resource.data.status == 'claimed'
   && request.resource.data.claimedBy == request.auth.uid)
);

6. Android Application Security
6.1 Vulnerability: Sensitive Data in SharedPreferences / Logs
Attribute	Detail
Vulnerability ID	SEC-APP-01
Severity	HIGH
Component	Android app — storage & logging
Description	Storing user tokens, emails, or roles in SharedPreferences without encryption makes them readable by other apps on rooted devices. Similarly, Log.d() statements in debug builds can leak sensitive info.
Attack Vector	Rooted device with a file manager app reads SharedPreferences XML; logcat logs captured by malicious app
Impact	Credential or session token theft on rooted devices

Mitigation — SEC-APP-01
    • Use EncryptedSharedPreferences (Jetpack Security library) for any locally stored sensitive data.
    • Never store passwords locally — rely solely on Firebase Auth tokens.
    • Remove all Log.d / Log.e statements in release builds using ProGuard rules.

// EncryptedSharedPreferences
val masterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
val prefs = EncryptedSharedPreferences.create(context, "secure_prefs", masterKey,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

// ProGuard rule to strip logs in release
-assumenosideeffects class android.util.Log { *; }

6.2 Vulnerability: Exported Activities
Attribute	Detail
Vulnerability ID	SEC-APP-02
Severity	MEDIUM
Component	AndroidManifest.xml
Description	Activities that do not explicitly set android:exported="false" may be launched by other apps via Intent, potentially bypassing the login check.
Attack Vector	Malicious app sends an explicit Intent to open AdminDashboardActivity directly, skipping auth
Impact	Auth bypass; unauthorized dashboard access on non-rooted devices

Mitigation — SEC-APP-02
    • Set android:exported="false" on all activities except MainActivity (launcher) and any deep-link receivers.
    • Add an auth guard at the top of every Activity's onCreate(): if FirebaseAuth.getCurrentUser() == null, redirect to LoginActivity.

// Every protected Activity
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (FirebaseAuth.getInstance().currentUser == null) {
        startActivity(Intent(this, LoginActivity::class.java))
        finish(); return
    }
}

6.3 Vulnerability: Missing Certificate Pinning
Attribute	Detail
Vulnerability ID	SEC-APP-03
Severity	LOW
Component	Network layer
Description	Without certificate pinning, an attacker with a self-signed CA installed on the device (possible on corporate or university networks) could perform HTTPS MITM and intercept Firebase/Maps traffic.
Attack Vector	Attacker installs rogue CA on device (requires user action or MDM), intercepts traffic via HTTPS proxy
Impact	Firebase auth tokens or donation data visible to attacker

Mitigation — SEC-APP-03
    • For an academic mini-project, full certificate pinning is not required. The network_security_config.xml from SEC-NET-01 already blocks user-added CAs.
    • If deploying to production: implement OkHttp CertificatePinner for Firebase REST calls.

7. Privacy & Compliance
7.1 Location Permission Handling
Attribute	Detail
Vulnerability ID	SEC-PRIV-01
Severity	MEDIUM
Component	PostDonationActivity, FusedLocationProviderClient
Description	Requesting ACCESS_FINE_LOCATION when ACCESS_COARSE_LOCATION is sufficient. Fine location is more privacy-invasive and harder to get permission for on Android 12+.
Recommendation	Use ACCESS_COARSE_LOCATION for donors — city-level accuracy is sufficient. Only request FINE location if precise map navigation is needed for receivers.

7.2 Data Retention Policy
Attribute	Detail
Vulnerability ID	SEC-PRIV-02
Severity	INFO
Component	Firestore donations collection, Cloud Functions
Description	Expired and old claimed donations accumulate in Firestore indefinitely. These contain donor location data and could be a privacy liability.
Recommendation	Auto-delete donations older than 30 days via a scheduled Cloud Function. Anonymize old claims by removing receiverUid after 30 days.

8. Security Implementation Task List
Task ID	Security Task	Severity Fixed	Estimated Hours
SEC-T-01	Implement client-side password strength validation (regex check, strength meter UI)	HIGH	2h
SEC-T-02	Add Firebase email verification gate — block dashboard until verified	HIGH	2h
SEC-T-03	Add login attempt counter with 30-second cooldown after 5 failures	MEDIUM	2h
SEC-T-04	Migrate role enforcement to Firebase Custom Claims; update Firestore rules to use token claims	HIGH	4h
SEC-T-05	Deploy strict Firestore Security Rules as defined in Section 3.1	CRITICAL	3h
SEC-T-06	Move contactNumber out of donations document; reveal only post-claim via users lookup	MEDIUM	2h
SEC-T-07	Add input sanitization on all free-text fields before Firestore write	MEDIUM	2h
SEC-T-08	Set android:usesCleartextTraffic=false + network_security_config.xml	HIGH	1h
SEC-T-09	Restrict Google Maps API key with SHA-1 fingerprint + package name in Cloud Console	HIGH	1h
SEC-T-10	Move API key to local.properties + add to .gitignore	HIGH	1h
SEC-T-11	Implement Firestore Transaction for atomic donation claiming	CRITICAL	4h
SEC-T-12	Add max active donations limit (5) and 10-minute posting cooldown	MEDIUM	2h
SEC-T-13	Refine Firestore update rules to enforce state machine transitions	HIGH	2h
SEC-T-14	Replace SharedPreferences with EncryptedSharedPreferences for any local storage	HIGH	2h
SEC-T-15	Add ProGuard rules to strip all Log.* calls from release build	MEDIUM	1h
SEC-T-16	Set android:exported=false on all non-launcher activities	MEDIUM	1h
SEC-T-17	Add auth guard (currentUser == null check) in onCreate of every protected Activity	HIGH	2h
SEC-T-18	Switch donor location request to ACCESS_COARSE_LOCATION	MEDIUM	1h
SEC-T-19	Implement Cloud Function to auto-delete donations older than 30 days	INFO	2h
SEC-T-20	Rotate FCM token on logout; re-register on next login	LOW	1h

9. Vulnerability Summary
ID	Vulnerability	Severity	Status
SEC-AUTH-01	Weak password policy	HIGH	Fix: SEC-T-01
SEC-AUTH-02	No email verification	HIGH	Fix: SEC-T-02
SEC-AUTH-03	No login rate limiting	MEDIUM	Fix: SEC-T-03
SEC-AUTH-04	Role stored client-side only	HIGH	Fix: SEC-T-04
SEC-DATA-01	Overly permissive Firestore rules	CRITICAL	Fix: SEC-T-05
SEC-DATA-02	Contact number exposed to all	MEDIUM	Fix: SEC-T-06
SEC-DATA-03	No input sanitization	MEDIUM	Fix: SEC-T-07
SEC-DATA-04	FCM tokens readable by others	LOW	Fix: SEC-T-20
SEC-NET-01	Cleartext traffic allowed	HIGH	Fix: SEC-T-08
SEC-NET-02	API key exposed in APK	HIGH	Fix: SEC-T-09, SEC-T-10
SEC-BIZ-01	Race condition on claim	CRITICAL	Fix: SEC-T-11
SEC-BIZ-02	Unlimited donation spam	MEDIUM	Fix: SEC-T-12
SEC-BIZ-03	Unauthorized status manipulation	HIGH	Fix: SEC-T-13
SEC-APP-01	Sensitive data in plaintext storage	HIGH	Fix: SEC-T-14, SEC-T-15
SEC-APP-02	Exported activities bypass auth	MEDIUM	Fix: SEC-T-16, SEC-T-17
SEC-APP-03	No certificate pinning	LOW	Fix: SEC-T-08 (partial)
SEC-PRIV-01	Excessive location permission	MEDIUM	Fix: SEC-T-18
SEC-PRIV-02	No data retention policy	INFO	Fix: SEC-T-19

Total: 2 CRITICAL | 7 HIGH | 6 MEDIUM | 2 LOW | 1 INFO
Estimated remediation effort: ~37 hours across 20 security tasks.

