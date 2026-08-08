// ==================================================
// Smart Home Hardware Simulator
// Firebase Firestore Connected Version
// ==================================================

// ==================================================
// Firestore Device Locations
// ==================================================

const DEVICE_PATHS = {

    livingRoomLight: ["groundFloor", "livingRoom"],

    tvOutlet: ["groundFloor", "livingRoom"],

    securityCamera: ["groundFloor", "livingRoom"],

    kitchenLight: ["groundFloor", "kitchen"],

    kitchenIron: ["groundFloor", "kitchen"],

    garageLight: ["groundFloor", "garage"],

    garageCamera: ["groundFloor", "garage"],

    masterBedroomLight: ["firstFloor", "masterBedroom"],

    switchPanel1: ["firstFloor", "masterBedroom"],

    bedroomLight: ["firstFloor", "bedroom"],

    bedroomOutlet: ["firstFloor", "bedroom"],

    bathroomLight: ["firstFloor", "bathroom"]

};

// Global cache for device configurations (used for schedules)
window.globalDeviceConfigs = {};

// Wait until page loads

document.addEventListener("DOMContentLoaded", () => {


    console.log(
        "Smart Home Simulator Started"
    );



    // ==================================================
    // Device Mapping
    // HTML ID  -> Firestore Device ID
    // ==================================================


    const devices = {


        livingRoomLightStatus:
            "livingRoomLight",


        tvOutletStatus:
            "tvOutlet",


        kitchenLightStatus:
            "kitchenLight",


        ironStatus:
            "kitchenIron",


        garageLightStatus:
            "garageLight",


        masterBedroomLightStatus:
            "masterBedroomLight",


        bedroomLightStatus:
            "bedroomLight",


        bedroomOutletStatus:
            "bedroomOutlet",


        bathroomLightStatus:
            "bathroomLight",

        securityCameraStatus:
            "securityCamera",

        garageCameraStatus:
            "garageCamera",

        switchPanelStatus:
            "switchPanel1"
    };

    // ==================================================
    // Schedule Checker (Runs every minute)
    // ==================================================
    setInterval(() => {
        const now = new Date();
        const currentTime = now.getHours().toString().padStart(2, '0') + ":" + 
                            now.getMinutes().toString().padStart(2, '0');

        for (const [path, data] of Object.entries(window.globalDeviceConfigs)) {
            if (data.scheduleEnabled) {
                const pathParts = path.split('/');
                const deviceID = pathParts[pathParts.length - 1];
                
                let htmlID = "";
                for (const [key, val] of Object.entries(devices)) {
                    if (val === deviceID) { htmlID = key; break; }
                }

                if (data.scheduleOnTime === currentTime && data.status !== "ON") {
                    console.log(`Schedule triggered: Turning ON ${deviceID}`);
                    window.updateDevice(deviceID, htmlID, 'ON');
                } else if (data.scheduleOffTime === currentTime && data.status !== "OFF") {
                    console.log(`Schedule triggered: Turning OFF ${deviceID}`);
                    window.updateDevice(deviceID, htmlID, 'OFF');
                }
            }
        }
    }, 60000); // Check every 60 seconds



    // ==================================================
    // Update Device UI
    // ==================================================


    function changeUI(
        elementID,
        status
    ) {


        const element =
            document.getElementById(elementID);



        if (!element)
            return;



        element.innerHTML =
            status;



        element.classList.remove(
            "on",
            "off",
            "error",
            "disconnected"
        );



        switch (status) {


            case "ON":

                element.classList.add(
                    "on"
                );

                break;



            case "OFF":

                element.classList.add(
                    "off"
                );

                break;



            case "ERROR":

                element.classList.add(
                    "error"
                );

                break;



            case "DISCONNECTED":

                element.classList.add(
                    "disconnected"
                );

                break;



        }



    }





    // ==================================================
    // Update Firestore Device Status
    // ==================================================


    window.updateDevice =
        async function (
            deviceID,
            htmlID,
            status
        ) {


            try {


                /*
                
                Firestore Path:
                
                houses
                 |
                 house1
                 |
                 floors
                 |
                 floor
                 |
                 rooms
                 |
                 room
                 |
                 devices
                 |
                 deviceID
                
                */


                let deviceRef;



                // ------------------------------
                // Find Device Path
                // -----------------------------
                
                
                const location =
                    DEVICE_PATHS[deviceID];



                if (!location) {

                    console.log(
                        "Device path not found"
                    );

                    return;

                }




                const floor =
                    location[0];


                const room =
                    location[1];




                deviceRef =
                    db

                        .collection("houses")

                        .doc("house1")

                        .collection("floors")

                        .doc(floor)

                        .collection("rooms")

                        .doc(room)

                        .collection("devices")

                        .doc(deviceID);







                // Update Firebase
await deviceRef.set(
    {
        status: status
    },
    {
        merge: true
    }
);



                // Update UI


                changeUI(
                    htmlID,
                    status
                );



                console.log(
                    deviceID +
                    " changed to "
                    +
                    status
                );



            }

            catch (error) {

                console.error(
                    "Firebase Update Error",
                    error
                );


            }



        };







    // ==================================================
    // Read All Device Status
    // ==================================================


    async function loadDevices() {


        for (
            const htmlID in devices
        ) {


            const deviceID =
                devices[htmlID];



            try {


                let path;



                path =
                    DEVICE_PATHS[deviceID];



                if (!path)
                    continue;



                const snapshot =
                    await db

                        .collection("houses")

                        .doc("house1")

                        .collection("floors")

                        .doc(path[0])

                        .collection("rooms")

                        .doc(path[1])

                        .collection("devices")

                        .doc(deviceID)

                        .get();





                if (snapshot.exists) {


                    const data =
                        snapshot.data();



                    changeUI(
                        htmlID,
                        data.status
                    );



                }



            }

            catch (error) {


                console.log(
                    error
                );


            }


        }


    }







    // ==================================================
    // Iron Safety Feature
    // ==================================================


    window.ironControl =
        function (status) {
            updateDevice(
                "kitchenIron",
                "ironStatus",
                status
            );
        };







    // ==================================================
    // Multi Switch Update
    // ==================================================
    window.updateSwitch = async function (
    deviceID,
    switchName,
    value
) {

    try {

        const deviceRef = db
            .collection("houses")
            .doc("house1")
            .collection("floors")
            .doc("firstFloor")
            .collection("rooms")
            .doc("masterBedroom")
            .collection("devices")
            .doc(deviceID);

            await deviceRef.set(
    {
        [switchName]: value,
        status: value ? "ON" : "OFF"
    },
    {
        merge: true
    }
);

        console.log(
            switchName + " changed to " + value
        );

    }

    catch (error) {

        console.error(error);

    }

};



    // Load Firebase data

    loadDevices();





});
// ======================================
// Firebase Realtime Device Listener
// ======================================

