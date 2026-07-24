// ==========================================
// Firebase Configuration
// Smart Home Hardware Simulator
// ==========================================


// Your Firebase project configuration

const firebaseConfig = {

    apiKey: "AIzaSyBgZtSqcqit47FB7ja2osmjn05JvuA7ZLc",

    authDomain:
    "smarthome-scs3311-teampds.firebaseapp.com",

    projectId:
    "smarthome-scs3311-teampds",

    storageBucket:
    "smarthome-scs3311-teampds.firebasestorage.app",

    messagingSenderId:
    "83836093540",

    appId:
    "1:83836093540:android:1f687049325c426bbecb71"

};


// Initialize Firebase

firebase.initializeApp(firebaseConfig);


// Firestore reference

const db = firebase.firestore();


console.log(
    "Firebase Connected Successfully"
);