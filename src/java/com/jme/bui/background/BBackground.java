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

package com.jme.bui.background;

import com.jme.bui.util.Insets;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;

/**
 * Provides additional information about a background that is used to
 * display the backgrounds of various components.
 */
public abstract class BBackground
{
    /**
     * Returns the left inset that should be used by components rendered
     * inside this background.
     */
    public int getLeftInset ()
    {
        return _left;
    }

    /**
     * Returns the top inset that should be used by components rendered
     * inside this background.
     */
    public int getTopInset ()
    {
        return _top;
    }

    /**
     * Returns the right inset that should be used by components rendered
     * inside this background.
     */
    public int getRightInset ()
    {
        return _right;
    }

    /**
     * Returns the bottom inset that should be used by components rendered
     * inside this background.
     */
    public int getBottomInset ()
    {
        return _bottom;
    }

    /**
     * Returns the width of the "contents" of this background which is the
     * total width minus the insets.
     */
    public int getContentWidth (int width)
    {
        return width - _left - _right;
    }

    /**
     * Returns the height of the "contents" of this background which is
     * the total height minus the insets.
     */
    public int getContentHeight (int height)
    {
        return height - _top - _bottom;
    }

    /**
     * Creates a new set of insets that expand the supplied set by this
     * background's insets.
     */
    public Insets adjustInsets (Insets insets)
    {
        Insets ainsets = new Insets(insets);
        ainsets.left += getLeftInset();
        ainsets.top += getTopInset();
        ainsets.right += getRightInset();
        ainsets.bottom += getBottomInset();
        return ainsets;
    }

    /** Renders this background. */
    public void render (Renderer renderer, int x, int y, int width, int height)
    {
        Spatial.applyDefaultStates();
    }

    /** Configures this background with its insets. */
    protected BBackground (int left, int top, int right, int bottom)
    {
        _left = left;
        _top = top;
        _right = right;
        _bottom = bottom;
    }

    protected int _left, _top, _right, _bottom;
}
