<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <script src="http://code.jquery.com/jquery-1.11.2.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/0.3.4/sockjs.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
</head>

<body>
<p>

</p>

<div id="notifications-area"></div>

<!-- Javascript functions -->
<script>

    /**
     * Open the web socket connection and subscribe the "/notify" channel.
     */
    function connect() {

        // Create and init the SockJS object
        var socket = new SockJS('/umiclient/ws');
        var stompClient = Stomp.over(socket);

        // Subscribe the '/notify' channell
        stompClient.connect({}, function (frame) {
            stompClient.subscribe('/topic/' + request.getParameter("topicId") + '/comments/', function (notification) {
                // Call the notify function when receive a notification
                notify(JSON.parse(notification.body).content);

            });

        });

        return;
    } // function connect

    /**
     * Display the notification message.
     */
    function notify(message) {
        $("#notifications-area").append(message + "\n");
        return;
    }
    /**
     * Init operations.
     */
    $(document).ready(function () {

        // Start the web socket connection.
        connect();

    });


</script>

<br/>
<hr/>

</body>

</html>
