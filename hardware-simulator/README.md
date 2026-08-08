# Smart Home Hardware Simulator

## Developed by

**Safiya**

---

## My Contribution

I developed and integrated the **Smart Home Hardware Simulator** and contributed to the Android application's dynamic device monitoring, camera functionality, reports, and Firebase-based automation.

### Main work completed

1. Developed the web-based Hardware Simulator using **HTML, CSS, and JavaScript**.
2. Connected the Hardware Simulator to **Firebase Firestore**.
3. Implemented real-time device status synchronization between the simulator and Firebase.
4. Added ON/OFF controls for smart home devices.
5. Implemented the **Multi-Switch Panel** simulation.
6. Added **Camera Monitoring Simulation** for:

   * Living Room Security Camera
   * Garage Camera
7. Added camera status and monitoring UI to the Android application.
8. Implemented **Iron Safety Auto Shutdown** logic.
9. Added Firebase-based **Iron Safety configuration**:

   * `safetyEnabled`
   * `maxOnDuration`
10. Implemented **Light Scheduling** support:

    * `scheduleEnabled`
    * `scheduleOnTime`
    * `scheduleOffTime`
11. Implemented automatic light scheduling in the Hardware Simulator.
12. Updated Android scheduling functionality so schedule settings are stored in **Firebase Firestore** instead of only local storage.
13. Updated Firebase repository functionality to read and write scheduling and safety settings.
14. Added dynamic **Active Device** counting for Ground Floor and First Floor.
15. Added support for treating `ON`, `ONLINE`, and `CONNECTED` devices as active.
16. Added dynamic room device and active-device information to the floor screens.
17. Added a **Reports Activity** and dynamic device/energy information.
18. Restored the **Recent Activity** section on the dashboard.
19. Added dynamic room power information based on active devices.
20. Added Android camera screens and camera mock images.
21. Added Firebase real-time listeners for device changes.
22. Tested the project using Android Studio, Firebase, and the Hardware Simulator.
23. Configured and prepared **Firebase Cloud Functions** for server-side automation.
24. Created and maintained documentation for the Hardware Simulator.

---

# Hardware Simulator Features

## 1. Smart Device Dashboard

The Hardware Simulator provides a web-based dashboard for monitoring and controlling smart home devices.

Users can:

* View device status
* Turn devices ON
* Turn devices OFF
* Monitor real-time changes
* Interact with simulated smart home hardware

---

## 2. Firebase Firestore Integration

The simulator is connected to **Firebase Firestore**.

Device status changes are synchronized with Firebase in real time.

### Example

```text
Simulator
    ↓
Firebase Firestore
    ↓
Android Application
```

and:

```text
Android Application
    ↓
Firebase Firestore
    ↓
Hardware Simulator
```

This allows the Android application and Hardware Simulator to share the same device state.

---

# 3. Device ON/OFF Control

The simulator supports ON/OFF control for the smart home devices.

The following devices are simulated:

### Ground Floor

* Living Room Light
* TV Power Outlet
* Security Camera
* Kitchen Light
* Kitchen Iron
* Garage Light
* Garage Camera

### First Floor

* Master Bedroom Light
* Multi-Switch Panel
* Bedroom Light
* Bedroom Outlet
* Bathroom Light

---

# 4. Multi-Switch Panel

The Master Bedroom contains a simulated multi-switch unit.

The panel supports:

* Light
* Fan
* AC

Each switch can be controlled independently.

Example:

```text
Multi-Switch Panel

Light   → ON/OFF
Fan     → ON/OFF
AC      → ON/OFF
```

The switch states are synchronized with Firebase.

---

# 5. Camera Monitoring Simulation

The system includes simulated security cameras.

### Cameras

* Living Room Security Camera
* Garage Camera

The Android application includes camera monitoring screens with simulated camera previews.

The simulator provides camera status information, while the Android application provides the camera monitoring interface.

---

# 6. Iron Safety Automation

The Kitchen Iron is treated as a **safety-critical device**.

The simulator monitors the iron's Firebase status.

When the iron is switched ON:

```text
Iron ON
   ↓
Check safetyEnabled
   ↓
Start safety timer
   ↓
maxOnDuration reached
   ↓
Automatically turn OFF
   ↓
Update Firebase
```

If the iron is manually switched OFF before the timer finishes, the safety timer is cancelled.

### Firebase Configuration

The Kitchen Iron supports:

```text
safetyEnabled
maxOnDuration
```

Example:

```text
safetyEnabled = true
maxOnDuration = 15
```

`maxOnDuration` represents the maximum allowed ON duration in minutes.

---

# 7. Light Scheduling

The simulator supports automatic scheduling for lights.

Firebase stores:

```text
scheduleEnabled
scheduleOnTime
scheduleOffTime
```

Example:

