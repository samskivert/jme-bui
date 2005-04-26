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

package com.jme.bui;

import java.awt.Dimension;

import com.jme.bui.event.BEvent;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.system.DisplaySystem;
import com.jme.scene.state.AlphaState;

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
        setName(getClass().getName() + hashCode());

        AlphaState as = DisplaySystem.getDisplaySystem().getRenderer().
            createAlphaState();
        as.setBlendEnabled(true);
        as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        as.setTestEnabled(true);
        as.setTestFunction(AlphaState.TF_GREATER);
        setRenderState(as);
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
        _x = x;
        _y = y;
        updateNodeTranslation();
    }

    /**
     * Sets the width and height of this component in screen coordinates.
     */
    public void setSize (int width, int height)
    {
        _width = width;
        _height = height;
        updateNodeTranslation();
    }

    /**
     * Sets the bounds of this component in screen coordinates.
     *
     * @see #setLocation
     * @see #setSize
     */
    public void setBounds (int x, int y, int width, int height)
    {
        _x = x;
        _y = y;
        _width = width;
        _height = height;
        updateNodeTranslation();
    }

    /**
     * Returns true if this component is added to a hierarchy of
     * components that culminates in a top-level window.
     */
    public boolean isAdded ()
    {
        return (getWindow() != null);
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
        // nothing to do by default
        Log.log.info(this + " dispatching " + event);
    }

//     // documentation inherited
//     public void setParent (Node parent)
//     {
//         // make a note of whether or not we were attached to an "added"
//         // hierarchy before we allow our parent reference to be cleared
//         boolean hadBeenAdded = isAdded();

//         super.setParent(parent);

//         if (parent == null) {
//             // if we had been "added" to a hierarchy connected to a
//             // window, we need to call wasRemoved()
//             if (hadBeenAdded) {
//                 wasRemoved();
//             }

//         } else if (isAdded()) {
//             // if our parent is already added to a hierarcy connected to a
//             // top-level window, then we can call wasAdded() immediately;
//             // otherwise when our parent is added, it will roll a call to
//             // wasAdded() down the hierarchy
//             wasAdded();
//         }
//     }

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

    /**
     * Called when our dimensions change to update the position of our
     * underlying node.
     */
    protected void updateNodeTranslation ()
    {
        setLocalTranslation(new Vector3f(_x + _width/2, _y + _height/2, 0f));
    }

    protected BLookAndFeel _lnf;
    protected Dimension _preferredSize;
    protected int _x, _y, _width, _height;
}
