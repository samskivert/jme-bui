//
// $Id$
//
// BUI - a user interface library for the JME 3D engine
// Copyright (C) 2005-2006, Michael Bayne, All Rights Reserved
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

import com.jme.renderer.Renderer;

import com.jmex.bui.icon.BIcon;
import com.jmex.bui.text.BText;
import com.jmex.bui.util.Dimension;
import com.jmex.bui.util.Insets;

/**
 * Handles the underlying layout and rendering for {@link BLabel} and {@link
 * BButton}.
 */
public class Label
    implements BConstants
{
    public Label (BTextComponent container)
    {
        _container = container;
    }

    /**
     * Updates the text displayed by this label.
     */
    public void setText (String text)
    {
        _text = text;

        // if we're already part of the hierarchy, recreate our glyps
        if (_container.isAdded()) {
            recreateGlyphs();
        }

        // our size may have changed so we need to revalidate
        _container.invalidate();
    }

    /**
     * Returns the text currently being displayed by this label.
     */
    public String getText ()
    {
        return _text;
    }

    /**
     * Configures the label to display the specified icon.
     */
    public void setIcon (BIcon icon)
    {
        int owidth = 0, oheight = 0, nwidth = 0, nheight = 0;
        if (_icon != null) {
            owidth = _icon.getWidth();
            oheight = _icon.getHeight();
        }
        _icon = icon;
        if (_icon != null) {
            nwidth = _icon.getWidth();
            nheight = _icon.getHeight();
        }
        if (owidth != nwidth || oheight != nheight) {
            _container.invalidate();
        } else {
            _container.layout();
        }
    }

    /**
     * Returns the icon being displayed by this label.
     */
    public BIcon getIcon ()
    {
        return _icon;
    }

    /**
     * Configures the gap between the icon and the text.
     */
    public void setIconTextGap (int gap)
    {
        _gap = gap;
    }

    /**
     * Returns the gap between the icon and the text.
     */
    public int getIconTextGap ()
    {
        return _gap;
    }

    /**
     * Sets the orientation of this label with respect to its icon. If the
     * horizontal (the default) the text is displayed to the right of the
     * icon, if vertical the text is displayed below it.
     */
    public void setOrientation (int orient)
    {
        _orient = orient;
        if (_container.isAdded()) {
            _container.layout();
        }
    }

    /**
     * Called by our containing component when its state changes.
     */
    public void stateDidChange ()
    {
        if (_container.isAdded()) {
            recreateGlyphs();
        }
    }

    /**
     * Computes the preferred size of the label.
     */
    public Dimension computePreferredSize (int whint, int hhint)
    {
        int iwidth = 0, iheight = 0, twidth = 0, theight = 0, gap = 0;
        if (_icon != null) {
            iwidth = _icon.getWidth();
            iheight = _icon.getHeight();
        }
        if (_tgeom != null) {
            if (_icon != null) {
                gap = _gap;
            }
            twidth = _tgeom.getSize().width;
            theight = _tgeom.getSize().height;
        }

        int width, height;
        if (_orient == HORIZONTAL) {
            width = iwidth + gap + twidth;
            height = Math.max(iheight, theight);
        } else {
            width = Math.max(iwidth, twidth);
            height = iheight + gap + theight;
        }

        return new Dimension(width, height);
    }

    /**
     * Lays out the label text and icon.
     */
    public void layout ()
    {
        Dimension size = computePreferredSize(-1, -1);
        Insets insets = _container.getInsets();
        int xoff = 0, yoff = 0;

        if (_orient == HORIZONTAL) {
            if (_icon != null) {
                _ix = getXOffset(insets, size.width);
                _iy = getYOffset(insets, _icon.getHeight());
                xoff = (_icon.getWidth() + _gap);
            }
            if (_tgeom != null) {
                _tx = getXOffset(insets, size.width) + xoff;
                _ty = getYOffset(insets, _tgeom.getSize().height);
            }

        } else {
            if (_tgeom != null) {
                _tx = getXOffset(insets, _tgeom.getSize().width);
                _ty = getYOffset(insets, size.height);
                yoff = (_tgeom.getSize().height + _gap);
            }
            if (_icon != null) {
                _ix = getXOffset(insets, _icon.getWidth());
                _iy = getYOffset(insets, size.height) + yoff;
            }
        }
    }

    /**
     * Renders the label text and icon.
     */
    public void render (Renderer renderer)
    {
        if (_tgeom != null) {
            _tgeom.render(renderer, _tx, _ty);
        }
        if (_icon != null) {
            _icon.render(renderer, _ix, _iy);
        }
    }

    protected int getXOffset (Insets insets, int width)
    {
        switch (_container.getHorizontalAlignment()) {
        default:
        case LEFT: return insets.left;
        case RIGHT: return _container.getWidth() - width - insets.right;
        case CENTER: return (_container.getWidth() - insets.getHorizontal() -
                             width) / 2 + insets.left;
        }
    }

    protected int getYOffset (Insets insets, int height)
    {
        switch (_container.getVerticalAlignment()) {
        default:
        case TOP: return _container.getHeight() - height - insets.top;
        case BOTTOM: return insets.bottom;
        case CENTER: return (_container.getHeight() - insets.getVertical() -
                             height) / 2 + insets.bottom;
        }
    }

    /**
     * Clears out old glyphs and creates new ones for our current text.
     */
    protected void recreateGlyphs ()
    {
        if (_tgeom != null) {
            _tgeom = null;
        }
        if (_text == null) {
            return;
        }
        _tgeom = _container.getTextFactory().createText(
            _text, _container.getColor());
    }

    protected BTextComponent _container;
    protected String _text;
    protected BIcon _icon;
    protected int _ix, _iy;

    protected int _orient = HORIZONTAL;
    protected int _gap;

    protected BText _tgeom;
    protected int _tx, _ty;
}
