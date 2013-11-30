function send() {
  setStatus("");
  var elems = document.getElementsByName("result");
  for (var i = 0; i < elems.length; i++) {
    if (elems[i].checked) {
      setTimeout(function() {setStatus(elems[i].value);}, 1000);
      break;
    }
  }
  return void(0);
}

function setStatus(result) {
  var statusElem = document.getElementById("status");
  while (statusElem.childNodes.length > 0)
    statusElem.removeChild(statusElem.lastChild);
  switch (result) {
  case "OK":
    var ok = document.createElement("span");
    ok.id = "status_ok";
    ok.textContent = "OK";
    ok.style.color = "blue";
    statusElem.appendChild(ok);
    break;
  case "NG":
    var ng = document.createElement("span");
    ng.id = "status_ng";
    ng.textContent = "NG";
    ng.style.color = "red";
    statusElem.appendChild(ng);
    break;
  default:
    statusElem.textContent = result;
    break;
  }
}
