(function ($) {
    var InitFuncs = [];
    var initialized = false;
    window.addInitFunction = function (func) {
        if (initialized) {
            func();
        } else {
            var item = func;
            if (typeof func != "function") {
                item = Function(func);
            }
            InitFuncs.push(item);
        }
    };
    function beginInit() {
        for (var i = 0; i < InitFuncs.length; ++i) {
            InitFuncs[i]();
        }
        initialized = true;
    }
    window.InitConfig = {
        serverLog: false
    };
    $(function (e) {
        beginInit();
    });
    // addInitFunction(HistoryEngine.init);
    window.selectKaroTab = function (id) {
        var href = "#" + id;
        var $content = $(href);
        if ($content.length > 0) {
            $(href).parent().children(".karo-tab-content").each(function (index, elem) {
                if ($(elem).attr("id") != id) {
                    $('[karo-href="#' + $(elem).attr("id") + '"]').removeClass("active");
                    $(elem).trigger("karo-tab-before-hide");
                    $(elem).removeClass("active");
                    $(elem).trigger("karo-tab-after-hide");
                }
                $('[karo-href="' + href + '"]').addClass("active");
                $(elem).trigger("karo-tab-before-show");
                $(href).addClass("active");
                $(elem).trigger("karo-tab-after-show");
            });
        }
    };
    function onTabClick($tab) {
        var href = $tab.attr("karo-href");
        var id = href.substr(1);
        selectKaroTab(id);
    }

    window.initKaroTab = function (that) {
        var $tab = $(that).closest(".karo-tab");
        if ($tab.length > 0) {
            $tab.off("click");
            $tab.on("click", function () {
                nc();
                onTabClick($tab);
            });
        }
    };

    window.ClientCache = {
        get: function (key) {
            if (typeof localStorage == "undefined") {
                return null;
            }
            return localStorage.getItem(key);
        },
        set: function (key, value) {
            if (typeof localStorage == "undefined") {
                return;
            }
            var prevValue = this.get(key);
            localStorage.setItem(key, value);
            return prevValue;
        },
        remove: function (key) {
            if (typeof localStorage == "undefined") {
                return null;
            }
            return localStorage.removeItem(key);
        }
    };
    window.ObjectCache = {
        get: function (key) {
            var res = ClientCache.get(key);
            if (res == null) return null;
            return JSON.parse(res);
        },
        set: function (key, value) {
            var prevValue = this.get(key);
            ClientCache.set(key, JSON.stringify(value));
            return prevValue;
        },
        remove: function (key) {
            var prevValue = this.get(key);
            ClientCache.remove(key);
            return prevValue;
        }
    };
    window.LoadingHelper = {
        setLoading: function (text, timeout) {
            var textVisible = false;
            if (text) {
                textVisible = true;
            }
            $.mobile.loading("show", {
                text: text,
                textVisible: textVisible,
                textonly: false
            });
            if (timeout) setTimeout(LoadingHelper.unsetLoading, timeout);
        },
        unsetLoading: function () {
            $.mobile.loading("hide");
        },

        loadingOn: function (timeout) {
            $(".ui-header").addClass("loader-on");
            if (timeout) setTimeout(LoadingHelper.loadingOff, timeout);
        },
        loadingOff: function () {
            $(".ui-header").removeClass("loader-on");
        }

    };
    window.FormHelper = {
        getFormData: function (container) {
            var elems = $(container).find("input, select, textarea");
            var data = {};
            for (var i = 0; i < elems.length; ++i) {
                var elem = elems[i];
                var name = $(elem).attr("name");
                var value = $(elem).val();
                if (name && name.length > 0) {
                    data[name] = value;
                }
            }
            return data;
        }
    };

    window.Alerts = {
        success: function (msg) {
            this.show(msg, "success");
        },
        error: function (msg) {
            this.show(msg, "error");
        },
        warn: function (msg) {
            this.show(msg, "warn");
        },
        show: function (msg, className) {
            console.log(className + " msg: " + msg);
            $.notify(msg, {
                style: "law",
                className: className,
                autoHideDelay: 8000
            });
        }


    };

    window.sendRequest = function (endpoont, params, callbackSuccess, callbackError, callbackAlways) {
        var authKey = sessionStorage.getItem("authKey");
        $.ajax({
            url: Config.ServerHost + endpoont,
            dataType: Config.AjaxRequestDataType,
            method: "post",
            contentType: "application/json;charset=UTF-8",
            timeout: Config.AjaxRequestTimeOut,
            beforeSend: function(request) {
                request.setRequestHeader("auth_key", authKey);
            },
            data: params,
            success: function (response) {
                if (callbackSuccess) callbackSuccess(response);
            },
            error: function (response) {
                console.log("Error  sending request.");
                if (callbackError) callbackError();
            },
            complete: function () {
                if (callbackAlways) {
                    callbackAlways();
                }
            }
        });
    };

    window.generateId = function (prefix) {
        var lastUniqueId = ClientCache.get("lastUniqueId");
        if (lastUniqueId == null) {
            lastUniqueId = 1;
            ClientCache.set("lastUniqueId", lastUniqueId);
        } else {
            lastUniqueId = parseInt(lastUniqueId);
            lastUniqueId++;
            ClientCache.set("lastUniqueId", lastUniqueId);
        }
        prefix = prefix || "";
        return prefix + (lastUniqueId);
    };
    window.deepClone = function (obj) {
        return JSON.parse(JSON.stringify(obj));
    };
    window.getURLParam = function (name, url) {
        if (!url) url = window.location.href;
        name = name.replace(/[\[\]]/g, "\\$&");
        var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
            results = regex.exec(url);
        if (!results) return null;
        if (!results[2]) return '';
        return decodeURIComponent(results[2].replace(/\+/g, " "));
    };
    window.indexById = function (list, id) {
        return indexByAttrValue(list, "id", id);
    };
    window.indexByAttrValue = function (list, attr, value) {
        for (var i = 0; i < list.length; ++i) {
            if (value == list[i][attr]) return i;
        }
        return -1;
    };
    window.getSafeValue = function (value, def) {
        if (typeof value != "undefined") return value;
        return def;
    };


    var JQueryExtensions = {};
    JQueryExtensions.loadContent = function (blockId, params) {
        var that = this;
        this.addClass("loading-content");
        this.html("");
        sendRequest($.extend({
            requestProcessor: Config.RequestProcessors.BlockRequest,
            action: "getContent",
            blockId: blockId
        }, params), {
            onSuccess: function (response) {
                if (typeof response.content != "undefined") {
                    that.html(response.content);
                }
            },
            onAlways: function () {
                that.removeClass("loading-content");
            }
        });
    };
    $.fn.extend(JQueryExtensions);
})(jQuery);
