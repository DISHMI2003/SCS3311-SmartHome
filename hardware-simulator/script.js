// ==========================================
// Smart Home Hardware Simulator
// script.js
// ==========================================


// Wait until HTML page loads completely

document.addEventListener("DOMContentLoaded", () => {


    // ======================================
    // Device Status Update
    // ======================================


    window.updateStatus = function(id, status) {


        const device =
            document.getElementById(id);


        if(!device){
            return;
        }


        // Change text

        device.innerHTML = status;



        // Remove previous status colors

        device.classList.remove(
            "on",
            "off",
            "error",
            "disconnected"
        );



        // Add new status color

        switch(status){


            case "ON":

                device.classList.add("on");

                break;



            case "OFF":

                device.classList.add("off");

                break;



            case "ERROR":

                device.classList.add("error");

                break;



            case "DISCONNECTED":

                device.classList.add("disconnected");

                break;

        }



        console.log(
            id + " changed to " + status
        );


        // Future Firebase update

        /*
        updateFirebaseDevice(
            id,
            status
        );
        */

    };





    // ======================================
    // Iron Safety Function
    // ======================================


    window.ironControl = function(status){



        updateStatus(
            "ironStatus",
            status
        );



        if(status === "ON"){


            console.log(
                "Iron timer started"
            );



            setTimeout(()=>{


                updateStatus(
                    "ironStatus",
                    "OFF"
                );



                alert(
                    "⚠️ Safety Alert\n\nIron automatically turned OFF after 2 minutes."
                );



                console.log(
                    "Iron auto OFF"
                );



                // Future Firebase notification

                /*
                sendNotification(
                   "Iron turned OFF automatically"
                );
                */


            },120000);



        }



    };





    // ======================================
    // Multi Switch Panel
    // ======================================


    const switches = [

        "switch1",
        "switch2",
        "switch3"

    ];



    switches.forEach((switchId)=>{


        const toggle =
            document.getElementById(switchId);



        if(toggle){


            toggle.addEventListener(
                "change",
                ()=>{


                    let status =
                    toggle.checked
                    ? "ON"
                    : "OFF";



                    console.log(
                        switchId +
                        " : " +
                        status
                    );



                    /*
                    Future Firebase:

                    updateSwitch(
                       switchId,
                       status
                    );

                    */


                }
            );


        }


    });






    // ======================================
    // Camera Control
    // ======================================



    window.cameraOnline=function(id){


        const camera =
        document.getElementById(id);



        if(!camera)
            return;



        camera.innerHTML="ONLINE";



        camera.classList.remove(
            "off",
            "disconnected"
        );



        camera.classList.add(
            "on"
        );


    };





    window.cameraOffline=function(id){



        const camera =
        document.getElementById(id);



        if(!camera)
            return;



        camera.innerHTML=
        "DISCONNECTED";



        camera.classList.remove(
            "on",
            "off"
        );



        camera.classList.add(
            "disconnected"
        );


    };





    // ======================================
    // Initial Device States
    // ======================================



    const devices=[


        "livingLightStatus",

        "tvOutletStatus",

        "kitchenLightStatus",

        "ironStatus",

        "garageLightStatus",

        "masterLightStatus",

        "bedroomLightStatus",

        "bedroomOutletStatus",

        "bathroomLightStatus"


    ];




    devices.forEach((device)=>{


        const element =
        document.getElementById(device);



        if(element){


            element.classList.remove(
                "on",
                "error",
                "disconnected"
            );


            element.classList.add(
                "off"
            );


        }


    });




    console.log(
        "Smart Home Simulator Loaded Successfully"
    );



});
