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

package com.jme.bui;

import java.awt.Dimension;
import java.util.ArrayList;

import com.jme.bui.event.BEvent;
import com.jme.bui.event.ComponentListener;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;

/**
 * The basic entity in the BUI user interface system. A hierarchy of
 * components and component derivations make up a user interface.
 */
public class BComponent extends Node
{
    public BComponent ()
    {
        super("");
        // we can't pass our auto-generated name to our superclass
        // constructor because those methods cannot be called until it
        // finishes execution, so we construct with a blank name and set a
        // valid one immediately
        setName(getClass().getName() + ":" + hashCode());

        setRenderQueueMode(Renderer.QUEUE_ORTHO);
    }

    /**
     * Configures this component with a look and feel that will be used to
     * render it and all of its children (unless those children are
     * configured themselves with a custom look and feel).
     */
    public void setLookAndFeel (BLookAndFeel lnf)
    {
        _lnf = lnf;
    }

    /**
     * Returns the preferred size of this component.
     */
    public Dimension getPreferredSize ()
    {
        return (_preferredSize == null) ?
            computePreferredSize() : _preferredSize;
    }

    /**
     * Configures the preferred size of this component. This will override
     * any information provided by derived classes that have opinions
     * about their preferred size.
     */
    public void setPreferredSize (Dimension preferredSize)
    {
        _preferredSize = preferredSize;
    }

    /** Returns the x coordinate of this component. */
    public int getX ()
    {
        return _x;
    }

    /** Returns the y coordinate of this component. */
    public int getY ()
    {
        return _y;
    }

    /** Returns the width of this component. */
    public int getWidth ()
    {
        return _width;
    }

    /** Returns the height of this component. */
    public int getHeight ()
    {
        return _height;
    }

    /**
     * Sets the upper left position of this component in absolute screen
     * coordinates.
     */
    public void setLocation (int x, int y)
    {
        setBounds(x, y, _width, _height);
    }

    /**
     * Sets the width and height of this component in screen coordinates.
     */
    public void setSize (int width, int height)
    {
        setBounds(_x, _y, width, height);
    }

    /**
     * Sets the bounds of this component in screen coordinates.
     *
     * @see #setLocation
     * @see #setSize
     */
    public void setBounds (int x, int y, int width, int height)
    {
        if (_x != x || _y != y) {
            _x = x;
            _y = y;
            setLocalTranslation(new Vector3f(_x, _y, 0f));
        }
        _width = width;
        _height = height;
    }

    /**
     * Adds a listener to this component. The listener will be notified
     * when events of the appropriate type are dispatched on this
     * component.
     */
    public void addListener (ComponentListener listener)
    {
        if (_listeners == null) {
            _listeners = new ArrayList();
        }
        _listeners.add(listener);
    }

    /**
     * Removes a listener from this component. Returns true if the
     * listener was in fact in the listener list for this component, false
     * if not.
     */
    public boolean removeListener (ComponentListener listener)
    {
        if (_listeners != null) {
            return _listeners.remove(listener);
        } else {
            return false;
        }
    }

    /**
     * Returns true if this component is added to a hierarchy of
     * components that culminates in a top-level window.
     */
    public boolean isAdded ()
    {
        BWindow win = getWindow();
        return (win != null && win.isAdded());
    }

    /**
     * Instructs this component to lay itself out. This happens
     * automatically when the component is attached to the interface
     * hierarchy, but can be called again if the component changes in such
     * a manner as to need a relayout.
     */
    public void layout ()
    {
        // we have nothing to do by default
    }

    /**
     * Returns the component "hit" by the specified mouse coordinates
     * which might be this component or any of its children. This method
     * should return null if the supplied mouse coordinates are outside
     * the bounds of this component.
     */
    public BComponent getHitComponent (int mx, int my)
    {
	if ((mx >= _x) && (my >= _y) &&
            (mx < _x + _width) && (my < _y + _height)) {
            return this;
        }
        return null;
    }

    /**
     * Instructs this component to process the supplied event.
     */
    public void dispatchEvent (BEvent event)
    {
        // dispatch this event to our listeners
        if (_listeners != null) {
            for (int ii = 0, ll = _listeners.size(); ii < ll; ii++) {
                event.dispatch((ComponentListener)_listeners.get(ii));
            }
        }
    }

    /**
     * Computes and returns a preferred size for this component. This
     * method is called if no overriding preferred size has been supplied.
     */
    protected Dimension computePreferredSize ()
    {
        return new Dimension(0, 0);
    }

    /**
     * This method is called when we are added to a hierarchy that is
     * connected to a top-level window (at which point we can rely on
     * having a look and feel and can set ourselves up).
     */
    protected void wasAdded ()
    {
    }

    /**
     * This method is called when we are removed from a hierarchy that is
     * connected to a top-level window. If we wish to clean up after
     * things done in {@link #wasAdded}, this is a fine place to do so.
     */
    protected void wasRemoved ()
    {
    }

    /**
     * Called by a component after it has changed internally in such a way
     * as to require a relayout, this method climbs the interface
     * hierarchy to the containing window and forces a full relayout.
     */
    protected void relayout ()
    {
        BWindow window = getWindow();
        if (window != null) {
            window.layout();
        }
    }

    /**
     * Returns a reference to the look and feel in scope for this
     * component.
     */
    protected BLookAndFeel getLookAndFeel ()
    {
        // FYI: (null instanceof Whatever) is always false
        return (_lnf == null && parent instanceof BComponent) ?
            ((BComponent)parent).getLookAndFeel() : _lnf;
    }

    /**
     * Returns the window that defines the root of our component
     * hierarchy.
     */
    protected BWindow getWindow ()
    {
        if (this instanceof BWindow) {
            return (BWindow)this;
        } else if (parent instanceof BComponent) {
            return ((BComponent)parent).getWindow();
        } else {
            return null;
        }
    }

    protected BLookAndFeel _lnf;
    protected Dimension _preferredSize;
    protected int _x, _y, _width, _height;
    protected ArrayList _listeners;
}
