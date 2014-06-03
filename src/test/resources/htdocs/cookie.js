function setCookie(key, value, expireDays) {
  var cookie = key + '=' + encodeURIComponent(value) ;
  if (expireDays) {
    var e = new Date();
    e.setTime(e.getTime() + expireDays * 24 * 3600 * 1000);
    cookie += '; expires=' + e.toUTCString();
  }
  document.cookie = cookie;
}
