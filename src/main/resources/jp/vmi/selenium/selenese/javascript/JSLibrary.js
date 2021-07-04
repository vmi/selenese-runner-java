// Following code is copied from Selenium project:
//   http://www.seleniumhq.org/
// and modified.
//
// Original license:
// ------------------------------------------------------------
// Licensed to the Software Freedom Conservancy (SFC) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The SFC licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
// ------------------------------------------------------------

//
// for overriding dialog functions.
//

function replaceAlertMethod(element) {
  if (!window.__isReplacedAlertMethod) {
    window.__isReplacedAlertMethod = true;
    var canUseLocalStorage = false;
    try { canUseLocalStorage = !!window.localStorage; } catch(ex) { /* probe failed */ }
    var canUseJSON = false;
    try { canUseJSON = !!JSON; } catch(ex) { /* probe failed */ }
    if (canUseLocalStorage && canUseJSON) {
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
    } else {
      window.__webdriverAlerts = [];
      window.alert = function(msg) { window.__webdriverAlerts.push(msg); };
      window.__webdriverConfirms = [];
      if (typeof window.__webdriverNextConfirm === 'undefined')
        window.__webdriverNextConfirm = true;
      window.confirm = function(msg) {
        window.__webdriverConfirms.push(msg);
        var res = window.__webdriverNextConfirm;
        window.__webdriverNextConfirm = true;
        return res;
      };
      window.__webdriverPrompts = [];
      if (typeof window.__webdriverNextPrompt === 'undefined')
        window.__webdriverNextPrompt = true;
      window.prompt = function(msg, def) {
        window.__webdriverPrompts.push(msg || def);
        var res = window.__webdriverNextPrompt;
        window.__webdriverNextPrompt = true;
        return res;
      };
    }
  }
  var fw;
  if (element && (fw = element.ownerDocument.defaultView) && fw != window) {
    fw.alert = window.alert;
    fw.confirm = window.confirm;
    fw.prompt = window.prompt;
  }
}

function getNextAlert() {
  var canUseLocalStorage = false;
  try { canUseLocalStorage = !!window.localStorage; } catch(ex) { /* probe failed */ }
  var canUseJSON = false;
  try { canUseJSON = !!JSON; } catch(ex) { /* probe failed */ }
  if (canUseLocalStorage && canUseJSON) {
    if (!('__webdriverAlerts' in window.localStorage))
      return null;
    var alerts = JSON.parse(window.localStorage.getItem('__webdriverAlerts'));
    if (! alerts)
      return null;
    var msg = alerts.shift();
    window.localStorage.setItem('__webdriverAlerts', JSON.stringify(alerts));
    if (msg)
      msg = msg.replace(/\n/g, ' ');
    return msg;
  } else {
    if (!window.__webdriverAlerts) { return null }
    var t = window.__webdriverAlerts.shift();
    if (t) { t = t.replace(/\\n/g, ' '); }
    return t;
  }
}

function isAlertPresent() {
  var canUseLocalStorage = false;
  try { canUseLocalStorage = !!window.localStorage; } catch(ex) { /* probe failed */ }
  var canUseJSON = false;
  try { canUseJSON = !!JSON; } catch(ex) { /* probe failed */ }
  if (canUseLocalStorage && canUseJSON) {
    if (!('__webdriverAlerts' in window.localStorage))
      return false;
    var alerts = JSON.parse(window.localStorage.getItem('__webdriverAlerts'));
    return alerts && alerts.length > 0;
  } else {
    return window.__webdriverAlerts && window.__webdriverAlerts.length > 0;
  }
}

function setNextConfirmationState(state) {
  var canUseLocalStorage = false;
  try { canUseLocalStorage = !!window.localStorage; } catch(ex) { /* probe failed */ }
  var canUseJSON = false;
  try { canUseJSON = !!JSON; } catch(ex) { /* probe failed */ }
  if (canUseLocalStorage && canUseJSON) {
    window.localStorage.setItem('__webdriverNextConfirm', JSON.stringify(state));
  } else {
    window.__webdriverNextConfirm = state;
  }
}

