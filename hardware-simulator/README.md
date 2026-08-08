# Smart Home Hardware Simulator

## Developed by

**Safiya**

---

# 1. My Contribution

### Hardware Simulator

1. Developed the Smart Home Hardware Simulator using **HTML, CSS, and JavaScript**.
2. Created the simulator dashboard for both **Ground Floor and First Floor**.
3. Implemented ON/OFF controls for smart devices.
4. Implemented the **3-Switch Panel** with Light, Fan, and AC controls.
5. Added **camera monitoring simulation** for the Living Room and Garage.
6. Added mock camera images and camera status interfaces.
7. Connected the Hardware Simulator to **Firebase Firestore**.
8. Implemented real-time device status synchronization with Firebase.
9. Fixed Firestore device paths and document IDs to match the project database.

### Iron Safety

10. Implemented **Kitchen Iron safety automation**.
11. Added `safetyEnabled` configuration.
12. Added `maxOnDuration` configuration.
13. Implemented automatic Iron OFF after the configured duration.
14. Added logic to cancel the safety timer when the Iron is manually switched OFF.
15. Prepared a Firebase **Cloud Function** for server-side Iron safety.
16. Tested the Cloud Function code with ESLint.
17. Prepared the Cloud Function for deployment.

### Light Scheduling

18. Implemented Firebase-based **light scheduling**.
19. Added `scheduleEnabled`.
20. Added `scheduleOnTime`.
21. Added `scheduleOffTime`.
22. Implemented automatic ON/OFF based on the configured schedule.
23. Connected Android scheduling settings with Firebase so the Simulator can use them.

### Android Application

24. Updated the Android **Dashboard** with dynamic Firebase values.
25. Added dynamic Ground Floor active-device count.
26. Added dynamic First Floor active-device count.
27. Updated Ground Floor room/device counts.
28. Updated First Floor room/device counts.
29. Added real-time active-device detection using `ON`, `ONLINE`, and `CONNECTED`.
30. Updated `FirebaseRepository` with floor-wide device listeners.
31. Restored the **Recent Activity** section.
32. Created the **Reports Activity**.
33. Added dynamic device information to Reports.
34. Added simulated total house energy usage.
35. Added simulated energy usage per device.
36. Updated Room Power so it dynamically changes according to active devices.
37. Added Android **Camera Activity**.
38. Added Living Room and Garage camera mock interfaces.
39. Updated scheduling functionality to save configuration to Firebase instead of only local storage.

### Firebase / Cloud / GitHub

40. Configured Firebase CLI.
41. Initialized Firebase Cloud Functions.
42. Connected the project to the correct Firebase project.
43. Tested Firebase Firestore communication.
44. Created and maintained Git branches for my work.
45. Committed and pushed my Hardware Simulator changes.
46. Created the `safiya-App-update` branch for Android updates.
47. Committed and pushed my Android application updates.
48. Updated the Hardware Simulator README/documentation.
49. Tested the Android application after the latest changes using a successful build/compile.

---

# 2. Clear Explanation of My Work

## 2.1 Hardware Simulator

My main responsibility was developing the **Smart Home Hardware Simulator**.

I created a web-based simulator using:

* HTML
* CSS
* JavaScript
* Firebase Firestore

The simulator represents the physical devices in the smart home. Users can turn devices ON and OFF, and the status is synchronized with Firebase.

The simulator contains devices from both floors.

### Ground Floor

* Living Room Light
* TV Outlet
* Security Camera
* Kitchen Light
* Kitchen Iron
* Garage Light
* Garage Camera

### First Floor

* Master Bedroom Light
* 3-Switch Panel
* Bedroom Light
* Bedroom Outlet
* Bathroom Light

The simulator communicates with the same Firestore database used by the Android application.

```text
Android App
     ↓
Firebase Firestore
     ↑
Hardware Simulator
```

When a device status changes, the change can be reflected between the systems.

---

# 3. Device Control

I implemented ON/OFF controls for the smart home devices.

For example:

```text
Living Room Light
       ↓
      ON
       ↓
Firebase Firestore
```

If the user switches it OFF:

```text
Living Room Light
       ↓
      OFF
       ↓
Firebase Firestore
```

I tested these controls with the different devices in the simulator.

---

# 4. Multi-Switch Panel

I implemented the **3-Switch Panel** separately because it contains multiple switches.

It supports:

```text
Switch Panel
├── Light
├── Fan
└── AC
```

Each switch can be controlled independently.

For example:

```text
Light → ON
Fan   → OFF
AC    → ON
```

The states are also synchronized through Firebase.

---

# 5. Camera Simulation

I implemented camera simulation for the smart home security system.

The project includes:

* Living Room Security Camera
* Garage Camera

I created camera interfaces and mock camera images because this is a software/hardware simulation rather than a real physical camera.