```text
scheduleEnabled = true
scheduleOnTime = 18:00
scheduleOffTime = 22:00
```

The simulator checks the current time periodically.

### Automatic Schedule

```text
18:00
  ↓
Light automatically ON

22:00
  ↓
Light automatically OFF
```

This allows the simulator to behave like an automated smart home system.

---

# 8. Real-Time Firebase Listeners

The simulator listens for changes in Firestore.

For example:

```text
Android changes Living Room Light
            ↓
       Firebase
            ↓
Simulator receives change
            ↓
Living Room Light becomes ON
```

The simulator also updates Firebase when a device is controlled from the web interface.

---

# Android Application Contributions

In addition to the Hardware Simulator, I contributed to the Android application's functionality.

## 9. Dynamic Dashboard

The dashboard now retrieves information dynamically from Firebase.

It displays:

* User profile information
* User greeting
* Ground Floor active devices
* First Floor active devices

Active devices include devices with statuses:

```text
ON
ONLINE
CONNECTED
```

---

# 10. Dynamic Floor Information

Ground Floor and First Floor screens now retrieve device information from Firebase.

The application dynamically calculates:

* Number of rooms
* Total devices
* Active devices
* Devices in each room
* Currently active devices in each room

Example:

```text
Living Room
5 devices • 2 currently ON
```

The values are updated based on Firebase data instead of static text.

---

# 11. Dynamic Room Power

The Room screen now calculates simulated power consumption based on active devices.

For example:

```text
Device ON
   ↓
Active device detected
   ↓
Simulated power consumption
   ↓
12 W per active device
```

Therefore, the displayed power value changes dynamically according to the current device status.

---

# 12. Reports

A Reports Activity was added to the Android application.

The Reports screen retrieves device information from both floors and provides:

* Device status information
* Total simulated energy usage
* Per-device simulated energy usage
* Device information from the entire house

The report is based on the current Firebase device data.

---

# 13. Recent Activity

The **Recent Activity** section was restored on the Android dashboard.

It provides a place to display recent smart home device activity.

---

# 14. Camera Android Screens

Camera monitoring screens were added to the Android application.

### Added components

* Camera Activity
* Camera layout
* Camera status UI
* Camera preview UI
* Camera information UI
* Camera control buttons
* Living Room camera mock image
* Garage camera mock image

The cameras are simulated because this project does not use physical camera hardware.

---

# 15. Firebase Repository Updates

The Firebase Repository was updated to support:

* Reading device information
* Listening to devices in real time
* Updating device states
* Listening to all devices on a floor
* Reading safety configuration
* Writing iron safety configuration
* Reading scheduling configuration
* Writing scheduling configuration

This allows Android and the Hardware Simulator to communicate through Firebase.

---

# 16. Android Scheduling Integration

The Android scheduling functionality was updated so schedule settings can be stored in Firebase.

The application can save:

```text
scheduleEnabled
scheduleOnTime
scheduleOffTime
```

The Hardware Simulator can then read these settings and execute the schedule.

---

# 17. Firebase Cloud Functions

Firebase Cloud Functions were initialized for the project to support server-side automation.

The purpose of the Cloud Functions component is to allow safety and automation rules to run independently from the Android application.

The project was configured using:

```text
Firebase CLI
Firebase Functions
JavaScript
```

The Cloud Functions code was also checked using ESLint before deployment.

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

# Smart Home Data Flow

The overall system works using Firebase as the communication layer:

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
```

For automation:

```text
Firebase Device Data
        │
        ▼
Hardware Simulator
        │
        ├── Iron Safety Timer
        │
        └── Light Scheduling
```

---

# Testing Performed

The following functionality was tested:

* Firebase connection
* Device ON/OFF control
* Real-time Firestore updates
* Living Room Light
* TV Power Outlet
* Kitchen Light
* Kitchen Iron
* Garage Light
* Master Bedroom Light
* Multi-Switch Panel
* Bedroom Light
* Bathroom Light
* Camera simulation
* Dynamic active-device counts
* Dynamic room information
* Dynamic power calculation
* Reports screen
* Firebase scheduling configuration
* Iron safety configuration
* Android-to-Firebase communication
* Firebase-to-Simulator communication

Example simulator console output:

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

---

# GitHub Contribution

All simulator and related Android changes were developed on the personal feature branches:

```text
safiya-simulator
safiya-App-update
```

Changes were committed and pushed to the team repository for integration with the main project.

---

# Conclusion

The Hardware Simulator provides a cloud-connected simulation of the Smart Home system. It allows the Android application and simulated hardware to communicate through Firebase Firestore.

The contribution includes device control, real-time synchronization, camera simulation, multi-switch control, iron safety automation, light scheduling, dynamic device monitoring, reports, room power calculation, and supporting Android/Firebase integration.
