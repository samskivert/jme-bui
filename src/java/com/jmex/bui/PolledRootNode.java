//
// $Id$
//
// BUI - a user interface library for the JME 3D engine
// Copyright (C) 2005, Michael Bayne, All Rights Reserved
//
// This library is free software; you can redistribute it and/or modify it
// under the terms of the GNU Lesser General Public License as published
// by the Free Software Foundation; either version 2.1 of the License, or
// (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package com.jmex.bui;

import java.util.ArrayList;

import com.jme.input.InputHandler;
import com.jme.input.InputSystem;
import com.jme.input.KeyInput;
import com.jme.input.KeyInputListener;
import com.jme.input.MouseInput;
import com.jme.scene.Node;
import com.jme.util.Timer;

import com.jmex.bui.event.InputEvent;
import com.jmex.bui.event.KeyEvent;
import com.jmex.bui.event.MouseEvent;

/**
 * Processes the polled input information available from the underlying
 * input system into input events and dispatches those to the appropriate
 * parties.
 */
public class PolledRootNode extends BRootNode
{
    public PolledRootNode (Timer timer, InputHandler handler)
    {
        _timer = timer;
        _handler = handler;

        // register our interest in key presses
        KeyInput.get().addListener(_keyListener);
    }

    // documentation inherited
    public void rootInvalidated (BComponent root)
    {
        // add the component to the list of invalid roots
        if (!_invalidRoots.contains(root)) {
            _invalidRoots.add(root);
        }
    }

    // documentation inherited
    protected void updateWorldData (float timePerFrame)
    {
        super.updateWorldData(timePerFrame);

        // determine our tick stamp in milliseconds
        _tickStamp = _timer.getTime() * 1000 / _timer.getResolution();

        // update the mouse input system
        MouseInput mousein = MouseInput.get();
        mousein.update();

        // determine whether the mouse has moved in the last frame
        int mx = mousein.getXAbsolute();
        int my = mousein.getYAbsolute();
        boolean mouseMoved = false;
        if (_mouseX != mx || _mouseY != my) {
            _mouseX = mx;
            _mouseY = my;
            mouseMoved = true;
        }

        if (mouseMoved) {
            computeHoverComponent(mx, my);
        }

        // mouse press and mouse motion events do not necessarily go to
        // the component under the mouse. when the mouse is clicked down
        // on a component (any button), it becomes the "clicked"
        // component, the target for all subsequent click and motion
        // events (which become drag events) until all buttons are
        // released
        BComponent tcomponent = _ccomponent;
        // if there's no clicked component, use the hover component
        if (tcomponent == null) {
            tcomponent = _hcomponent;
        }
        // if there's no hover component, use the default event target
        if (tcomponent == null) {
            tcomponent = _dcomponent;
        }

        // update the mouse modifiers, possibly generating events
        for (int ii = 0; ii < MOUSE_MODIFIER_MAP.length; ii++) {
            int modifierMask = MOUSE_MODIFIER_MAP[ii];
            boolean down = mousein.isButtonDown(ii);
            boolean wasDown = ((_modifiers & modifierMask) != 0);
            int type = -1;
            if (down && !wasDown) {
                // if we had no mouse button down previous to this,
                // whatever's under the mouse becomes the "clicked"
                // component (which might be null)
                if ((_modifiers & ANY_BUTTON_PRESSED) == 0) {
                    _ccomponent = tcomponent;
                    setFocus(tcomponent);
                }
                type = MouseEvent.MOUSE_PRESSED;
                _modifiers |= modifierMask;

            } else if (!down && wasDown) {
                type = MouseEvent.MOUSE_RELEASED;
                _modifiers &= ~modifierMask;
            }

            if (type != -1 && tcomponent != null) {
                tcomponent.dispatchEvent(
                    new MouseEvent(this, _tickStamp, _modifiers,
                                   type, ii, mx, my));
            }
        }

        // if the mouse has moved, let the target component know about
        // that as well
        if (mouseMoved) {
            if (tcomponent != null) {
                int type = (tcomponent == _ccomponent) ?
                    MouseEvent.MOUSE_DRAGGED : MouseEvent.MOUSE_MOVED;
                tcomponent.dispatchEvent(
                    new MouseEvent(this, _tickStamp, _modifiers,
                                   type, mx, my));
            }
        }

        // process any mouse wheel events
        int wdelta = mousein.getWheelDelta();
        if (wdelta != 0 && tcomponent != null) {
            tcomponent.dispatchEvent(
                new MouseEvent(this, _tickStamp, _modifiers,
                               MouseEvent.MOUSE_WHEELED, -1, mx, my, wdelta));
        }

        // finally, if no buttons are up after processing, clear out our
        // "clicked" component
        if ((_modifiers & ANY_BUTTON_PRESSED) == 0) {
            _ccomponent = null;
        }

        // poll the keyboard and notify event listeners
        KeyInput.get().update();

        // if we have no focus component, update the normal input handler
        if (_focus == null && _handler != null) {
            _handler.update(timePerFrame);
        }

        // finally validate all invalid roots
        while (_invalidRoots.size() > 0) {
            BComponent root = (BComponent)_invalidRoots.remove(0);
            // make sure the root is still added to the view hierarchy
            if (root.isAdded()) {
                root.validate();
            }
        }
    }