I also added camera status handling so that camera states such as `ONLINE` can be recognized by the system.

The Android application also contains a `CameraActivity` for displaying camera information.

---

# 6. Firebase Firestore Integration

I connected the Hardware Simulator to **Firebase Firestore**.

The simulator uses the project's hierarchical database structure:

```text
House
 ↓
Floor
 ↓
Room
 ↓
Device
```

For example:

```text
houses
└── house1
    └── floors
        └── groundFloor
            └── rooms
                └── kitchen
                    └── devices
                        └── kitchenIron
```

Each device stores information such as:

```text
name
type
status
```

The simulator uses these documents to control the devices.

---

# 7. Real-Time Device Synchronization

The Hardware Simulator listens for changes in Firebase Firestore.

For example:

```text
Android Application
        ↓
Change device status
        ↓
Firebase Firestore
        ↓
Hardware Simulator
        ↓
Device status updated
```

The same process works in the opposite direction:

```text
Hardware Simulator
        ↓
Change device status
        ↓
Firebase Firestore
        ↓
Android Application
        ↓
UI updated
```

This provides real-time communication between the simulated hardware and mobile application.

---

# 8. Iron Safety Automation

The Kitchen Iron is a **safety-critical device**, so I implemented automatic safety control.

The Iron uses:

```text
safetyEnabled
maxOnDuration
```

For example:

```text
safetyEnabled = true
maxOnDuration = 15
```

When the Iron is turned ON:

```text
Iron ON
   ↓
Check safetyEnabled
   ↓
Start timer
   ↓
15 minutes
   ↓
Automatically OFF
```

If the user manually turns the Iron OFF before the timer finishes:

```text
Iron OFF
   ↓
Cancel timer
```

This prevents the Iron from remaining ON accidentally.

---

# 9. Firebase Cloud Function

I also prepared the **Cloud Function** for the Iron safety feature.

The purpose is to move safety automation toward the cloud/server side rather than depending only on the browser being open.

I:

* Initialized Firebase Functions.
* Created the Functions project.
* Implemented the safety logic.
* Fixed ESLint errors.
* Successfully passed `npm run lint`.
* Prepared the function for deployment.

The final deployment was blocked because Firebase requires the project to use the **Blaze plan** to enable the required Cloud Build API.

Therefore:

> **Cloud Function implemented and deployment prepared; production deployment requires Firebase Blaze plan activation.**

---

# 10. Light Scheduling

I implemented scheduling support for lights.

Firebase stores:

```text
scheduleEnabled
scheduleOnTime
scheduleOffTime
```

For example:

```text
scheduleEnabled = true
scheduleOnTime = 18:00
scheduleOffTime = 22:00
```

The simulator checks the current time periodically.

At 18:00:

```text
Light → ON
```

At 22:00:

```text
Light → OFF
```

This allows the Android application and Hardware Simulator to share the same scheduling configuration through Firebase.

---

# 11. Android Dashboard Updates

I also contributed to the Android application.

I updated the Dashboard so that information is no longer completely static.

The Dashboard can calculate active devices dynamically from Firebase.

For example:

```text
Ground Floor
Active Devices: 3
```

and:

```text
First Floor
Active Devices: 2
```

These values can change when device statuses change.

I also updated active-device detection to recognize:

```text
ON
ONLINE
CONNECTED
```

This was especially useful for cameras because a camera can be `ONLINE` rather than `ON`.

---

# 12. Ground Floor and First Floor Updates

I updated both floor screens.

The application dynamically calculates:

* Number of rooms
* Number of devices
* Number of active devices
* Devices in each room
* Currently active devices

For example:

```text
Living Room
3 devices • 2 currently ON
```

Instead of hard-coded values, the information comes from Firebase.

---

# 13. Firebase Repository

I updated `FirebaseRepository.kt`.

I added functionality for listening to devices across a complete floor.

This allows the application to receive real-time Firestore changes.

The general flow is:

```text
Firestore
   ↓
FirebaseRepository
   ↓
Android Activity
   ↓
UI
```

This makes the Dashboard and floor information more dynamic.

---

# 14. Recent Activity

I restored the **Recent Activity** section on the Dashboard.

The purpose is to give the user a quick view of recent smart-home activity rather than showing only the current device state.

---

# 15. Reports

I created `ReportsActivity`.

The Reports page provides information about devices across both floors.

It includes:

* Device information
* Device status
* Total simulated energy usage
* Simulated energy usage per device

The energy values are **simulated**, because the project does not use physical electricity meters.

---

# 16. Dynamic Room Power

I updated the Room pages so the Power section is no longer always:

```text
0 W
```

Instead, power is calculated according to active devices.

For example:

```text
Device OFF
→ No simulated power

Device ON
→ Simulated power added
```

This demonstrates how a real smart-home system could display energy consumption.

---

