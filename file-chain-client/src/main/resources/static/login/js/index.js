$("#login-button").click(function (event) {
    event.preventDefault();
    var key = $('[name="key"]').val();
    if (!key) {
        return;
    }
    $('form').fadeOut(500);
    $('.wrapper').addClass('form-success');
    sendRequest(Config.EndPoints.login, {
        key: key
    }, function () {
        sessionStorage.setItem("authKey", key);
        setTimeout(function(){
            location = "/";
        }, 500);
    }, function () {
        $('.wrapper').removeClass('form-success');
        $('form').fadeIn(500);
        $('[name="key"]').val("");
        $('[name="key"]').focus();
    }, undefined, "get");
});

$(function () {
    if (!sessionStorage.getItem("authKey")) {
        $('.wrapper').removeClass('form-success');
        $('form').fadeIn(500);
    } else {
        location = "/admin/admin.jsp";
    }
});