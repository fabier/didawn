function downloadAsync(url, filename, callback) {
    var oReq = new XMLHttpRequest();
    oReq.open("GET", url, true);
    oReq.responseType = "arraybuffer";
    oReq.onload = function (oEvent) {
        var arrayBuffer = oReq.response;
        var blob = new Blob([arrayBuffer], {type: "octet/stream"});
        var url = window.URL.createObjectURL(blob);
        var link = document.createElement('a');
        link.href = url;
        link.download = filename;
        link.click();
        if (callback) {
            callback();
        }
    };
    oReq.send();
}