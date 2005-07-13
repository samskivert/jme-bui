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

import com.jme.renderer.Renderer;
import com.jme.system.DisplaySystem;

import com.jme.bui.background.BBackground;
import com.jme.bui.layout.BLayoutManager;
import com.jme.bui.util.Dimension;
import com.jme.bui.util.Insets;

/**
 * A window defines the top-level of a component hierarchy. It must be
 * created with a look and feel and layout manager.
 */
public class BWindow extends BContainer
{
    public BWindow (BLookAndFeel lnf, BLayoutManager layout)
    {
        setLookAndFeel(lnf);
        setLayoutManager(layout);
    }

    /**
     * Sizes this window to its preferred size. This method does not
     * change the window's coordinates.
     */
    public void pack ()
    {
        Dimension ps = getPreferredSize();
        setBounds(_x, _y, ps.width, ps.height);
    }

    /**
     * Positions this window in the center of the display. This should be
     * called after configuring the size of the window (using, for
     * example, a call to {@link #pack}).
     */
    public void center ()
    {
        int width = DisplaySystem.getDisplaySystem().getWidth();
        int height = DisplaySystem.getDisplaySystem().getHeight();
        setLocation((width-getWidth())/2, (height-getHeight())/2);
    }

    /**
     * Configures this window to be modal which causes it to "steal" all
     * mouse and keyboard input while it is added to the interface
     * hierarchy. Mouse movement and button press events that would
     * normally go to other windows or to the default mouse target will
     * instead be sent to the top-most modal window. Other events destined
     * for other windows (key events and mouse entry and exit events) will
     * not be dispatched.
     */
    public void setModal (boolean modal)
    {
        _modal = modal;
    }

    /**
     * Returns whether or not this window is modal. See {@link #setModal}
     * for more information on modality.
     */
    public boolean isModal ()
    {
        return _modal;
    }

    /**
     * Returns the root node that manages this window.
     */
    public BRootNode getRootNode ()
    {
        return _root;
    }

    /**
     * Detaches this window from the root node and removes it from the
     * display.
     */
    public void dismiss ()
    {
        if (_root != null) {
            _root.removeWindow(this);
        } else {
            Log.log.warning("Unmanaged window dismissed [window=" + this + "].");
            Thread.dumpStack();
        }
    }

    // documentation inherited
    public void invalidate ()
    {
        super.invalidate();

        if (_root != null) {
            // when an invalidation call reaches an attached top-level
            // window, we start the revalidation process
            validate();
        }
    }

    // documentation inherited
    public boolean isAdded ()
    {
        return _root != null;
    }

    /**
     * Configures this window with its root node. Do not call this method,
     * it is called automatically when a window is added to the root node
     * via a call to {@link BRootNode#addWindow}.
     */
    protected void setRootNode (BRootNode root)
    {
        if (_root != root) {
            _root = root;
            if (_root == null) {
                wasRemoved();
            } else {
                wasAdded();
                // if we've already been configured with dimensions, start
                // the validation process, otherwise wait for whoever
                // created us to give us dimensions
                if (_width != 0 && _height != 0) {
                    validate();
                }
            }
        }
    }

    /**
     * Requests that the specified component be given the input focus.
     */
    protected void requestFocus (BComponent component)
    {
        if (_root == null) {
            Log.log.warning("Un-added window requested to change focus " +
                            "[win=" + this + ", focus=" + component + "].");
            Thread.dumpStack();
        } else {
            _root.requestFocus(component);
        }
    }

    /** The root node that connects us into the JME system. */
    protected BRootNode _root;

    /** Whether or not this window steals all input from other windows
     * further down the hierarchy. */
    protected boolean _modal;
}
