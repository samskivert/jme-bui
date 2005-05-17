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

import com.jme.bui.event.InputDispatcher;
import com.jme.bui.layout.BLayoutManager;
import com.jme.bui.util.Dimension;
import com.jme.bui.util.Insets;
import com.jme.renderer.Renderer;

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
        _node.setRenderQueueMode(Renderer.QUEUE_ORTHO);
    }

    /**
     * Sizes this window to its preferred size. This method does not
     * change the window's coordinates.
     */
    public void pack ()
    {
        Dimension ps = getPreferredSize();
        Log.log.info("Sizing to " + ps);
        setBounds(_x, _y, ps.width, ps.height);
    }

    /**
     * Configures this window with a background. This should be called
     * before any components are added to the window to ensure proper
     * render order.
     */
    public void setBackground (BBackground background)
    {
        if (_background != null) {
            _node.detachChild(_background.getNode());
        }
        _background = background;
        _node.attachChild(_background.getNode());
    }

    /**
     * Configures this window with its input dispatcher. Do not call this
     * method, it is called automatically when a window is added to a
     * dispatcher via a call to {@link InputDispatcher#addWindow}.
     */
    public void setInputDispatcher (InputDispatcher dispatcher)
    {
        if (_dispatcher != dispatcher) {
            _dispatcher = dispatcher;
            if (_dispatcher == null) {
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

    // documentation inherited
    public void invalidate ()
    {
        super.invalidate();

        if (_dispatcher != null) {
            // when an invalidation call reaches an attached top-level
            // window, we start the revalidation process
            validate();
        }
    }

    // documentation inherited
    public boolean isAdded ()
    {
        return _dispatcher != null;
    }

    // documentation inherited
    public Insets getInsets ()
    {
        Insets insets = super.getInsets();
        if (_background != null) {
            Insets cinsets = new Insets(insets);
            cinsets.left += _background.getLeftInset();
            cinsets.top += _background.getTopInset();
            cinsets.right += _background.getRightInset();
            cinsets.bottom += _background.getBottomInset();
            insets = cinsets;
        }
        return insets;
    }

    // documentation inherited
    protected void layout ()
    {
        super.layout();

        if (_background != null) {
            // our background occupies our entire dimensions
            _background.setBounds(0, 0, _width, _height);
        }
    }

    /**
     * Requests that the specified component be given the input focus.
     */
    protected void requestFocus (BComponent component)
    {
        if (_dispatcher == null) {
            Log.log.warning("Un-added window requested to change focus " +
                            "[win=" + this + ", focus=" + component + "].");
            Thread.dumpStack();
        } else {
            _dispatcher.requestFocus(component);
        }
    }

    /** The dispatcher that handles our events. */
    protected InputDispatcher _dispatcher;

    /** The background we display to denote our window. */
    protected BBackground _background;
}
