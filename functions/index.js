const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

const db = admin.firestore();


exports.ironAutoOff = functions.firestore
    .document(
        "houses/house1/floors/groundFloor/rooms/" +
        "kitchen/devices/kitchenIron",
    )
    .onUpdate(async (change) => {
      const after = change.after.data();

      if (after.status === "ON") {
        console.log("Iron turned ON");

        setTimeout(async () => {
          const ironRef = db.doc(
              "houses/house1/floors/groundFloor/rooms/" +
              "kitchen/devices/kitchenIron",
          );

          await ironRef.update({
            status: "OFF",
          });

          console.log("Iron automatically turned OFF");
        }, 120000);
      }

      return null;
    });
