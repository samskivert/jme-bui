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

package com.jme.bui.util;

/**
 * Represents insets from the edges of a component.
 */
public class Insets
{
    /** The inset from the left edge. */
    public int left;

    /** The inset from the top edge. */
    public int top;

    /** The inset from the right edge. */
    public int right;

    /** The inset from the bottom edge. */
    public int bottom;

    public Insets (int left, int top, int right, int bottom)
    {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public Insets (Insets other)
    {
        left = other.left;
        top = other.top;
        right = other.right;
        bottom = other.bottom;
    }

    public Insets ()
    {
    }

    /**
     * Returns the sum of the horizontal insets.
     */
    public int getHorizontal ()
    {
        return left + right;
    }

    /**
     * Returns the sum of the vertical insets.
     */
    public int getVertical ()
    {
        return top + bottom;
    }

    public String toString ()
    {
        return "l:" + left + " t:" + top + " r:" + right + " b:" + bottom;
    }
}
