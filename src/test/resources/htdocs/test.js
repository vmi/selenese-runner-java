function decodeForm() {
  var q = document.location.search.substring(1);
  q.split(/&/).forEach(function(item) {
    var pair = item.split(/=/, 2);
    var value = decodeURIComponent(pair[1]);
    var elem = document.getElementById(pair[0]);
    if (elem) {
      var key = "textContent";
      var text = elem[key];
      if (typeof text != "string") {
        key = "innerText";
        text = elem[key];
        alert("Error?: " + text);
      }
      if (text)
        text += ", " + value;
      else
        text = value;
      elem[key] = text;
    }
  });
}
