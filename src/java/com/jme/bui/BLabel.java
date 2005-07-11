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
        setText(text, true);
    }

    /**
     * Updates the text displayed by this label.
     *
     * @param invalidate if false, the label will not cause a revalidation
     * as a result of this text update. This should only be used when the
     * caller knows the label's size will not change as a result of the
     * text change (this is used by {@link BTextField}, for example).
     */
    public void setText (String text, boolean invalidate)
    {
        _text = text;

        // if we're already part of the hierarchy, recreate our glyps
        if (isAdded()) {
            recreateGlyphs();
        }

        // if our text change is allowed to impact the layout hierarchy,
        // invalidate ourselves, otherwise relayout just ourselves
        if (invalidate) {
            invalidate();
        } else {
            layout();
        }
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
            _node.detachChild(_icon.getQuad());
        }
        _icon = icon;
        if (_icon != null) {
            nwidth = _icon.getWidth();
            nheight = _icon.getHeight();
            _node.attachChild(_icon.getQuad());
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

        int width = 0;
        if (_icon != null) {
            width += _icon.getWidth();
        }
        if (_tgeom != null) {
            if (width != 0) {
                width += _gap;
            }
            width += _tgeom.getSize().width;
        }

        int height = 0;
        if (_icon != null) {
            height = _icon.getHeight();
        }
        if (_tgeom != null) {
            height = Math.max(height, _tgeom.getSize().height);
        }

        Insets insets = getInsets();
        int xoff;
        switch (_halign) {
        case CENTER: xoff = (_width - width) / 2; break;
        case RIGHT: xoff = _width - width - insets.right; break;
        default:
        case LEFT: xoff = insets.left;
        }

        if (_icon != null) {
            _icon.getQuad().setLocalTranslation(
                new Vector3f(xoff + _icon.getWidth()/2,
                             getYOffset(insets, _icon.getHeight()) +
                             _icon.getHeight()/2, 0));
            xoff += (_icon.getWidth() + _gap);
        }

        if (_tgeom != null) {
            _tgeom.setLocation(
                xoff, getYOffset(insets, _tgeom.getSize().height));
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
            _node.detachChild(_tgeom.getGeometry());
            _tgeom = null;
        }

        if (_text == null) {
            return;
        }

        BLookAndFeel lnf = getLookAndFeel();
        _tgeom = lnf.getTextFactory().createText(_text, lnf.getForeground());

        _node.attachChild(_tgeom.getGeometry());
        _node.updateGeometricState(0.0f, true);
        _node.updateRenderState();
    }

    // documentation inherited
    protected Dimension computePreferredSize ()
    {
        int width = 0, height = 0;
        if (_icon != null) {
            width = _icon.getWidth();
            height = _icon.getHeight();
        }
        if (_tgeom != null) {
            width += _tgeom.getSize().width;
            height = Math.max(height, _tgeom.getSize().height);
        }
        return new Dimension(width, height);
    }

    protected String _text;
    protected BIcon _icon;

    protected BText _tgeom;
    protected int _halign = LEFT, _valign = CENTER;
    protected int _gap;
}
