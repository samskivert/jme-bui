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

package com.jme.bui.layout;

import java.awt.Dimension;

import com.jme.bui.BComponent;
import com.jme.bui.BContainer;

/**
 * Lays out the children of a container like so:
 * <pre>
 * +------------------------------------+
 * |               NORTH                |
 * +-----+------------------------+-----+
 * |     |                        |     |
 * |  W  |         CENTER         |  E  |
 * |     |                        |     |
 * +-----+------------------------+-----+
 * |               SOUTH                |
 * +------------------------------------+
 * </pre>
 */
public class BorderLayout extends BLayoutManager
{
    /** A layout constraint. */
    public static final Integer NORTH = new Integer(0);

    /** A layout constraint. */
    public static final Integer SOUTH = new Integer(1);

    /** A layout constraint. */
    public static final Integer EAST = new Integer(2);

    /** A layout constraint. */
    public static final Integer WEST = new Integer(3);

    /** A layout constraint. */
    public static final Integer CENTER = new Integer(4);

    /** A layout constraint. */
    public static final Integer IGNORE = new Integer(5);

    /**
     * Creates a border layout with zero gap between the horizontal
     * components and zero gap between the vertical.
     */
    public BorderLayout ()
    {
        this(0, 0);
    }

    /**
     * Creates a border layout with the specified gap between the
     * horizontal components and the specified gap between the vertical.
     */
    public BorderLayout (int hgap, int vgap)
    {
        _hgap = hgap;
        _vgap = vgap;
    }

    // documentation inherited
    public void addLayoutComponent (BComponent comp, Object constraints)
    {
        if (constraints instanceof Integer) {
            if (constraints != IGNORE) {
                _components[((Integer)constraints).intValue()] = comp;
            }
        } else {
            throw new IllegalArgumentException(
                "Components added to a BorderLayout must have proper " +
                "constraints (eg. BorderLayout.NORTH).");
        }
    }

    // documentation inherited
    public void removeLayoutComponent (BComponent comp)
    {
        for (int ii = 0; ii < _components.length; ii++) {
            if (_components[ii] == comp) {
                _components[ii] = null;
                break;
            }
        }
    }

    // documentation inherited
    public Dimension computePreferredSize (BContainer target)
    {
        Dimension psize = new Dimension();
        int horizComps = 0, vertComps = 0;

        BComponent comp = _components[SOUTH.intValue()];
        if (comp != null) {
            Dimension cpsize = comp.getPreferredSize();
            psize.width = Math.max(psize.width, cpsize.width);
            psize.height += cpsize.height;
            vertComps++;
        }

        comp = _components[NORTH.intValue()];
        if (comp != null) {
            Dimension cpsize = comp.getPreferredSize();
            psize.width = Math.max(psize.width, cpsize.width);
            psize.height += cpsize.height;
            vertComps++;
        }

        int centerWidth = 0, centerHeight = 0;
        for (int ii = EAST.intValue(); ii <= CENTER.intValue(); ii++) {
            comp = _components[ii];
            if (comp != null) {
                Dimension cpsize = comp.getPreferredSize();
                centerWidth += cpsize.width;
                centerHeight = Math.max(centerHeight, cpsize.height);
                horizComps++;
            }
        }

        psize.width = Math.max(psize.width, centerWidth);
        psize.height += centerHeight;

        // add in the gaps
        psize.width += Math.max(horizComps - 1, 0) * _hgap;
        psize.height += Math.max(vertComps - 1, 0) * _vgap;

        return psize;
    }

    // documentation inherited
    public void layoutContainer (BContainer target)
    {
        // determine what we've got to work with
        int x = 0, y = 0;
        int width = target.getWidth(), height = target.getHeight();

        BComponent comp = _components[SOUTH.intValue()];
        if (comp != null) {
            Dimension cpsize = comp.getPreferredSize();
            comp.setBounds(x, y, width, cpsize.height);
            y += (cpsize.height + _vgap);
            height -= (cpsize.height + _vgap);
        }

        comp = _components[NORTH.intValue()];
        if (comp != null) {
            Dimension cpsize = comp.getPreferredSize();
            comp.setBounds(x, target.getHeight() - cpsize.height,
                           width, cpsize.height);
            height -= (cpsize.height + _vgap);
        }

        comp = _components[WEST.intValue()];
        if (comp != null) {
            Dimension cpsize = comp.getPreferredSize();
            comp.setBounds(x, y, cpsize.width, height);
            x += (cpsize.width + _hgap);
            width -= (cpsize.width + _hgap);
        }

        comp = _components[EAST.intValue()];
        if (comp != null) {
            Dimension cpsize = comp.getPreferredSize();
            comp.setBounds(target.getWidth() - cpsize.width, y,
                           cpsize.width, height);
            width -= (cpsize.width + _hgap);
        }

        comp = _components[CENTER.intValue()];
        if (comp != null) {
            comp.setBounds(x, y, width, height);
        }
    }

    protected int _hgap, _vgap;
    protected BComponent[] _components = new BComponent[5];
}
