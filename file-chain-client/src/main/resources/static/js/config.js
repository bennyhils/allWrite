Config = {
    versionCode: 35,
    ServerHost: "",
    AjaxRequestJSONP: "jsonp_callback",
    AjaxRequestDataType: "json",
    AjaxRequestCrossDomain: true,
    AjaxRequestTimeOut: 10*1000,
    GoogleApiKey: "AIzaSyDFZmUv12xrYR8XUC7Td9UlXj2WPHSocZQ",
    EndPoints: {
        // login: "/server/login.json",
        // getInList: "/server/getInList.json",
        // getOutList: "/server/getOutList.json",
        // getClientsList: "/server/getClientList.json",
        // acceptFile: "/server/acceptFile.json",
        // uploadRequest: "/uploadRequest"
        login: "/auth",
        getInList: "/incoming/list",
        getOutList: "/outgoing/list",
        getClientsList: "/member/list",
        acceptFile: "/download",
        uploadRequest: "/uploadRequest"
    }


};