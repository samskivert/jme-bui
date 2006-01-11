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

import java.util.ArrayList;

import com.jme.renderer.Renderer;

import com.jmex.bui.icon.BIcon;
import com.jmex.bui.text.BText;
import com.jmex.bui.text.BTextFactory;
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
        _value = text;
        _twidth = Integer.MAX_VALUE;

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
        return _value;
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
        if (_text != null) {
            if (_icon != null) {
                gap = _gap;
            }
            twidth = _text.size.width;
            theight = _text.size.height;
        }

        int width, height;
        switch (_orient) {
        default:
        case HORIZONTAL:
            width = iwidth + gap + twidth;
            height = Math.max(iheight, theight);
            break;
        case VERTICAL:
            width = Math.max(iwidth, twidth);
            height = iheight + gap + theight;
            break;
        case OVERLAPPING:
            width = Math.max(iwidth, twidth);
            height = Math.max(iheight, theight);
            break;
        }

        return new Dimension(width, height);
    }

    /**
     * Lays out the label text and icon.
     */
    public void layout ()
    {
        // compute the available width into which we can lay out our text
        Insets insets = _container.getInsets();
        int twidth = _container.getWidth() - insets.getHorizontal();
        if (_icon != null && _orient == HORIZONTAL) {
            twidth -= _icon.getWidth();
            twidth -= _gap;
        }
        // if the width changed, re-line-break our text
        if (twidth != _twidth) {
            _twidth = twidth;
            recreateGlyphs();
        }

        // now compute any offsets needed to center or align things
        Dimension size = computePreferredSize(-1, -1);
        int xoff = 0, yoff = 0;
        switch (_orient) {
        case HORIZONTAL:
            if (_icon != null) {
                _ix = getXOffset(insets, size.width);
                _iy = getYOffset(insets, _icon.getHeight());
                xoff = (_icon.getWidth() + _gap);
            }
            if (_text != null) {
                _tx = getXOffset(insets, size.width) + xoff;
                _ty = getYOffset(insets, _text.size.height);
            }
            break;

        case VERTICAL:
            if (_text != null) {
                _tx = getXOffset(insets, _text.size.width);
                _ty = getYOffset(insets, size.height);
                yoff = (_text.size.height + _gap);
            }
            if (_icon != null) {
                _ix = getXOffset(insets, _icon.getWidth());
                _iy = getYOffset(insets, size.height) + yoff;
            }
            break;

        case OVERLAPPING:
            if (_icon != null) {
                _ix = getXOffset(insets, _icon.getWidth());
                _iy = getYOffset(insets, _icon.getHeight());
            }
            if (_text != null) {
                _tx = getXOffset(insets, _text.size.width);
                _ty = getYOffset(insets, _text.size.height);
            }
            break;
        }
    }

    /**
     * Renders the label text and icon.
     */
    public void render (Renderer renderer)
    {
        if (_icon != null) {
            _icon.render(renderer, _ix, _iy);
        }
        if (_text != null) {
            _text.render(renderer, _tx, _ty,
                         _container.getHorizontalAlignment());
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
        if (_text != null) {
            _text = null;
        }
        if (_value == null) {
            return;
        }

        _text = new Text();
        ArrayList lines = new ArrayList();
        BTextFactory tfact = _container.getTextFactory();
        String text = _value;
        int[] remain = new int[] { _value.length() };
        while (remain[0] > 0) {
            text = text.substring(text.length()-remain[0]);
            BText line = tfact.wrapText(
                text, _container.getColor(), _container.getTextEffect(),
                _container.getEffectColor(), _twidth, remain);
            _text.size.width = Math.max(_text.size.width, line.getSize().width);
            _text.size.height += line.getSize().height;
            lines.add(line);
        }
        _text.lines = (BText[])lines.toArray(new BText[lines.size()]);
    }

    protected static class Text
    {
        public BText[] lines;

        public Dimension size = new Dimension();

        public void render (Renderer renderer, int tx, int ty, int halign)
        {
            // render the lines from the bottom up
            for (int ii = lines.length-1; ii >= 0; ii--) {
                int lx = tx;
                if (halign == RIGHT) {
                    lx += size.width - lines[ii].getSize().width;
                } else if (halign == CENTER) {
                    lx += (size.width - lines[ii].getSize().width)/2;
                }
                lines[ii].render(renderer, lx, ty);
                ty += lines[ii].getSize().height;
            }
        }
    }

    protected BTextComponent _container;
    protected String _value;

    protected int _orient = HORIZONTAL;
    protected int _gap;

    protected BIcon _icon;
    protected int _ix, _iy;

    protected Text _text;
    protected int _tx, _ty, _twidth = Integer.MAX_VALUE;
}