function getNextConfirmation() {
  var canUseLocalStorage = false;
  try { canUseLocalStorage = !!window.localStorage; } catch(ex) { /* probe failed */ }
  var canUseJSON = false;
  try { canUseJSON = !!JSON; } catch(ex) { /* probe failed */ }
  if (canUseLocalStorage && canUseJSON) {
    if (!('__webdriverConfirms' in window.localStorage))
      return null;
    var confirms = JSON.parse(window.localStorage.getItem('__webdriverConfirms'));
    if (! confirms)
      return null;
    var msg = confirms.shift();
    window.localStorage.setItem('__webdriverConfirms', JSON.stringify(confirms));
    if (msg)
      msg = msg.replace(/\n/g, ' ');
    return msg;
  } else {
    if (!window.__webdriverConfirms) { return null; }
    return window.__webdriverConfirms.shift();
  }
}

function isConfirmationPresent() {
  var canUseLocalStorage = false;
  try { canUseLocalStorage = !!window.localStorage; } catch(ex) { /* probe failed */ }
  var canUseJSON = false;
  try { canUseJSON = !!JSON; } catch(ex) { /* probe failed */ }
  if (canUseLocalStorage && canUseJSON) {
    if (!('__webdriverConfirms' in window.localStorage))
      return false;
    var confirms = JSON.parse(window.localStorage.getItem('__webdriverConfirms'));
    return confirms && confirms.length > 0;
  } else {
    return window.__webdriverConfirms && window.__webdriverConfirms.length > 0;
  }
}

function answerOnNextPrompt(answer) {
  var canUseLocalStorage = false;
  try { canUseLocalStorage = !!window.localStorage; } catch(ex) { /* probe failed */ }
  var canUseJSON = false;
  try { canUseJSON = !!JSON; } catch(ex) { /* probe failed */ }
  if (canUseLocalStorage && canUseJSON) {
    window.localStorage.setItem('__webdriverNextPrompt', JSON.stringify(answer));
  } else {
    window.__webdriverNextPrompt = answer;
  }
}

function getNextPrompt() {
  var canUseLocalStorage = false;
  try { canUseLocalStorage = !!window.localStorage; } catch(ex) { /* probe failed */ }
  var canUseJSON = false;
  try { canUseJSON = !!JSON; } catch(ex) { /* probe failed */ }
  if (canUseLocalStorage && canUseJSON) {
    if (!('__webdriverPrompts' in window.localStorage))
      return null;
    var prompts = JSON.parse(window.localStorage.getItem('__webdriverPrompts'));
    if (!prompts)
      return null;
    var msg = prompts.shift();
    window.localStorage.setItem('__webdriverPrompts', JSON.stringify(prompts));
    if (msg)
      msg = msg.replace(/\n/g, ' ');
    return msg;
  } else {
    if (!window.__webdriverPrompts) { return null; }
    return window.__webdriverPrompts.shift();
  }
}

function isPromptPresent() {
  var canUseLocalStorage = false;
  try { canUseLocalStorage = !!window.localStorage; } catch(ex) { /* probe failed */ }
  var canUseJSON = false;
  try { canUseJSON = !!JSON; } catch(ex) { /* probe failed */ }
  if (canUseLocalStorage && canUseJSON) {
    if (!('__webdriverPrompts' in window.localStorage))
      return false;
    var prompts = JSON.parse(window.localStorage.getItem('__webdriverPrompts'));
    return prompts && prompts.length > 0;
  } else {
    return window.__webdriverPrompts && window.__webdriverPrompts.length > 0;
  }
}

//
// for handling key event.
//

