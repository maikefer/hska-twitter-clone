/**
 * Created by bob on 19/10/2016.
 */

$(function() {

  var $profile = $('#profile-header');

  /**
   * Show profile
   */
  $profile.on('click', function(ev) {
      var name = $('#profile-name').text();
      window.location.href = "/users/" + name;
  });

});
