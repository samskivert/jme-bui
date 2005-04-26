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
            _components[((Integer)constraints).intValue()] = comp;
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

        BComponent comp = _components[NORTH.intValue()];
        if (comp != null) {
            Dimension cpsize = comp.getPreferredSize();
            psize.width = Math.max(psize.width, cpsize.width);
            psize.height += cpsize.height;
            vertComps++;
        }

        comp = _components[SOUTH.intValue()];
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
        int x = target.getX(), y = target.getY();
        int width = target.getWidth(), height = target.getHeight();

        BComponent comp = _components[NORTH.intValue()];
        if (comp != null) {
            comp.setLocation(x, y);
            Dimension cpsize = comp.getPreferredSize();
            comp.setSize(width, cpsize.height);
            y += (cpsize.height + _vgap);
            height -= (cpsize.height + _vgap);
        }

        comp = _components[SOUTH.intValue()];
        if (comp != null) {
            Dimension cpsize = comp.getPreferredSize();
            comp.setLocation(x, target.getY() + target.getHeight() -
                             cpsize.height);
            comp.setSize(width, cpsize.height);
            height -= (cpsize.height + _vgap);
        }

        comp = _components[WEST.intValue()];
        if (comp != null) {
            comp.setLocation(x, y);
            Dimension cpsize = comp.getPreferredSize();
            comp.setSize(cpsize.width, height);
            x += (cpsize.width + _hgap);
            width -= (cpsize.width + _hgap);
        }

        comp = _components[EAST.intValue()];
        if (comp != null) {
            Dimension cpsize = comp.getPreferredSize();
            comp.setLocation(target.getX() + target.getWidth() -
                             cpsize.width, y);
            comp.setSize(cpsize.width, height);
            width -= (cpsize.width + _hgap);
        }

        comp = _components[CENTER.intValue()];
        if (comp != null) {
            comp.setLocation(x, y);
            comp.setSize(width, height);
        }
    }

    protected int _hgap, _vgap;
    protected BComponent[] _components = new BComponent[5];
}
