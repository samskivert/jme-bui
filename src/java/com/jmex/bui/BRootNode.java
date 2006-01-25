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
import java.util.logging.Level;

import com.jme.intersection.CollisionResults;
import com.jme.intersection.PickResults;
import com.jme.math.Ray;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Spatial;

import com.jmex.bui.Log;
import com.jmex.bui.event.FocusEvent;
import com.jmex.bui.event.MouseEvent;

/**
 * Connects the BUI system into the JME scene graph.
 */
public abstract class BRootNode extends Geometry
{
    public BRootNode ()
    {
        super("BUI Root Node");

        // we need to render in the ortho queue
        setRenderQueueMode(Renderer.QUEUE_ORTHO);
    }

    /**
     * Registers a top-level window with the input system.
     */
    public void addWindow (BWindow window)
    {
        _windows.add(window);

        // if this window is modal, make it the default event target (which
        // will save the current focus for later restoration)
        if (window.isModal()) {
            pushDefaultEventTarget(window);
        }

        // add this window to the hierarchy (which may set a new focus)
        window.setRootNode(this);

        // recompute the hover component; the window may be under the mouse
        computeHoverComponent(_mouseX, _mouseY);
    }

    /**
     * Removes a window from participation in the input system.
     */
    public void removeWindow (BWindow window)
    {
        // if our focus is in this window, clear it
        if (_focus != null && _focus.getWindow() == window) {
            setFocus(null);
        }

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
        window.setRootNode(null);
    }

    /**
     * This is called by a window or a scroll pane when it has become invalid.
     * The root node should schedule a revalidation of this component on the
     * next tick or the next time an event is processed.
     */
    public abstract void rootInvalidated (BComponent root);

    /**
     * Configures a component to receive all events that are not sent to some
     * other component. This is generally done for modal windows but can be
     * used in other circumstances. The current focus is stored and the focus
     * is cleared. When the event target is popped the previous focus will be
     * restored.
     */
    public void pushDefaultEventTarget (BComponent component)
    {
        _defaults.add(0, new TargetRecord(_dcomponent, _focus));
        _dcomponent = component;
        setFocus(null);
    }

    /**
     * Pops the default event target off the stack.
     */
    public void popDefaultEventTarget (BComponent component)
    {
        if (_dcomponent == component) {
            if (_defaults.size() > 0) {
                TargetRecord record = (TargetRecord)_defaults.remove(0);
                _dcomponent = record.target;
                // restore the saved focus if possible
                if (record.priorFocus != null && record.priorFocus.isAdded()) {
                    setFocus(record.priorFocus);
                }

            } else {
                _dcomponent = null;
            }

        } else {
            // this doesn't do the precisely correct thing with the focus if an
            // intermediate window is removed, but it's not a common enough
            // circumstance to merit fiddling around with
            for (int ii = 0, ll = _defaults.size(); ii < ll; ii++) {
                TargetRecord record = (TargetRecord)_defaults.get(ii);
                if (record.target == component) {
                    _defaults.remove(ii);
                    break;
                }
            }
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
     * Generates a string representation of this instance.
     */
    public String toString ()
    {
        return "BRootNode@" + hashCode();
    }

    // documentation inherited
    public void onDraw (Renderer renderer)
    {
        // we're rendered in the ortho queue, so we just add ourselves to
        // the queue here and we'll get a call directly to draw() later
        // when the ortho queue is rendered
        if (!renderer.isProcessingQueue()) {
            renderer.checkAndAdd(this);
        }
    }

    // documentation inherited
    public void draw (Renderer renderer)
    {
        super.draw(renderer);

        // render all of our windows
        for (int ii = 0, ll = _windows.size(); ii < ll; ii++) {
            BWindow win = (BWindow)_windows.get(ii);
            try {
                win.render(renderer);
            } catch (Throwable t) {
                Log.log.log(Level.WARNING, win + " failed in render()", t);
            }
        }
    }

    // documentation inherited
    public void findCollisions (Spatial scene, CollisionResults results)
    {
        // nothing doing
    }

    // documentation inherited
    public boolean hasCollision (Spatial scene, boolean checkTriangles)
    {
        return false; // nothing doing
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

    protected static class TargetRecord
    {
        public BComponent target;
        public BComponent priorFocus;

        public TargetRecord (BComponent target, BComponent priorFocus) {
            this.target = target;
            this.priorFocus = priorFocus;
        }
    }

    protected long _tickStamp;
    protected int _modifiers;
    protected int _mouseX, _mouseY;

    protected ArrayList _windows = new ArrayList();
    protected BComponent _hcomponent, _ccomponent;
    protected BComponent _dcomponent, _focus;
    protected ArrayList _defaults = new ArrayList();
}
