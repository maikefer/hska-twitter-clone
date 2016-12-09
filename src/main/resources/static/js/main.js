/**
 * Created by bob on 19/10/2016.
 */


'use strict';

var stompClient = null;
var currentUser = null;

// Current location
var path = null;

$(function() {

  // Get current path
  path = window.location.pathname;

  // Get current user
  if (path !== '/login' && path !== '/registration') // No user on /login and /registration
    currentUser = document.getElementById('profile-name').textContent;

  var $profile = $('#profile-header');

  /**
   * Show profile
   */
  $profile.on('click', function(ev) {
      var name = $('#profile-name').text();
      window.location.href = "/users/" + name;
  });

  // Disconnect Socket
  disconnect();

  // Connect Socket
  if (path !== '/login' && path !== '/registration') // Don't connect on /login and /registration
    connect();

});


/**
 * Disconnects the WebSocket
 */
function disconnect() {
  if (stompClient != null) {
    stompClient.disconnect();
  }
  // setConnected(false);
  console.log("Disconnected");
}


/**
 * Connects to server via WebSocket
 */
function connect() {
  var hostname = window.location.hostname;
  var port = window.location.port;
  var url = 'ws://' + hostname + ':' + port + '/ws';

  // var socket = new SockJS('http://localhost:8080/hello');
  // var socket = new WebSocket('ws://localhost:8080/hello');
  // stompClient = Stomp.over(socket);
  stompClient = Stomp.client(url);
  stompClient.debug = null; // Disable debug output
  stompClient.connect({}, function (frame) {
    // setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/posts', function (post) {
      postReceived(post)
    });
  });
}

/**
 * Send new post to the server
 */
function createPost() {
  var message = document.getElementById('postText').value;
  var username = currentUser;

  if (stompClient && message.length !== 0 && message !== null && username != null) {
    stompClient.send("/app/post", {}, JSON.stringify({
      'message' : message,
      'username': username
    }));

    // Reset post content and counter
    document.getElementById('postText').value = null;
    document.getElementById('counter').innerHTML = 140;
  }

}

/**
 * is called when a new post received. Decides on which timeline post got appended.
 * @param post
 */
function postReceived(post) {
  var obj = JSON.parse(post.body);

  if (path === '/') {
    if (obj.user === currentUser) { // Append to global AND private timeline
      appendPost(obj, true);
    } else { // Global timeline only
      if (false) // Check if its a post from a followed user
        appendPost(obj, true);
      else
        appendPost(obj);
    }
  } else if (path !== '/login' && path !== 'registration') {
    showNotification(obj);
  }
}

/**
 * Appends new created post to the timeline
 * @param post
 */
function appendPost(post, isPrivate) {
  // Construct DOM element
  var elem =  "<li class=\"mdl-list__item mdl-list__item--two-line\" style=\"padding-right: 10px;\">" +
                "<span class=\"mdl-list__item-primary-content\">" +
                  "<a href=\"/users/" + post.user + "\">" +
                    "<i class=\"material-icons mdl-list__item-avatar\">person</i>" +
                  "</a>" +
                  "<a href=\"/users/" + post.user + "\" class=\"timeline-entry-username\">" + post.user +"</a>" +
                  "<span class=\"mdl-list__item-sub-title message\">" + post.message + "</span>" +
                "</span>" +
                "<span class=\"mdl-list__item-secondary-content timeline-entry-date-container\">" +
                  "<span class=\"mdl-list__item-secondary-info timeline-entry-date\">" + post.timestamp + "</span>" +
                "</span>" +
              "</li>";

  // Append as first child to the timeline
  $("#global-timeline").prepend(elem);

  if (isPrivate)
    $("#private-timeline").prepend(elem);
}

/**
 * Show notification that a new post was created
 * @param post
 */
function showNotification(post) {

  var snackbarContainer = document.querySelector('#snackbar');

  // Handler for the action button click
  var handler = function(event) {
    window.location = '/';
  };

  var data = {
    message: 'New post from ' + post.user + '.',
    timeout: 4000,
    actionHandler: handler,
    actionText: 'Show'
  };

  // Show notification
  snackbarContainer.MaterialSnackbar.showSnackbar(data);
}
