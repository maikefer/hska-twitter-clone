/**
 * Created by bob on 07.12.2016.
 */


$(function() {

  var $followingTab = $('#following-tab');
  var $followersTab = $('#follower-tab');

  if (getParameterByName('follower')) {
    activate('follower');
  } else {
    activate('following');
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
      case 'follower':
        $followingTab.removeClass('is-active');
        $followersTab.addClass('is-active');
        return;
    }
  }

  function getParameterByName(name, url) {
    if (!url) {
      url = window.location.href;
    }
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
      results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
  }

});
