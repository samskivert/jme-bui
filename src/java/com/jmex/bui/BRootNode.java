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
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Spatial;

import com.jmex.bui.Log;
import com.jmex.bui.event.BEvent;
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

        // if the current top window has a focus, store it and clear the focus
        if (_focus != null) {
            ((BWindow)_windows.get(_windows.size()-1))._savedFocus = _focus;
            setFocus(null);
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

        // clear any saved focus reference
        window._savedFocus = null;

        // first remove the window from our list
        if (!_windows.remove(window)) {
            Log.log.warning("Requested to remove unmanaged window " +
                            "[window=" + window + "].");
            Thread.dumpStack();
            return;
        }

        // then remove the hover component (which may result in a mouse exited
        // even being dispatched to the window or one of its children)
        computeHoverComponent(_mouseX, _mouseY);

        // remove the window from the interface heirarchy
        window.setRootNode(null);

        // finally restore the focus to the new top-most window if it has a
        // saved focus
        if (_windows.size() > 0) {
            BWindow top = (BWindow)_windows.get(_windows.size()-1);
            setFocus(top._savedFocus);
            top._savedFocus = null;
        }
    }

    /**
     * This is called by a window or a scroll pane when it has become invalid.
     * The root node should schedule a revalidation of this component on the
     * next tick or the next time an event is processed.
     */
    public abstract void rootInvalidated (BComponent root);

    /**
     * Configures a component to receive all events that are not sent to some
     * other component. When an event is not consumed during normal processing,
     * it is sent to the default event targets, most recently registered to
     * least recently registered.
     */
    public void pushDefaultEventTarget (BComponent component)
    {
        _defaults.add(component);
    }

    /**
     * Pops the default event target off the stack.
     */
    public void popDefaultEventTarget (BComponent component)
    {
        _defaults.remove(component);
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
    public void updateGeometricState (float time, boolean initiator)
    {
        super.updateGeometricState(time, initiator);

        // update our geometry views if we have any
        for (int ii = 0, ll = _geomviews.size(); ii < ll; ii++) {
            ((BGeomView)_geomviews.get(ii)).update(time);
        }
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
     * Dispatches an event to the specified target (which may be null). If the
     * target is null, or did not consume the event, it will be passed on to
     * the most recently opened modal window if one exists (and the supplied
     * target component was not a child of that window) and then to the default
     * event targets if the event is still unconsumed.
     */
    protected void dispatchEvent (BComponent target, BEvent event)
    {
        // first try the "natural" target of the event if there is one
        BWindow sentwin = null;
        if (target != null) {
            if (target.dispatchEvent(event)) {
                return;
            }
            sentwin = target.getWindow();
        }

        // next try the most recently opened modal window, if we have one
        for (int ii = _windows.size()-1; ii >= 0; ii--) {
            BWindow window = (BWindow)_windows.get(ii);
            if (window.isModal()) {
                if (window != sentwin) {
                    if (window.dispatchEvent(event)) {
                        return;
                    }
                }
                break;
            }
        }

        // finally try the default event targets
        for (int ii = _defaults.size()-1; ii >= 0; ii--) {
            BComponent deftarg = (BComponent)_defaults.get(ii);
            if (deftarg.dispatchEvent(event)) {
                return;
            }
        }
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
     * Registers a {@link BGeomView} with the root node. This is called
     * automatically from {@link BGeomView#wasAdded}.
     */
    protected void registerGeomView (BGeomView nview)
    {
        _geomviews.add(nview);
    }

    /**
     * Clears out a node view registration. This is called automatically from
     * {@link BGeomView#wasRemoved}.
     */
    protected void unregisterGeomView (BGeomView nview)
    {
        _geomviews.remove(nview);
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

    protected long _tickStamp;
    protected int _modifiers;
    protected int _mouseX, _mouseY;

    protected ArrayList _windows = new ArrayList();
    protected BComponent _hcomponent, _ccomponent;
    protected BComponent _dcomponent, _focus;
    protected ArrayList _defaults = new ArrayList();
    protected ArrayList _geomviews = new ArrayList();
}