function triggerKeyEvent(element, eventType, keyCode, controlKeyDown, altKeyDown, shiftKeyDown, metaKeyDown) {
  var evt;
  if (element.fireEvent && element.ownerDocument && element.ownerDocument.createEventObject) { // IE
    evt = element.ownerDocument.createEventObject();
    evt.shiftKey = shiftKeyDown;
    evt.metaKey = metaKeyDown;
    evt.altKey = altKeyDown;
    evt.ctrlKey = controlKeyDown;
    evt.keyCode = keyCode;
    element.fireEvent('on' + eventType, evt);
    return;
  }
  var doc = element.ownerDocument;
  var view = doc.defaultView;
  evt = document.createEvent('Events');
  evt.initEvent(eventType, true, true);
  evt.view = window;
  evt.shiftKey = shiftKeyDown;
  evt.metaKey = metaKeyDown;
  evt.altKey = altKeyDown;
  evt.ctrlKey = controlKeyDown;
  evt.keyCode = keyCode;
  evt.which = keyCode;
  evt.charCode = (eventType === "keypress") ? keyCode : 0;
  console.log(keyCode + "/" + evt.keyCode);
  element.dispatchEvent(evt);
}

//
// for setting cursor position in text field.
//
function setCursorPosition(element, position) {
  if (position == -1)
    position = element.value.length;
    element.focus();
  if (element.setSelectionRange) {
    element.setSelectionRange(position, position);
  } else if (element.createTextRange) {
    var range = element.createTextRange();
    range.collapse(true);
    range.moveEnd('character', position);
    range.moveStart('character', position);
    range.select();
  }
}

//
// for getting cursor position in text field.
//
function getCursorPosision(element) {
  try {
    var selectRange = document.selection.createRange().duplicate();
    var elementRange = element.createTextRange();
    selectRange.move('character', 0);
    elementRange.move('character', 0);
    var inRange1 = selectRange.inRange(elementRange);
    var inRange2 = elementRange.inRange(selectRange);
    elementRange.setEndPoint('EndToEnd', selectRange);
  } catch (e) {
    throw Error('There is no cursor on this page!');
  }
  return String(elementRange.text).replace(/\r/g,' ').length;
}

//
// get element index.
//
function getElementIndex(element) {
  var _isCommentOrEmptyTextNode = function(node) {
    return node.nodeType == 8 || ((node.nodeType == 3) && !(/[^\t\n\r ]/.test(node.data)));
  };
  var previousSibling;
  var index = 0;
  while ((previousSibling = element.previousSibling) != null) {
    if (!_isCommentOrEmptyTextNode(previousSibling)) {
      index++;
    }
    element = previousSibling;
  }
  return index;
}

function isOrdered(element1, element2) {
  if (element1 === element2)
    return false;
  var previousSibling;
  while ((previousSibling = element2.previousSibling) != null) {
    if (previousSibling === element1)
      return true;
    element2 = previousSibling;
  }
  return false;
}

function getTable(table, row, col) {
  var rows = table.rows;
  if (row > rows.length)
    return "Cannot access row " + row + " - table has " + rows.length + " rows";
  if (col > rows[row].cells.length)
    return "Cannot access column " + col + " - table row has " + rows[row].cells.length + " columns";
  return rows[row].cells[col];
}

function getText(element) {
  function isVisible(element) {
    var style = (typeof getComputedStyle !== 'undefined') ? getComputedStyle(element) : element.currentStyle
    return style.display !== 'none' && style.visibility !== 'hidden';
  }
  function trim(text) {
    // String.replace() version of trim obtained from https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/Trim#Polyfill
    return String.prototype.trim ? text.trim() : text.replace(/^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g, '');
  }
  function getTextInternal(element) {
    var texts = [];
    var nodes = element.childNodes;
    for (var i = 0; i < nodes.length; i++) {
      var node = nodes[i];
      switch (node.nodeType) {
      case 1: // ELEMENT_NODE
        switch (node.nodeName) {
        case "SCRIPT":
        case "STYLE":
        case "LINK":
          // skip
          break;
        case "BR":
          texts.push("\n");
          break;
        default:
          if (isVisible(node))
            texts.push(getTextInternal(node));
          break;
        }
        break;
      case 8: // COMMENT_NODE
        // skip
        break;
      default:
        var text = (node.textContent || node.innerText || node.nodeValue).replace(/[\u0000-\u0020]+/g, " ");
        texts.push(text);
        break;
      }
    }
    return trim(texts.join("").replace(/ +/g, " "));
  }
  return getTextInternal(element);
}

if (module)
  module.exports = triggerKeyEvent;
