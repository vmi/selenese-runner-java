function replaceAlertMethod(element) { // BEGIN
  window.localStorage.setItem('__webdriverAlerts', JSON.stringify([]));
  window.alert = function(msg) {
    var alerts = JSON.parse(window.localStorage.getItem('__webdriverAlerts'));
    alerts.push(msg);
    window.localStorage.setItem('__webdriverAlerts', JSON.stringify(alerts));
  };
  window.localStorage.setItem('__webdriverConfirms', JSON.stringify([]));
  if (!('__webdriverNextConfirm' in window.localStorage))
    window.localStorage.setItem('__webdriverNextConfirm', JSON.stringify(true));
  window.confirm = function(msg) {
    var confirms = JSON.parse(window.localStorage.getItem('__webdriverConfirms'));
    confirms.push(msg);
    window.localStorage.setItem('__webdriverConfirms', JSON.stringify(confirms));
    var res = JSON.parse(window.localStorage.getItem('__webdriverNextConfirm'));
    window.localStorage.setItem('__webdriverNextConfirm', JSON.stringify(true));
    return res;
  };
  window.localStorage.setItem('__webdriverPrompts', JSON.stringify([]));
  if (!('__webdriverNextPrompt' in window.localStorage))
    window.localStorage.setItem('__webdriverNextPrompt', JSON.stringify(""));
  window.prompt = function(msg) {
    var prompts = JSON.parse(window.localStorage.getItem('__webdriverPrompts'));
    prompts.push(msg);
    window.localStorage.setItem('__webdriverPrompts', JSON.stringify(prompts));
    var res = JSON.parse(window.localStorage.getItem('__webdriverNextPrompt'));
    window.localStorage.setItem('__webdriverNextPrompt', JSON.stringify(""));
    return res;
  };
  var fw;
  if (element && (fw = element.ownerDocument.defaultView) && fw != window) {
    fw.alert = window.alert;
    fw.confirm = window.confirm;
    fw.prompt = window.prompt;
  }
} // END

function getNextAlert() { // BEGIN
  if (!('__webdriverAlerts' in window.localStorage))
    return null;
  var alerts = JSON.parse(window.localStorage.getItem('__webdriverAlerts'));
  if (! alerts)
    return null;
  var msg = alerts.shift();
  window.localStorage.setItem('__webdriverAlerts', JSON.stringify(alerts));
  if (msg)
    msg = msg.replace(/\\n/g, ' ');
  return msg;
} // END

function isAlertPresent() { // BEGIN
  if (!('__webdriverAlerts' in window.localStorage))
    return false;
  var alerts = JSON.parse(window.localStorage.getItem('__webdriverAlerts'));
  return alerts && alerts.length > 0;
} // END

function getNextConfirmation() { // BEGIN
  if (!('__webdriverConfirms' in window.localStorage))
    return null;
  var confirms = JSON.parse(window.localStorage.getItem('__webdriverConfirms'));
  if (! confirms)
    return null;
  var msg = confirms.shift();
  window.localStorage.setItem('__webdriverConfirms', JSON.stringify(confirms));
  if (msg)
    msg = msg.replace(/\\n/g, ' ');
  return msg;
} // END

function isConfirmationPresent() { // BEGIN
  if (!('__webdriverConfirms' in window.localStorage))
    return false;
  var confirms = JSON.parse(window.localStorage.getItem('__webdriverConfirms'));
  return confirms && confirms.length > 0;
} // END

function answerOnNextPrompt(answer) { // BEGIN
  window.localStorage.setItem('__webdriverNextPrompt', JSON.stringify(answer));
} // END

function getNextPrompt() { // BEGIN
  if (!('__webdriverPrompts' in window.localStorage))
    return null;
  var prompts = JSON.parse(window.localStorage.getItem('__webdriverPrompts'));
  if (!prompts)
    return null;
  var msg = prompts.shift();
  window.localStorage.setItem('__webdriverPrompts', JSON.stringify(prompts));
  if (msg)
    msg = msg.replace(/\\n/g, ' ');
  return msg;
} // END

function isPromptPresent() { // BEGIN
  if (!('__webdriverPrompts' in window.localStorage))
    return false;
  var prompts = JSON.parse(window.localStorage.getItem('__webdriverPrompts'));
  return prompts && prompts.length > 0;
} // END