# 17. Android Camera Functionality

I also contributed to the Android camera interface.

I created:

```text
CameraActivity.kt
activity_camera.xml
```

and camera-related drawable resources.

I added mock images for:

```text
Living Room Camera
Garage Camera
```

This provides a camera-monitoring interface for the smart-home application.

---

# 18. Scheduling Integration with Android

I also updated the Android scheduling functionality.

Previously, scheduling settings were mainly stored locally.

I changed the implementation so scheduling configuration can be stored in Firebase.

For example:

```text
scheduleEnabled
scheduleOnTime
scheduleOffTime
```

For the Iron:

```text
safetyEnabled
maxOnDuration
```

Therefore:

```text
Android App
     ↓
Save Schedule
     ↓
Firebase
     ↓
Hardware Simulator
     ↓
Execute Schedule
```

This makes the Android application and simulator work together rather than having separate local configurations.

---

# 19. GitHub Contribution

I worked using separate Git branches.

My main branches were:

```text
safiya-simulator
safiya-App-update
```

### `safiya-simulator`

Used mainly for:

* Hardware Simulator
* Firebase Web integration
* Device control
* Safety automation
* Scheduling
* Simulator documentation

### `safiya-App-update`

Used for:

* Dashboard updates
* Floor updates
* Camera functionality
* Reports
* Dynamic power
* Firebase Repository updates
* Android scheduling updates

I also resolved Git synchronization problems when the remote branch contained changes that were not present locally.

---

# 20. Testing

I tested:

* Firebase connection
* Device ON/OFF controls
* Firestore updates
* Real-time synchronization
* Multi-switch panel
* Camera simulation
* Camera status
* Dashboard active-device counts
* Ground Floor dynamic values
* First Floor dynamic values
* Room power calculations
* Reports
* Recent Activity
* Iron safety logic
* Light scheduling logic
* Firebase scheduling configuration
* Cloud Function linting
* Android compilation

Example simulator output:

```text
Firebase Connected Successfully
Smart Home Simulator Started

livingRoomLight changed to ON
livingRoomLight changed to OFF
tvOutlet changed to ON
tvOutlet changed to OFF
kitchenLight changed to ON
kitchenLight changed to OFF
kitchenIron changed to ON
kitchenIron changed to OFF
garageLight changed to ON
garageLight changed to OFF
masterBedroomLight changed to ON
masterBedroomLight changed to OFF
bedroomLight changed to ON
bedroomLight changed to OFF
bathroomLight changed to ON
bathroomLight changed to OFF
```

The Android application was successfully compiled after the latest updates.

---

# 21. My Overall Role

My main responsibility was the **Hardware Simulator and Firebase-based automation**.

I developed the web-based Smart Home Simulator, connected it to Firebase Firestore, implemented device controls, camera simulation, multi-switch controls, Iron safety automation, and light scheduling.

I also contributed to the Android application by implementing dynamic Dashboard and floor information, camera functionality, Reports, dynamic power calculations, Recent Activity, and Firebase-based scheduling configuration.

I also prepared and tested the Firebase Cloud Function for Iron safety automation and maintained the related GitHub branches and documentation.

---

# Technologies Used

* HTML
* CSS
* JavaScript
* Kotlin
* Android Studio
* Firebase Authentication
* Firebase Firestore
* Firebase Cloud Functions
* Firebase CLI
* Git
* GitHub

---

# Project Structure

```text
hardware-simulator/
│
├── index.html
├── style.css
├── script.js
├── firebase.js
└── README.md
```

Android-related files include:

```text
app/
└── src/
    └── main/
        ├── java/
        │   └── com/example/smarthome/
        │       ├── CameraActivity.kt
        │       ├── ReportsActivity.kt
        │       ├── MainActivity.kt
        │       ├── GroundFloorActivity.kt
        │       ├── FirstFloorActivity.kt
        │       └── repository/
        │           └── FirebaseRepository.kt
        │
        └── res/
            ├── layout/
            │   ├── activity_camera.xml
            │   ├── activity_reports.xml
            │   ├── activity_main.xml
            │   ├── activity_ground_floor.xml
            │   └── activity_first_floor.xml
            │
            └── drawable/
                ├── living_room_camera_mock.jpg
                └── garage_camera_mock.jpg
```

---

# Overall System Data Flow

```text
                 Android Application
                         │
                         │
                         ▼
                  Firebase Firestore
                         │
                         │
                         ▼
                 Hardware Simulator
                         │
             ┌───────────┴───────────┐
             │                       │
             ▼                       ▼
       Iron Safety              Light Scheduling
        Automation                Automation
```

The Android application and Hardware Simulator use Firebase Firestore as the common communication layer.

This provides a connected smart-home system with real-time monitoring, device control, safety automation, scheduling, camera simulation, reporting, and dynamic device information.
