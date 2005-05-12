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

package com.jme.bui.border;

import com.jme.bui.BComponent;
import com.jme.bui.util.Insets;

/**
 * Combines two borders into a single compound border.
 */
public class CompoundBorder extends BBorder
{
    public CompoundBorder (BBorder outer, BBorder inner)
    {
        _outer = outer;
        _inner = inner;
        Insets oi = _outer.getInsets(), ii = _inner.getInsets();
        _insets = new Insets(oi.left + ii.left, oi.top + ii.top,
                             oi.right + ii.right, oi.bottom + ii.bottom);
    }

    // documentation inherited
    public Insets getInsets ()
    {
        return _insets;
    }

    // documentation inherited
    public void addGeometry (BComponent component, int x, int y)
    {
        _outer.addGeometry(component, 0, 0);
        Insets oi = _outer.getInsets();
        _inner.addGeometry(component, oi.left, oi.bottom);
    }

    // documentation inherited
    public void removeGeometry (BComponent component)
    {
        _outer.removeGeometry(component);
        _inner.removeGeometry(component);
    }

    // documentation inherited
    public void setSize (int x, int y, int width, int height)
    {
        _outer.setSize(x, y, width, height);
        Insets oi = _outer.getInsets();
        _inner.setSize(x + oi.left, y + oi.bottom,
                       width-oi.getHorizontal(), height-oi.getVertical());
    }

    protected BBorder _outer, _inner;
    protected Insets _insets;
}
