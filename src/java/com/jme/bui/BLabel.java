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

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Text;
import com.jme.scene.shape.Quad;
import com.jme.system.DisplaySystem;

import com.jme.bui.text.BText;
import com.jme.bui.util.Dimension;
import com.jme.bui.util.Insets;

/**
 * A simple component for displaying a textual label.
 */
public class BLabel extends BComponent
    implements BConstants
{
    /**
     * Creates a label that will display the supplied text.
     */
    public BLabel (String text)
    {
        setText(text);
    }

    /**
     * Creates a label that will display the supplied icon.
     */
    public BLabel (BIcon icon)
    {
        setIcon(icon);
    }

    /**
     * Returns the text currently being displayed by this label.
     */
    public String getText ()
    {
        return _text;
    }

    /**
     * Returns the icon being displayed by this label.
     */
    public BIcon getIcon ()
    {
        return _icon;
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
        if (isAdded()) {
            layout();
        }
    }

    /**
     * Configures this label's horizontal alignment.
     */
    public void setHorizontalAlignment (int align)
    {
        if (_halign != align) {
            _halign = align;
            if (isAdded()) {
                layout();
            }
        }
    }

    /**
     * Returns this label's horizontal alignment setting.
     */
    public int getHorizontalAlignment ()
    {
        return _halign;
    }

    /**
     * Configures this label's vertical alignment.
     */
    public void setVerticalAlignment (int align)
    {
        if (_valign != align) {
            _valign = align;
            if (isAdded()) {
                layout();
            }
        }
    }

    /**
     * Returns this label's vertical alignment setting.
     */
    public int getVerticalAlignment ()
    {
        return _valign;
    }

    /**
     * Updates the text displayed by this label.
     */
    public void setText (String text)
    {
        _text = text;

        // if we're already part of the hierarchy, recreate our glyps
        if (isAdded()) {
            recreateGlyphs();
        }

        // our size may have changed so we need to revalidate
        invalidate();
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
            invalidate();
        } else {
            layout();
        }
    }

    /**
     * Configures the gap between the icon and the text.
     */
    public void setIconTextGap (int gap)
    {
        _gap = gap;
    }

    // documentation inherited
    protected void wasAdded ()
    {
        super.wasAdded();

        // create our underlying glyphs
        recreateGlyphs();
    }

    // documentation inherited
    protected void layout ()
    {
        super.layout();

        Dimension size = computePreferredSize();
        Insets insets = getInsets();
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

    protected int getXOffset (Insets insets, int width)
    {
        switch (_halign) {
        default:
        case LEFT: return insets.left;
        case RIGHT: return _width - width - insets.right;
        case CENTER: return (_width - width) / 2;
        }
    }

    protected int getYOffset (Insets insets, int height)
    {
        switch (_valign) {
        default:
        case TOP: return _height - height - insets.top;
        case BOTTOM: return insets.bottom;
        case CENTER: return (_height - height) / 2;
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

        BLookAndFeel lnf = getLookAndFeel();
        _tgeom = lnf.getTextFactory().createText(_text, lnf.getForeground());
    }

    // documentation inherited
    protected void renderComponent (Renderer renderer)
    {
        super.renderComponent(renderer);
        if (_tgeom != null) {
            _tgeom.render(renderer, _tx, _ty);
        }
        if (_icon != null) {
            _icon.render(renderer, _ix, _iy);
        }
    }

    // documentation inherited
    protected Dimension computePreferredSize ()
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

    protected String _text;
    protected BIcon _icon;
    protected int _ix, _iy;

    protected BText _tgeom;
    protected int _tx, _ty;
    protected int _halign = LEFT, _valign = CENTER;
    protected int _gap;
    protected int _orient = HORIZONTAL;
}
