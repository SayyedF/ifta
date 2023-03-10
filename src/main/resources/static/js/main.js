$(document).ready(function () {
    connect();
});

/*$('#fmodal').on('show.bs.modal', function () {
    $.get("/modal/show/56/2", function (data) {
        $('#fmodal').find('.modal-content').html(data);
    })
});*/

function showFatwaModal(fatwaId, muftiId) {
    $.get("/modal/show/"+fatwaId+"/"+muftiId, function (data) {
        $('#fmodal').find('.modal-content').html(data);
        //$('#fmodal').modal();
        jQuery('#fmodal').modal();
    })
}

var stompClient = null;
var userName = null;//$("#from").val();

function connect() {
    userName = $("meta[name='_username']").attr("content");//$("#from").val();
    var isMufti = $("meta[name='_isMufti']").attr("content");

    if(userName == undefined) {
        return;
    }
    if (userName == null || userName === "") {
        alert('Please input a nickname!');
        return;
    }

    //if(isMufti == 'yes') {
        var socket = new SockJS('/broadcast');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function () {
            stompClient.subscribe('/user/topic/notifications', function (output) {
                showNotification(createAlertNode(JSON.parse(output.body)));
            });
            stompClient.subscribe('/user/topic/counter', function (output) {
                updateNotificationBadge(JSON.parse(output.body));
            });
            stompClient.subscribe('/user/topic/seen', function (output) {
                removeNotification(JSON.parse(output.body));
            });
        }, function (err) {
            alert('error: ' + err);
        });
    //}
}

function disconnect() {
    if (stompClient != null) {
        sendConnection(' disconnected from server');

        stompClient.disconnect(function() {
            console.log('disconnected...');
            setConnected(false);
        });
    }
}

function sendConnection(message) {
    var text = userName + message;
    sendBroadcast({'from': 'server', 'text': text});
}

function sendBroadcast(json) {
    stompClient.send("/app/broadcast", {}, JSON.stringify(json));
}

function send() {
    var text = $("#message").val();
    sendBroadcast({'from': userName, 'text': text});
    $("#message").val("");
}

function createTextNode(messageObj) {
    return '<div class="row alert alert-info"><div class="col-md-8">' +
        messageObj.text +
        '</div><div class="col-md-4 text-right"><small>[<b>' +
        messageObj.from +
        '</b> ' +
        messageObj.time +
        ']</small>' +
        '</div></div>';
}

function createAlertNode(notification) {
    return '<li id="n'+ notification.id +'">\n' +
    ' <a href="javascript:void()">\n' +
    '  <span class="mr-3 avatar-icon bg-success-lighten-2"><i class="icon-present"></i></span>\n' +
    '   <div class="notification-content">\n' +
    '    <h6 class="notification-heading">' + notification.heading + '</h6>\n' +
    '    <span class="notification-text">' + notification.text + '</span> \n' +
    '   </div>\n' +
    '  </a>\n' +
    '</li>';
}

function removeNotification(notification) {
    $('#'+notification.id).remove();
}

function showNotification(alert) {
    $("#notifications").append(alert);
}

function updateNotificationBadge(counter) {
    $("#count").text(counter.count);
}

function showBroadcastMessage(message) {
    $("#content").html($("#content").html() + message);
    $("#clear").show();
}

function clearBroadcast() {
    $("#content").html("");
    $("#clear").hide();
}
