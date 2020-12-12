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
<<<<<<< HEAD

function onLoad() {
    gapi.load('auth2', function () {
        gapi.auth2.init();
    });
}

=======
function profile() {
    window.location.href = "/profile.html";
}
function search() {
    window.location.href = "/search.html";
}
>>>>>>> c7ea1370a9c12af61b251219ddb6476331f0aebb
function openForm(){
    console.log("edit profile");
    document.getElementById("myForm").style.display = "block";
}
  
function closeForm() {
    console.log("Sending Info");
    document.getElementById("myForm").style.display = "none";
<<<<<<< HEAD

    // make an ajax post
    $.ajax({
        type: "POST",
        url: "/profile/" + localStorage.uId,
        dataType: "json",
        // uid, req.uName, req.uGender, req.uTidiness, req.uNoise, req.uSleepTime, req.uWakeTime, req.uPet, req.uVisitor, req.uHobby
        data: JSON.stringify({  }),
        success: this.loginResponse,
        error: this.loginError
=======
    $('#uploadForm').submit(function(e) {
        e.preventDefault();
        $.ajax({
            url: '/echo/json/',
            data: $(this).serialize(),
            type: 'POST',
            success: function(data) {
                alert(data);
            }
        });
>>>>>>> c7ea1370a9c12af61b251219ddb6476331f0aebb
    });
}
