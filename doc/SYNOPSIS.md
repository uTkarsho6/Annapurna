A
Synopsis On
Smart Food Waste Management System – Connecting Restaurants, Hotels, and NGOs
Mini Project – III (7VSIT346)

Submitted by the following second year B. Tech. students


Mr. Utkarsh Patil	(246109045)
Mr. Ganesh Mirgane	(246109053)
Miss.Patil Anushka Dattatray	(246109047)



Under the Guidance of
Smt. Bharati S. Shetty





Department of Information Technology Walchand College of Engineering, Sangli Maharashtra, India - 416415
Academic Year: 2026-27
Title: Food Waste Management System

    1. Abstract:
In developing nations, food wastage and hunger are two parallel problems that contradict each other. While tons of excess food are discarded daily by restaurants, hotels, and banquet halls, a significant population struggles to find a single square meal. The gap between these two groups is not a lack of resources, but a lack of efficient connectivity and logistics.

This project proposes an Android Application designed to bridge this gap. The system acts as a platform connecting food donors (restaurants/hotels) with food recipients (NGOs/Orphanages) in real-time. By utilizing location services and instant notifications, the application ensures that perishable excess food is claimed and distributed before it spoils, effectively managing food waste while serving a humanitarian cause.



    2. Keywords:

Smart Food Waste Management, Android Application, Food Donation System, Location-Based Services,    NGO Connectivity, Firebase Database, Waste Reduction, Real Time Notification.



    3. Objectives:

    • To connect food donors with NGOs, enabling efficient distribution of food.
    • Provide Location-based discovery of Food Donations.
    • To create a simple system where donors can post food details and NGOs can easily find and collect donation through centralized interface.
    • To create a real time notification system where are NGO's instantly alerted when donor post details.
















    4. Introduction:
Food waste has become a significant global issue, contributing not only to environmental degradation         but also to social inequality. While many individuals face food scarcity, a considerable amount of                   surplus food from restaurants, hotels, and catering services is discarded daily. The primary                        challenge lies not in the lack of resources but in the absence of an efficient system that connects                      food donors with potential recipients quickly and reliably.
With the rapid growth of mobile technology and cloud computing, Android-based applications                     offer an effective solution for managing such challenges. The Smart Food Waste Management                      System is designed as a hyper-local digital platform that bridges the communication gap                                between food donors and NGOs. By integrating geolocation services, real-time alerts, and                         centralized databases, the application simplifies the donation process and ensures timely                              pickup of surplus food.
The system not only focuses on reducing waste but also aims to build a sustainable ecosystem                              that encourages    community engagement and responsible food management. Through automation                            and smart connectivity, the   proposed solution enhances efficiency, transparency, and accessibility                   in food donation activities.  
     5. Methodology
      Application Start	
        ◦ User opens the application.
        ◦ Selects Register / Login.
        ◦ Authentication handled using Firebase Authentication
        Role-Based Access
        ◦ After successful login, user selects role:
        ◦ Donor
        ◦ Receiver
        ◦ System redirects to respective dashboard.
     




         Donor Module
        ◦ Login verification.
        ◦ Donor enters: Food name ,QuantitY,Location ,Contact details
        ◦ Donation data stored in Firebase Cloud Database.
        ◦ Food status set as Available.
        ◦ Donor can logout securely.
           Data Storage
        ◦ All user data and food details stored in:
        ◦ Firebase Realtime Database / Firestore
       Enables:
    • Real-time updates
    • Secure cloud storage
    • Centralized data management
            Receiver Module & Data Processing
    • Login verification.
    • Receiver views available food donations.
    • Food displayed via list/map interface.
    • Receiver selects available food.
    • System updates:
    • Food status → Claimed
    • Claim details saved in database.
    • Prevents multiple users from claiming same food.

Real-Time Synchronization
    • When food is claimed:
    • Database updates instantly.
    • Other users see updated availability.
    • Ensures transparency and avoids duplication.








6.  Flowchart:

                         
               














7.Pseudocode:

START
START

Display "Welcome User"

WHILE user not authenticated
    Display options: Register or Login
    IF user selects Register THEN
        Collect registration details
        Create account
        Display "Registration Successful"
    ELSE IF user selects Login THEN
        Enter credentials
        IF login is valid THEN
            authenticated = TRUE
        ELSE
            Display "Invalid Login"
        END IF
    END IF
END WHILE


WHILE authenticated = TRUE

    Display Main Menu:
        1. Donate
        2. Receive
        3. Food Map
        4. About Us
        5. Contact Us
        6. Logout

    IF option = Donate THEN

        Select User Role (Donor / NGO / Admin)

        IF role = Donor THEN
            Enter food details (type, quantity, pickup time)
            Fetch current GPS location
            Post donation request
            Notify nearby NGOs
        END IF

    ELSE IF option = Receive THEN

        IF role = NGO THEN
            Search available donations nearby

            IF donations found THEN
                Display donation feed (sorted by distance)
                Select donation
                Send claim request

                IF donor confirms pickup THEN
                    Lock donation
                    Open navigation to donor location
                    Confirm handover
                    Update food saved analytics
                    Update donor impact score
                    Display "Pickup Successful"
                ELSE
                    Display "Request Rejected"
                END IF

            ELSE
                Display "No Donations Available"
            END IF
        END IF

    ELSE IF option = Food Map THEN
        Display nearby food locations on map

    ELSE IF option = About Us THEN
        Display platform information

    ELSE IF option = Contact Us THEN
        Display support/contact details

    ELSE IF option = Logout THEN
        authenticated = FALSE
        Display "Logged Out Successfully"
    END IF

END WHILE

END

END


8.References:

        ◦ S. Mittal, "Food Waste Management System using Android," International Journal of Engineering Research, 2022.
        ◦ Android Developer Documentation. Available: https://developer.android.com/
        ◦ Google Maps Platform Documentation. Available: https://developers.google.com/maps
        ◦ Kennard, Nicole. (2019). Food Waste Management. 10.1007/978-3-319-69626-3_86-1.
        ◦ Mohiuddin, Irfan & Shareef, Mohammed & Arif, Farhan & Bamasdoos, Ms. (2025). The Smart Food Waste Management System. International Journal of Information Technology and Computer Engineering. 13. 162-170. 10.62647/IJITCE2025V13I2sPP162-170.

   

  Name                                                                                                           Signature 
1. Utkarsh Patil (246109045) 
2. Ganesh Mirgane (246109053)
3. Anushka Patil (246109047) 
                                                                                     



           Name and Signature    		               Name and Signature      	        Name and Signature 
      Guide				     Panel Member			   HoD



    



















