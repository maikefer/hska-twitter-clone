/**
 * Created by bob on 19/10/2016.
 */

$(function() {

  var $timeline = $('#timeline');
  var $followers = $('#followers');
  var $profile = $('#profile-header');
  var $followingTab = $('#following-tab');
  var $followersTab = $('#followers-tab');

  // Show followers
  $('#btn-followers').on('click', function(ev) {
    show('followers');

    // Activate tab
    activate('followers');
  });

  // Show whom im following
  $('#btn-following').on('click', function(ev) {
    show('followers');

    // Activate tab
    activate('following');
  });

  /**
   * Show timeline
   */
  $profile.on('click', function(ev) {
    var pathname = window.location.pathname;
    // navigate to user
    if (pathname === '/') {
      // get profile name
      var name = $('#profile-name').text();

      // Navigate to user
      window.location.href = "/users/" + name;
    } else {
      show('timeline');
    }

  });

  /**
   * Show a DOM container
   * @param container
   */
  function show(container) {
    switch (container) {
      case 'followers':
        $timeline.css('display', 'none');
        $followers.css('display', 'block');
        return;
      case 'timeline':
        $timeline.css('display', 'block');
        $followers.css('display', 'none');
        return;
    }
  }

  /**
   * Activates the desired tab
   * @param tab
   */
  function activate(tab) {
    switch (tab) {
      case 'following':
        $followersTab.removeClass('is-active');
        $followingTab.addClass('is-active');
        return;
      case 'followers':
        $followingTab.removeClass('is-active');
        $followersTab.addClass('is-active');
        return;
    }
  }

});
