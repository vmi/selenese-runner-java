// Following code is copied from:
// selenium/javascript/selenium-core/scripts/htmlutils.js
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
  } else {
    if (window.KeyEvent) {
      var doc = element.ownerDocument;
      var view = doc.defaultView;
      evt = doc.createEvent('KeyEvents');
      evt.initKeyEvent(eventType, true, true, view, controlKeyDown, altKeyDown, shiftKeyDown, metaKeyDown, keyCode, keyCode);
    } else {
      evt = document.createEvent('UIEvents');
      evt.initUIEvent(eventType, true, true, window, 1);
      evt.shiftKey = shiftKeyDown;
      evt.metaKey = metaKeyDown;
      evt.altKey = altKeyDown;
      evt.ctrlKey = controlKeyDown;
      evt.keyCode = keyCode;
      evt.charCode = eventType == "keypress" ? keyCode : 0;
      evt.which = keyCode;
    }
    element.dispatchEvent(evt);
  }
}