    /** This listener is notified when a key is pressed or released. */
    protected KeyInputListener _keyListener = new KeyInputListener() {
        public void onKey (char character, int keyCode, boolean pressed) {
            // first update the state of the modifiers
            int modifierMask = -1;
            for (int ii = 0; ii < KEY_MODIFIER_MAP.length; ii += 2) {
                if (KEY_MODIFIER_MAP[ii] == keyCode) {
                    modifierMask = KEY_MODIFIER_MAP[ii+1];
                    break;
                }
            }
            if (modifierMask != -1) {
                if (pressed) {
                    _modifiers |= modifierMask;
                } else {
                    _modifiers &= ~modifierMask;
                }
            }

            // if we have a focus, generate a key event and dispatch it
            BComponent target = _focus;
            if (target == null) {
                target = _dcomponent;
            }
            if (target != null) {
                KeyEvent event = new KeyEvent(
                    this, _tickStamp, _modifiers,
                    pressed ? KeyEvent.KEY_PRESSED : KeyEvent.KEY_RELEASED,
                    character, keyCode);
                target.dispatchEvent(event);
            }
        }
    };

    protected Timer _timer;
    protected InputHandler _handler;
    protected ArrayList _invalidRoots = new ArrayList();

    /** Maps key codes to modifier flags. */
    protected static final int[] KEY_MODIFIER_MAP = {
        KeyInput.KEY_LSHIFT, InputEvent.SHIFT_DOWN_MASK,
        KeyInput.KEY_RSHIFT, InputEvent.SHIFT_DOWN_MASK,
        KeyInput.KEY_LCONTROL, InputEvent.CTRL_DOWN_MASK,
        KeyInput.KEY_RCONTROL, InputEvent.CTRL_DOWN_MASK,
        KeyInput.KEY_LMENU, InputEvent.ALT_DOWN_MASK,
        KeyInput.KEY_RMENU, InputEvent.ALT_DOWN_MASK,
        KeyInput.KEY_LWIN, InputEvent.META_DOWN_MASK,
        KeyInput.KEY_RWIN, InputEvent.META_DOWN_MASK,
    };

    /** Maps button indices to modifier flags. */
    protected static final int[] MOUSE_MODIFIER_MAP = {
        InputEvent.BUTTON1_DOWN_MASK,
        InputEvent.BUTTON2_DOWN_MASK,
        InputEvent.BUTTON3_DOWN_MASK,
    };

    /** Used to check whether any button remains pressed. */
    protected static final int ANY_BUTTON_PRESSED =
        InputEvent.BUTTON1_DOWN_MASK |
        InputEvent.BUTTON2_DOWN_MASK |
        InputEvent.BUTTON3_DOWN_MASK;
}
