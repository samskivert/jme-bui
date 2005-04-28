//
// $Id$
//
// BUI - a user interface library for the JME 3D engine
// Copyright (C) 2005, Michael Bayne, All Rights Reserved
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// Redistributions of source code must retain the above copyright notice, this
// list of conditions and the following disclaimer.
//
// Redistributions in binary form must reproduce the above copyright notice,
// this list of conditions and the following disclaimer in the documentation
// and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package com.jme.bui.event;

import java.util.ArrayList;

import com.jme.input.InputSystem;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.util.Timer;

import com.jme.bui.BComponent;
import com.jme.bui.BWindow;
import com.jme.bui.Log;

/**
 * Processes the polled input information available from the underlying
 * input system into input events and dispatches those to the appropriate
 * parties.
 */
public class InputDispatcher
{
    public InputDispatcher (Timer timer)
    {
        _timer = timer;
        _keyInput = InputSystem.getKeyInput();
        _mouseInput = InputSystem.getMouseInput();

        if (_keyInput == null || _mouseInput == null) {
            throw new IllegalStateException(
                "InputSystem not properly initialized prior to creation of " +
                "InputDispatcher [key=" + _keyInput +
                ", mouse=" + _mouseInput + "].");
        }
    }

    /**
     * Registers a top-level window with the input system.
     */
    public void addWindow (BWindow window)
    {
        _windows.add(window);
    }

    /**
     * Removes a window from participation in the input system.
     */
    public void removeWindow (BWindow window)
    {
        _windows.remove(window);
    }

    /**
     * This method should be called on every frame to allow the input
     * dispatcher to process input since the previous frame and dispatch
     * any newly generated events.
     */
    public void update ()
    {
        // determine our tick stamp in milliseconds
        long tickStamp = _timer.getTime() * 1000 / _timer.getResolution();

        // update our keyboard buffer and check for new events
        _keyInput.update();
        while (_keyInput.next()) {
            boolean pressed = _keyInput.state();
            int keyCode = _keyInput.key();

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
            if (_focus != null) {
                KeyEvent event = new KeyEvent(
                    this, tickStamp, _modifiers, pressed ?
                    KeyEvent.KEY_PRESSED : KeyEvent.KEY_RELEASED,
                    _keyInput.keyChar(), keyCode);
                _focus.dispatchEvent(event);
            }
        }

        // check to see if the component under the mouse has changed
        int mx = _mouseInput.getXAbsolute(), my = _mouseInput.getYAbsolute();
        BComponent nhcomponent = null;

        // if we have a previous hover component, do a quick check to see
        // if it still contains the cursor
        if (_hcomponent != null) {
            nhcomponent = _hcomponent.getHitComponent(mx, my);
        }

        // if it does not contain the cursor, check for a new hover
        // component starting with each of our root components
        if (nhcomponent == null) {
            for (int ii = 0, ll = _windows.size(); ii < ll; ii++) {
                BWindow comp = (BWindow)_windows.get(ii);
                nhcomponent = comp.getHitComponent(mx, my);
                if (nhcomponent != null) {
                    break;
                }
            }
        }

        // generate any necessary mouse entry or exit events
        if (_hcomponent != nhcomponent) {
            // inform the previous component that the mouse has exited
            if (_hcomponent != null) {
                _hcomponent.dispatchEvent(
                    new MouseEvent(this, tickStamp, _modifiers,
                                   MouseEvent.MOUSE_EXITED, mx, my));
            }
            // inform the new component that the mouse has entered
            if (nhcomponent != null) {
                nhcomponent.dispatchEvent(
                    new MouseEvent(this, tickStamp, _modifiers,
                                   MouseEvent.MOUSE_ENTERED, mx, my));
            }
            _hcomponent = nhcomponent;
        }

        // mouse press and mouse motion events do not necessarily go to
        // the component under the mouse. when the mouse is clicked down
        // on a component (any button), it becomes the "clicked"
        // component, the target for all subsequent click and motion
        // events (which become drag events) until all buttons are
        // released
        BComponent tcomponent = (_ccomponent == null) ?
            _hcomponent : _ccomponent;

        // update the mouse modifiers, possibly generating events
        for (int ii = 0; ii < MOUSE_MODIFIER_MAP.length; ii++) {
            int modifierMask = MOUSE_MODIFIER_MAP[ii];
            boolean down = _mouseInput.isButtonDown(ii);
            boolean wasDown = ((_modifiers & modifierMask) != 0);
            int type = -1;
            if (down && !wasDown) {
                // if we had no mouse button down previous to this and
                // have a component under the mouse, it becomes the
                // "clicked" component
                if ((_modifiers & ANY_BUTTON_PRESSED) == 0 &&
                    tcomponent != null) {
                    _ccomponent = tcomponent;
                    setFocus(tickStamp, tcomponent);
                }
                type = MouseEvent.BUTTON_PRESSED;
                _modifiers |= modifierMask;

            } else if (!down && wasDown) {
                type = MouseEvent.BUTTON_RELEASED;
                _modifiers &= ~modifierMask;
            }

            if (type != -1 && tcomponent != null) {
                tcomponent.dispatchEvent(
                    new MouseEvent(this, tickStamp, _modifiers,
                                   type, ii, mx, my));
            }
        }

        // if the mouse has moved, let the target component know about
        // that as well
        if (_mouseX != mx || _mouseY != my) {
            _mouseX = mx;
            _mouseY = my;
            if (tcomponent != null) {
                int type = (tcomponent == _ccomponent) ?
                    MouseEvent.MOUSE_DRAGGED : MouseEvent.MOUSE_MOVED;
                tcomponent.dispatchEvent(
                    new MouseEvent(this, tickStamp, _modifiers,
                                   type, mx, my));
            }
        }

        // finally, if no buttons are up after processing, clear out our
        // "clicked" component
        if ((_modifiers & ANY_BUTTON_PRESSED) == 0) {
            _ccomponent = null;
        }
    }

    /**
     * Configures the component that has keyboard focus.
     */
    protected void setFocus (long tickStamp, BComponent focus)
    {
        // if the focus is changing, dispatch an event to report it
        if (_focus != focus) {
            if (_focus != null) {
                _focus.dispatchEvent(
                    new FocusEvent(this, tickStamp, FocusEvent.FOCUS_LOST));
            }
            _focus = focus;
            if (_focus != null) {
                _focus.dispatchEvent(
                    new FocusEvent(this, tickStamp, FocusEvent.FOCUS_GAINED));
            }
        }
    }

    protected Timer _timer;
    protected KeyInput _keyInput;
    protected MouseInput _mouseInput;

    protected int _modifiers;
    protected int _mouseX, _mouseY;

    protected ArrayList _windows = new ArrayList();
    protected BComponent _hcomponent, _ccomponent;
    protected BComponent _focus;

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
