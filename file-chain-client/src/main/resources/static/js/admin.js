if (!sessionStorage.getItem("authKey")) {
    location = "/login/index.html";
}
DateTimePickerFormat = "YYYY.MM.DD HH:mm:ss";

(function ($) {
    var clientList = [];
    var inList = [];
    var outList = [];
    window.to = {
        address: "",
        publicKey: ""
    };
    function init() {
        updateData();
        setInterval(function () {
            updateData();
        }, 3000);
        $('#file-input').on("change", function () {
            var value = $(this).val();
            value = value.substr(value.lastIndexOf("\\")+1);
            sendRequest(Config.EndPoints.uploadRequest + "?fileLocalPath=" + value, JSON.stringify(to), function (data) {
                updateData();
                $('[href="#tab-3"]').click();
            }, undefined, undefined);
        })
    }
    function updateData() {
        sendRequest(Config.EndPoints.getInList, {}, function (data) {
            handleInList(data);
            console.log("inList updated");
        }, undefined, undefined, "get");
        sendRequest(Config.EndPoints.getOutList, {}, function (data) {
            handleOutList(data);
            console.log("outList updated");
        }, undefined, undefined, "get");
        sendRequest(Config.EndPoints.getClientsList, {}, function (data) {
            handleClientList(data);
            console.log("clientList updated");
        }, undefined, undefined, "get");
    }

    function handleInList(list) {
        function updateFile(file) {
            let fileElem = $('[fileId="'+file.id+'"]');
            fileElem.removeClass("bg-yellow");
            fileElem.removeClass("bg-aqua");
            fileElem.removeClass("bg-green");
            fileElem.removeClass("bg-red");
            fileElem.attr("status", file.status);
            if (file.status == 0) {
                fileElem.addClass("bg-yellow");
            } else if (file.status == 1) {
                fileElem.addClass("bg-aqua");
            } else if (file.status == 2) {
                fileElem.addClass("bg-green");
            } else if (file.status == 3) {
                fileElem.addClass("bg-red");
            }

            if (file.status == 1) {
                fileElem.find(".info-box-icon i").hide();
                fileElem.find(".info-box-icon").addClass("lds-hourglass");
            } else {
                fileElem.find(".info-box-icon i").show();
                fileElem.find(".info-box-icon").removeClass("lds-hourglass");
            }

            fileElem.find(".info-box-icon i").removeClass("fa-download").removeClass("fa-files-o")
                .addClass(file.status == 0 ? "fa-download" : "fa-files-o");


            let text1 = fileElem.find('.info-box-text');
            let text2 = fileElem.find('.info-box-number');
            let text3 = fileElem.find('.progress-description');
            if (file.status == 0) {
                text1.text("Ожидает подтверждения");
            } else if (file.status == 1) {
                text1.text("Идет загрузка");
            } else if (file.status == 2) {
                text1.text("Загружен");
            } else if (file.status == 3) {
                text1.text("Ошибка при загрузке");
            }
            let progress = fileElem.find('.progress .progress-bar');
            progress.css({
                width: file.progress + "%"
            });
            text2.text(file.name);
            text3.text(formatSpeed(file.speed));
        }
        function buildFile(file) {
            let fileElem = $('<div>', {
                class: "info-box",
                fileId: file.id
            });
            if (file.status == 0) {
                fileElem.addClass("bg-yellow");
            } else if (file.status == 1) {
                fileElem.addClass("bg-aqua");
            } else if (file.status == 2) {
                fileElem.addClass("bg-green");
            } else if (file.status == 3) {
                fileElem.addClass("bg-red");
            }
            fileElem.attr("status", file.status);
            let icon = $('<span>', {
                class: "info-box-icon"
            }).append($('<i>', {
                class: file.status == 0 ? "fa fa-download" : "fa fa-files-o"
            }));
            icon.click(function () {
                sendRequest(Config.EndPoints.acceptFile, {
                    requestFileInfo: file
                }, function () {
                    updateData();
                })
            });
            let content = $('<div>', {
                class: "info-box-content"
            });
            let text1 = $('<span>', {
                class: "info-box-text"
            });
            let text2 = $('<span>', {
                class: "info-box-number"
            });
            if (file.status == 0) {
                text1.text("Ожидает подтверждения");
            } else if (file.status == 1) {
                text1.text("Идет загрузка");
            } else if (file.status == 2) {
                text1.text("Загружен");
            } else if (file.status == 3) {
                text1.text("Ошибка при загрузке");
            }
            text2.text(file.name);
            let progress = $('<div>', {
                class: "progress"
            }).append($('<div>', {
                class: "progress-bar"
            }).css({
                    width: file.progress + "%"
            }));
            let text3 = $('<span>', {
                class: "progress-description"
            }).text(formatSpeed(file.speed));
            content.append(text1).append(text2).append(progress).append(text3);
            fileElem.append(icon);
            fileElem.append(content);
            fileElem.data("fileId", file.id);
            if (file.status == 1) {
                fileElem.find(".info-box-icon i").hide();
                fileElem.find(".info-box-icon").addClass("lds-hourglass");
            } else {
                fileElem.find(".info-box-icon i").show();
                fileElem.find(".info-box-icon").removeClass("lds-hourglass");
            }
            return fileElem;
        }

        var currentFilesList = $(".files-list1");
        let currentFiles = currentFilesList.find(".info-box");
        var usersCache = ObjectCache.get("usersCache") || {};

        $(".label2").text(list.length);

        for (let i = 0; i < currentFiles.length; ++i) {
            if (!findFileById($(currentFiles[i]).data("fileId"), list)) {
                $(currentFiles[i]).fadeOut({
                    duration: 300,
                    complete: function () {
                        $(this).remove();
                    }
                })
            } else {
                updateFile(findFileById($(currentFiles[i]).data("fileId"), list));
            }
        }
        for (let i = 0; i < list.length; ++i) {
            if (!currentFilesList.find('[fileId="'+list[i].id+'"]').length) {
                currentFilesList.append(buildFile(list[i]));
            }
        }

        ObjectCache.set("usersCache", usersCache);


        inList = list;
    }

    function handleOutList(list) {
        function updateFile(file) {
            let fileElem = $('[fileId="'+file.id+'"]');
            fileElem.removeClass("bg-yellow");
            fileElem.removeClass("bg-aqua");
            fileElem.removeClass("bg-green");
            fileElem.removeClass("bg-red");
            if (file.status == 0) {
                fileElem.addClass("bg-yellow");
            } else if (file.status == 1) {
                fileElem.addClass("bg-aqua");
            } else if (file.status == 2) {
                fileElem.addClass("bg-green");
            } else if (file.status == 3) {
                fileElem.addClass("bg-red");
            }

            fileElem.find(".info-box-icon i").removeClass("fa-upload").removeClass("fa-files-o")
                .addClass(file.status == 0 ? "fa-download" : "fa-files-o");

            let text1 = fileElem.find('.info-box-text');
            let text2 = fileElem.find('.info-box-number');
            let text3 = fileElem.find('.progress-description');
            if (file.status == 0) {
                text1.text("Ожидает подтверждения от " + file.receiver.address);
            } else if (file.status == 1) {
                text1.text("Идет загрузка");
            } else if (file.status == 2) {
                text1.text("Отправлен");
            } else if (file.status == 3) {
                text1.text("Ошибка при отправке");
            }
            let progress = fileElem.find('.progress .progress-bar');
            progress.css({
                width: file.progress + "%"
            });
            text2.text(file.name);
            text3.text(formatSpeed(file.speed));
        }
        function buildFile(file) {
            let fileElem = $('<div>', {
                class: "info-box",
                fileId: file.id
            });
            if (file.status == 0) {
                fileElem.addClass("bg-yellow");
            } else if (file.status == 1) {
                fileElem.addClass("bg-aqua");
            } else if (file.status == 2) {
                fileElem.addClass("bg-green");
            } else if (file.status == 3) {
                fileElem.addClass("bg-red");
            }

            let icon = $('<span>', {
                class: "info-box-icon"
            }).append($('<i>', {
                class: file.status == 0 ? "fa fa-upload" : "fa fa-files-o"
            }));
            let content = $('<div>', {
                class: "info-box-content"
            });
            let text1 = $('<span>', {
                class: "info-box-text"
            });
            let text2 = $('<span>', {
                class: "info-box-number"
            });
            if (file.status == 0) {
                text1.text("Ожидает подтверждения от " + file.receiver.address);
            } else if (file.status == 1) {
                text1.text("Идет загрузка");
            } else if (file.status == 2) {
                text1.text("Отправлен");
            } else if (file.status == 3) {
                text1.text("Ошибка при отправке");
            }
            let progress = $('<div>', {
                class: "progress"
            }).append($('<div>', {
                class: "progress-bar"
            }).css({
                width: file.progress + "%"
            }));
            let text3 = $('<span>', {
                class: "progress-description"
            }).text(formatSpeed(file.speed));
            content.append(text1).append(text2).append(progress).append(text3);
            fileElem.append(icon);
            fileElem.append(content);
            fileElem.data("fileId", file.id);
            return fileElem;
        }

        var currentFilesList = $(".files-list2");
        let currentFiles = currentFilesList.find(".info-box");
        var usersCache = ObjectCache.get("usersCache") || {};

        $(".label3").text(list.length);

        for (let i = 0; i < currentFiles.length; ++i) {
            if (!findFileById($(currentFiles[i]).data("fileId"), list)) {
                $(currentFiles[i]).fadeOut({
                    duration: 300,
                    complete: function () {
                        $(this).remove();
                    }
                })
            }
        }
        for (let i = 0; i < list.length; ++i) {
            if (!currentFilesList.find('[fileId="'+list[i].id+'"]').length) {
                currentFilesList.append(buildFile(list[i]));
            }
        }

        ObjectCache.set("usersCache", usersCache);


        inList = list;
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
            (function() {
                var item = user;
                btn.click(function() {
                    to = item;
                    initFileSend();
                });
            })();

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
