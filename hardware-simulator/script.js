// ==================================================
// Smart Home Hardware Simulator
// Firebase Firestore Connected Version
// ==================================================

// ===============================
// Local UI Status Update
// ===============================

window.updateStatus = function(id, status){

    const element = document.getElementById(id);

    if(!element){
        console.log("Element not found:", id);
        return;
    }


    element.innerHTML = status;


    element.classList.remove(
        "on",
        "off",
        "error",
        "disconnected"
    );


    if(status === "ON"){
        element.classList.add("on");
    }

    else if(status === "OFF"){
        element.classList.add("off");
    }

    else if(status === "ERROR"){
        element.classList.add("error");
    }

    else if(status === "DISCONNECTED"){
        element.classList.add("disconnected");
    }


    console.log(id,"changed to",status);

};

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


    livingLightStatus:
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
    "bathroomLight"


};





// ==================================================
// Update Device UI
// ==================================================


function changeUI(
    elementID,
    status
)
{


const element =
document.getElementById(elementID);



if(!element)
return;



element.innerHTML =
status;



element.classList.remove(
    "on",
    "off",
    "error",
    "disconnected"
);



switch(status)
{


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
async function(
deviceID,
htmlID,
status
)
{


try{


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
// ------------------------------


const paths = {


livingRoomLight:
[
"groundFloor",
"livingRoom"
],


tvOutlet:
[
"groundFloor",
"livingRoom"
],


securityCamera:
[
"groundFloor",
"livingRoom"
],


kitchenLight:
[
"groundFloor",
"kitchen"
],


kitchenIron:
[
"groundFloor",
"kitchen"
],


garageLight:
[
"groundFloor",
"garage"
],


garageCamera:
[
"groundFloor",
"garage"
],


masterBedroomLight:
[
"firstFloor",
"masterBedroom"
],


switchPanel1:
[
"firstFloor",
"masterBedroom"
],


bedroomLight:
[
"firstFloor",
"bedroom"
],


bedroomOutlet:
[
"firstFloor",
"bedroom"
],


bathroomLight:
[
"firstFloor",
"bathroom"
]


};





const location =
paths[deviceID];



if(!location)
{

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
firebase.firestore()

.collection("houses")

.doc("house1")

.collection("floors")

.doc(floor)

.collection("rooms")

.doc(room)

.collection("devices")

.doc(deviceID);







// Update Firebase


await deviceRef.update({

status:status

});





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

catch(error)
{

console.error(
"Firebase Update Error",
error
);


}



};







// ==================================================
// Read All Device Status
// ==================================================


async function loadDevices()
{


for(
const htmlID in devices
)
{


const deviceID =
devices[htmlID];



try{


let path;



const locations = {


livingRoomLight:
["groundFloor","livingRoom"],


tvOutlet:
["groundFloor","livingRoom"],


kitchenLight:
["groundFloor","kitchen"],


kitchenIron:
["groundFloor","kitchen"],


garageLight:
["groundFloor","garage"],


masterBedroomLight:
["firstFloor","masterBedroom"],


bedroomLight:
["firstFloor","bedroom"],


bedroomOutlet:
["firstFloor","bedroom"],


bathroomLight:
["firstFloor","bathroom"]


};



path =
locations[deviceID];



if(!path)
continue;



const snapshot =
await firebase.firestore()

.collection("houses")

.doc("house1")

.collection("floors")

.doc(path[0])

.collection("rooms")

.doc(path[1])

.collection("devices")

.doc(deviceID)

.get();





if(snapshot.exists)
{


const data =
snapshot.data();



changeUI(
htmlID,
data.status
);



}



}

catch(error)
{


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
function(status)
{


updateDevice(
"kitchenIron",
"ironStatus",
status
);



if(status==="ON")
{


setTimeout(()=>{


updateDevice(
"kitchenIron",
"ironStatus",
"OFF"
);



alert(
"⚠️ Kitchen Iron automatically turned OFF after 2 minutes"
);



},120000);



}


};







// ==================================================
// Multi Switch Update
// ==================================================


window.updateSwitch =
function(
deviceID,
switchName,
value
)
{


console.log(

deviceID +
" "
+
switchName
+
" = "
+
value

);



/*

Later connect:

switchPanel1
|
sub fields

*/


};








// Load Firebase data

loadDevices();





});