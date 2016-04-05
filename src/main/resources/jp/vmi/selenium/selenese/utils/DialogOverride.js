function replaceAlertMethod() { // BEGIN
  var doUseLocalStorage = false;
  try {
    doUseLocalStorage = !!window.localStorage && !!JSON;
  } catch (e) {
  }
  if (doUseLocalStorage) {
    window.localStorage.setItem('__webdriverPrompts', JSON.stringify([]));
    if (!('__webdriverNextPrompt' in window.localStorage)) {
      window.localStorage.setItem('__webdriverNextPrompt', JSON.stringify(""));
    }
    window.prompt = function(msg) {
      var prompts = JSON.parse(window.localStorage.getItem('__webdriverPrompts'));
      prompts.push(msg);
      window.localStorage.setItem('__webdriverPrompts', JSON.stringify(prompts));
      var res = JSON.parse(window.localStorage.getItem('__webdriverNextPrompt'));
      window.localStorage.setItem('__webdriverNextPrompt', JSON.stringify(""));
      return res;
    };
  } else {
    window.__webdriverPrompts = [];
    window.__webdriverNextPrompt = "";
    window.prompt = function(msg) {
      window.__webdriverPrompts.push(msg);
      var res = window.__webdriverNextPrompt;
      window.__webdriverNextPrompt = "";
      return res;
    };
  }
} // END

function answerOnNextPrompt() { // BEGIN
  var doUseLocalStorage = false;
  try {
    doUseLocalStorage = !!window.localStorage && !!JSON;
  } catch (e) {
  }
  window.localStorage.setItem('__webdriverNextPrompt', JSON.stringify(arguments[0]));
} // END

function getNextPrompt() { // BEGIN
  var doUseLocalStorage = false;
  try {
    doUseLocalStorage = !!window.localStorage && !!JSON;
  } catch (e) {
  }
  if (doUseLocalStorage) {
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
  } else {
    if (!window.__webdriverPrompts)
      return null;
    return window.__webdriverPrompts.shift();
  }
} // END

function isPromptPresent() { // BEGIN
  var doUseLocalStorage = false;
  try {
    doUseLocalStorage = !!window.localStorage && !!JSON;
  } catch (e) {
  }
  if (doUseLocalStorage) {
    if (!('__webdriverPrompts' in window.localStorage))
      return false;
    var prompts = JSON.parse(window.localStorage.getItem('__webdriverPrompts'));
    return prompts && prompts.length > 0;
  } else {
    return window.__webdriverPrompts && window.__webdriverPrompts.length > 0;
  }
} // END
