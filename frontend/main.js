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
        window.location.href = "/search.html";
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
    document.getElementById("myForm").style.display = "none";
}
