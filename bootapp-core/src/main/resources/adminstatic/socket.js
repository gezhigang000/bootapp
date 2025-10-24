var wsUrl = 'ws://' + (document.domain || '127.0.0.1') + ':8080/ws/log';

var ws = null;
var heartbeatTimer = null;
var reconnect = true;
var heartbeatInterval = 15;
var reconnectTimeout = 2000;
var retryCount = 0;
var loggingAutoBottom = true;

function createWebSocket() {
    if ("WebSocket" in window) {
        ws = new WebSocket(wsUrl);
        ws.onopen = function () {
            console.log("WebSocket 已连接");
            retryCount = 0;
            startHeartbeat();
        };

        ws.onmessage = function (evt) {
            console.log("WebSocket 收到消息：" + evt.data);
            var loggingText = document.getElementById("loggingText");
            loggingText.innerHTML += event.data;
            if (loggingAutoBottom) {
                loggingText.scrollTop = loggingText.scrollHeight;
            }
        };

        ws.onerror = function (e) {
            console.log('WebSocket错误:', e);
        }

        ws.onclose = function () {
            console.log("WebSocket 已关闭");
            stopHeartbeat();
            if (reconnect) {
                retryCount++;
                console.log('wait:' + reconnectTimeout*Math.ceil(retryCount/3))
                setTimeout(function () {
                    console.log("WebSocket 尝试重新连接");
                    createWebSocket();
                }, reconnectTimeout*(retryCount/2+1));
            }
        };
    } else {
        console.log("该浏览器不支持 WebSocket");
    }
}

// 发送消息
function sendMessage(message) {
    if (ws != null && ws.readyState == WebSocket.OPEN) {
        ws.send(message);
        console.log("WebSocket 发送消息：" + message);
    } else {
        console.log("WebSocket 连接没有建立或已关闭");
    }
}

// 开始心跳定时器
function startHeartbeat(interval) {
    heartbeatTimer = setInterval(function () {
        sendMessage("heartbeat");
    }, heartbeatInterval * 1000);
}

function stopHeartbeat() {
    clearInterval(heartbeatTimer);
}

createWebSocket();



$("#_btn_clearLog").bind("click", function() {
    $("#loggingText").html('');
});
$("#_btn_scrollToBottom").bind("click", function() {
    var scrollHeight = $('#loggingText').prop("scrollHeight");
    $('#loggingText').scrollTop(scrollHeight);
});
$("#_btn_toggleAutoScroll").bind("click", function() {
    loggingAutoBottom = !loggingAutoBottom;
    $(this).text(loggingAutoBottom ? "关闭自动滚动" : "开启自动滚动");
});