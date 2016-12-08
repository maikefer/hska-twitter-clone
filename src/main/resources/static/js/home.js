/**
 * Created by bob on 20/10/2016.
 */
document.getElementById('postText').onkeyup = function () {
  document.getElementById('counter').innerHTML = (140 - this.value.length);
};