function listenDevice(path, statusId) {

    db.doc(path).onSnapshot((doc) => {
        if (doc.exists) {
            let data = doc.data();
            let status = data.status;
            
            // Cache data for schedules
            window.globalDeviceConfigs[path] = data;

            let element = document.getElementById(statusId);

            if (element) {
                element.innerHTML = status;

                element.classList.remove(
                    "on",
                    "off",
                    "error",
                    "disconnected"
                );


                if (status === "ON" || status === "ONLINE") {
                    element.classList.add("on");
                }

                else if (status === "OFF") {
                    element.classList.add("off");
                }

                else if (status === "DISCONNECTED") {
                    element.classList.add("disconnected");
                }

                else if (status === "ERROR") {
                    element.classList.add("error");
                }
            }
            
            // ==========================================
            // Iron Safety Watcher
            // ==========================================
            if (statusId === "ironStatus") {
                if (status === "ON" && data.safetyEnabled === true) {
                    if (window.ironSafetyTimer) clearTimeout(window.ironSafetyTimer);
                    
                    const maxDuration = data.maxOnDuration || 1; // Default to 1 min if not set
                    
                    window.ironSafetyTimer = setTimeout(() => {
                        window.updateDevice("kitchenIron", "ironStatus", "OFF");
                        console.log(`Iron safety triggered: Automatically turned OFF after ${maxDuration} minutes`);
                        alert(`⚠️ Kitchen Iron automatically turned OFF after ${maxDuration} minute(s)`);
                    }, maxDuration * 60 * 1000);
                    
                } else if (status === "OFF") {
                    if (window.ironSafetyTimer) clearTimeout(window.ironSafetyTimer);
                }
            }

        }

    });

}
listenDevice(
    "houses/house1/floors/groundFloor/rooms/livingRoom/devices/livingRoomLight",
    "livingRoomLightStatus"
);


listenDevice(
    "houses/house1/floors/groundFloor/rooms/kitchen/devices/kitchenIron",
    "ironStatus"
);


listenDevice(
    "houses/house1/floors/groundFloor/rooms/garage/devices/garageCamera",
    "garageCameraStatus"
);


listenDevice(
    "houses/house1/floors/firstFloor/rooms/bedroom/devices/bedroomLight",
    "bedroomLightStatus"
);

listenDevice(
    "houses/house1/floors/groundFloor/rooms/livingRoom/devices/tvOutlet",
    "tvOutletStatus"
);

listenDevice(
    "houses/house1/floors/groundFloor/rooms/livingRoom/devices/securityCamera",
    "securityCameraStatus"
);

listenDevice(
    "houses/house1/floors/groundFloor/rooms/kitchen/devices/kitchenLight",
    "kitchenLightStatus"
);

listenDevice(
    "houses/house1/floors/groundFloor/rooms/garage/devices/garageLight",
    "garageLightStatus"
);

listenDevice(
    "houses/house1/floors/firstFloor/rooms/masterBedroom/devices/masterBedroomLight",
    "masterBedroomLightStatus"
);

listenDevice(
    "houses/house1/floors/firstFloor/rooms/bedroom/devices/bedroomOutlet",
    "bedroomOutletStatus"
);

listenDevice(
    "houses/house1/floors/firstFloor/rooms/bathroom/devices/bathroomLight",
    "bathroomLightStatus"
);
listenDevice(
    "houses/house1/floors/firstFloor/rooms/masterBedroom/devices/switchPanel1",
    "switchPanelStatus"
);