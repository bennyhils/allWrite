if (!sessionStorage.getItem("authKey")) {
    location = "/login";
}
DateTimePickerFormat = "YYYY.MM.DD HH:mm:ss";

(function ($) {
    var clientList = [];
    var inList = [];
    var outList = [];

    function init() {
        updateData();
        setInterval(function () {
            updateData();
        }, 3000);

    }
    function updateData() {
        sendRequest(Config.ServerHost, Config.EndPoints.getInList, {}, function (data) {
            handleInList(data);
            console.log("inList updated");
        });
        sendRequest(Config.ServerHost, Config.EndPoints.getOutList, {}, function (data) {
            handleOutList(data);
            console.log("outList updated");
        });
        sendRequest(Config.TrackerHost, Config.EndPoints.getClientsList, {}, function (data) {
            handleClientList(data);
            console.log("clientList updated");
        });
    }



    function handleInList(list) {

        inList = list;
    }

    function handleOutList(list) {

        outList = list;
    }

    function initFileSend() {
        $('#file-input').trigger('click');
    }

    function handleClientList(list) {
        function buildUser1(user) {
            let userElem = $('<li>', {
                address: user.address
            });
            let avatar = $('<img>', {
                src: "dist/img/avatar/" + usersCache[user.address].avatar + ".svg"
            });
            let address = $('<a>', {
                class: "users-list-name",
                href: "#"
            }).text(user.address);
            let btn = $('<button>', {
                type: "button",
                class: "btn btn-block btn-primary",
            }).append($('<i>', {
                class: "fa fa-external-link"
            })).append(" Отправить файл");
            btn.click(initFileSend);
            let overlay = $('<div>', {
                class: "overlay"
            }).append(btn);

            userElem.append(avatar);
            userElem.append(address);
            userElem.append(overlay);
            userElem.data("address", user.address);
            return userElem;
        }

        var currentUserList = $(".users-list");
        let currentUsers = currentUserList.find("li");
        var usersCache = ObjectCache.get("usersCache") || {};

        $(".label1").text(list.length);

        for (let i = 0; i < currentUsers.length; ++i) {
            if (!findUserByAddress($(currentUsers[i]).data("address"), list)) {
                $(currentUsers[i]).fadeOut({
                    duration: 300,
                    complete: function () {
                        $(this).remove();
                    }
                })
            }
        }
        for (let i = 0; i < list.length; ++i) {
            if (!usersCache[list[i].address]) {
                usersCache[list[i].address] = list[i];
                usersCache[list[i].address].avatar = parseInt(i % 11 + 1);
            }
            if (!currentUserList.find('[address="'+list[i].address+'"]').length) {
                currentUserList.append(buildUser1(list[i]));
            }
        }

        ObjectCache.set("usersCache", usersCache);

        clientList = list;
    }



    $('.selectpicker').selectpicker();

    addInitFunction(function () {
        console.log("Admin panel initialized");
    });

    addInitFunction(function () {
        console.log("Update data");
        init();
    });

}(jQuery));
