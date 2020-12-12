window.onLoad();

function onSignIn(googleUser) {
    //var profile = googleUser.getBasicProfile();
    var id_token = googleUser.getAuthResponse().id_token;

    // make an ajax post
    $.ajax({
        type: "POST",
        url: "/login",
        dataType: "json",
        data: JSON.stringify({ "id_token": id_token }),
        success: this.loginResponse,
        error: this.loginError
    });
}

function loginResponse(data) {
    if (data.mStatus === 'ok') {
        if (typeof(Storage) !== 'undefined') {
            localStorage.uId = data.mData.uId;
            localStorage.sessionKey = data.mData.sessionKey
        } else {
            console.log("Your browser doesn't support web storage");
            return;
        }
        // go to search page
        window.location.href = "/profile.html";
    } else if (data.mStatus === 'error') {
        loginError();
        console.log("The server replied with an error");
    } else {
        loginError();
        console.log("Unknown internal error");
    }
}

function loginError() {
    localStorage.uId = 0;
    localStorage.sessionKey = 0;
}

function signOut() {
    var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {
        console.log('User signed out.');
        auth2.disconnect();
    });
    auth2.disconnect();
    // go to main page
    window.location.href = "/index.html";
}

function onLoad() {
    gapi.load('auth2', function () {
        gapi.auth2.init();
    });
}

function profile() {
    window.location.href = "/profile.html";
}
function search() {
    window.location.href = "/search.html";
}
function openForm(){
    console.log("edit profile");
    document.getElementById("myForm").style.display = "block";
}
  
function closeForm() {
    console.log("Sending Info");
    document.getElementById("myForm").style.display = "none";

    // get user's input
    let name = document.getElementById("name");
    let gender = document.getElementById("gender");
    let tidiness = document.getElementById("tidiness");
    let noise = document.getElementById("noise");
    let pet = document.getElementById("pet");
    let visitor = document.getElementById("visitor");
    let sleep = document.getElementById("sleep");
    let wake = document.getElementById("wake");
    let hobby = document.getElementById("hobby");

    // make an ajax put
    $.ajax({
        type: "PUT",
        url: "/profile/" + localStorage.uId,
        dataType: "json",
        // uid, req.uName, req.uGender, req.uTidiness, req.uNoise, req.uSleepTime, req.uWakeTime, req.uPet, req.uVisitor, req.uHobby
        data: JSON.stringify({ "uid": localStorage.uId, "uName": name.value, "uGender": gender.value, "uTidiness": tidiness.value, "uNoise": noise.value, "uSleepTime": sleep.value, "uWakeTime": wake.value, "uPet": pet.value, "uVisitor": visitor.value, "uHobby": hobby.value }),
        success: this.loginResponse,
        error: this.loginError
    });

    window.location.reload();
}
