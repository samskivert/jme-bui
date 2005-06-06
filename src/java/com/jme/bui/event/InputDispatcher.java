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

package com.jme.bui.event;

import java.util.ArrayList;

import com.jme.input.InputHandler;
import com.jme.input.InputSystem;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.scene.Node;
import com.jme.util.Timer;

import com.jme.bui.BComponent;
import com.jme.bui.BScrollPane;
import com.jme.bui.BWindow;
import com.jme.bui.Log;

/**
 * Processes the polled input information available from the underlying
 * input system into input events and dispatches those to the appropriate
 * parties.
 */
public class InputDispatcher
{
    public InputDispatcher (Timer timer, InputHandler handler, Node rootNode)
    {
        _timer = timer;
        _handler = handler;
        _keyInput = InputSystem.getKeyInput();
        _mouseInput = InputSystem.getMouseInput();
        _rootNode = rootNode;

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
        window.setInputDispatcher(this);
        _rootNode.attachChild(window.getNode());

        // if this window is modal, make it the default event target
        if (window.isModal()) {
            pushDefaultEventTarget(window);
        }

        // recompute the hover component; the window may be under the mouse
        computeHoverComponent(_mouseX, _mouseY);
    }

    /**
     * Removes a window from participation in the input system.
     */
    public void removeWindow (BWindow window)
    {
        // first remove the window from our list
        if (!_windows.remove(window)) {
            Log.log.warning("Requested to remove unmanaged window " +
                            "[window=" + window + "].");
            Thread.dumpStack();
            return;
        }

        // if the window is modal, pop the default event target
        if (window.isModal()) {
            popDefaultEventTarget(window);
        }

        // then remove the hover component (which may result in a mouse
        // exited even being dispatched to the window or one of its
        // children)
        computeHoverComponent(_mouseX, _mouseY);

        // finally remove the window from the interface heirarchy
        window.setInputDispatcher(null);
        _rootNode.detachChild(window.getNode());
    }

    /**
     * Registers a scroll pane with the input dispatcher. This pane will
     * be updated on every frame, giving it a chance to re-render its
     * viewport.
     */
    public void addScrollPane (BScrollPane pane)
    {
        _spanes.add(pane);
    }

    /**
     * Unregisters the supplied srollpane. It will no longer be updated on
     * every frame.
     */
    public void removeScrollPane (BScrollPane pane)
    {
        _spanes.remove(pane);
    }

    /**
     * Configures a component (which would generally not be part of a
     * normal interface hierarchy) to receive all events that are not sent
     * to some other component.
     */
    public void pushDefaultEventTarget (BComponent component)
    {
        if (_dcomponent != null) {
            _defaults.add(0, _dcomponent);
        }
        _dcomponent = component;
    }

    /**
     * Pops the default event target off the stack.
     */
    public void popDefaultEventTarget (BComponent component)
    {
        if (_dcomponent == component) {
            if (_defaults.size() > 0) {
                _dcomponent = (BComponent)_defaults.remove(0);
            } else {
                _dcomponent = null;
            }
        } else {
            _defaults.remove(component);
        }
    }

    /**
     * Requests that the specified component be given the input focus.
     * Pass null to clear the focus.
     */
    public void requestFocus (BComponent component)
    {
        setFocus(component);
    }

    /**
     * This method should be called on every frame to allow the input
     * dispatcher to process input since the previous frame and dispatch
     * any newly generated events.
     */
    public void update (float timePerFrame)
    {
        // determine our tick stamp in milliseconds
        _tickStamp = _timer.getTime() * 1000 / _timer.getResolution();

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
            BComponent target = _focus;
            if (target == null) {
                target = _dcomponent;
            }
            if (target != null) {
                KeyEvent event = new KeyEvent(
                    this, _tickStamp, _modifiers, pressed ?
                    KeyEvent.KEY_PRESSED : KeyEvent.KEY_RELEASED,
                    _keyInput.keyChar(), keyCode);
                target.dispatchEvent(event);
            }
        }

        // determine whether the mouse has moved in the last frame
        int mx = _mouseInput.getXAbsolute(), my = _mouseInput.getYAbsolute();
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
            boolean down = _mouseInput.isButtonDown(ii);
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
        int wdelta = _mouseInput.getWheelDelta();
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

        // if we have no focus component, update the normal input handler
        if (_focus == null) {
            _handler.update(timePerFrame);
        }

        // update our scroll panes
        for (int ii = 0, ll = _spanes.size(); ii < ll; ii++) {
            ((BScrollPane)_spanes.get(ii)).update();
        }
    }

    /**
     * Generates a string representation of this instance.
     */
    public String toString ()
    {
        return "Dispatcher@" + hashCode();
    }

    /**
     * Configures the component that has keyboard focus.
     */
    protected void setFocus (BComponent focus)
    {
        // allow the component we clicked on to adjust the focus target
        if (focus != null) {
            focus = focus.getFocusTarget();
        }

        // if the focus is changing, dispatch an event to report it
        if (_focus != focus) {
            if (_focus != null) {
                _focus.dispatchEvent(
                    new FocusEvent(this, _tickStamp, FocusEvent.FOCUS_LOST));
            }
            _focus = focus;
            if (_focus != null) {
                _focus.dispatchEvent(
                    new FocusEvent(this, _tickStamp, FocusEvent.FOCUS_GAINED));
            }
        }
    }

    /**
     * Recomputes the component over which the mouse is hovering,
     * generating mouse exit and entry events as necessary.
     */
    protected void computeHoverComponent (int mx, int my)
    {
        // check for a new hover component starting with each of our root
        // components
        BComponent nhcomponent = null;
        for (int ii = _windows.size()-1; ii >= 0; ii--) {
            BWindow comp = (BWindow)_windows.get(ii);
            nhcomponent = comp.getHitComponent(mx, my);
            if (nhcomponent != null) {
                break;
            }
            // if this window is modal, stop here
            if (comp.isModal()) {
                break;
            }
        }

        // generate any necessary mouse entry or exit events
        if (_hcomponent != nhcomponent) {
            // inform the previous component that the mouse has exited
            if (_hcomponent != null) {
                _hcomponent.dispatchEvent(
                    new MouseEvent(this, _tickStamp, _modifiers,
                                   MouseEvent.MOUSE_EXITED, mx, my));
            }
            // inform the new component that the mouse has entered
            if (nhcomponent != null) {
                nhcomponent.dispatchEvent(
                    new MouseEvent(this, _tickStamp, _modifiers,
                                   MouseEvent.MOUSE_ENTERED, mx, my));
            }
            _hcomponent = nhcomponent;
        }
    }

    protected Timer _timer;
    protected KeyInput _keyInput;
    protected MouseInput _mouseInput;
    protected InputHandler _handler;
    protected Node _rootNode;

    protected long _tickStamp;
    protected int _modifiers;
    protected int _mouseX, _mouseY;

    protected ArrayList _windows = new ArrayList();
    protected ArrayList _spanes = new ArrayList();
    protected BComponent _hcomponent, _ccomponent;
    protected BComponent _dcomponent, _focus;
    protected ArrayList _defaults = new ArrayList();

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
