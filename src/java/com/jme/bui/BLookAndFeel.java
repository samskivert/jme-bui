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

import java.net.URL;

import com.jme.bui.ScaledBackground;
import com.jme.bui.TiledBackground;
import com.jme.bui.font.BBitmapFont;
import com.jme.bui.font.BFont;
import com.jme.bui.text.BKeyMap;
import com.jme.bui.text.DefaultKeyMap;
import com.jme.renderer.ColorRGBA;

/**
 * Defines the look and feel of a hierarchy of user interface components.
 * Every component has an associated look and feel, if one has not been
 * specified explicitly, the component's parent will be queried for its
 * look and feel and that instance will be used to control the component's
 * look and behavior.
 */
public class BLookAndFeel
{
    /**
     * Creates an unconfigured look and feel.
     */
    public BLookAndFeel ()
    {
    }

    /**
     * Returns the font to be used when configuring components.
     */
    public BFont getFont ()
    {
        return _font == null ? _parent.getFont() : _font;
    }

    /**
     * Configures the font used by this look and feel.
     */
    public void setFont (BFont font)
    {
        _font = font;
    }

    /**
     * Returns the foreground color to be used when configuring components.
     */
    public ColorRGBA getForeground ()
    {
        return _foreground == null ? _parent.getForeground() : _foreground;
    }

    /**
     * Configures the foreground color used by this look and feel.
     */
    public void setForeground (ColorRGBA color)
    {
        _foreground = color;
    }

    /**
     * Returns the background color to be used when configuring components.
     */
    public ColorRGBA getBackground ()
    {
        return _background == null ? _parent.getBackground() : _background;
    }

    /**
     * Configures the background color used by this look and feel.
     */
    public void setBackground (ColorRGBA color)
    {
        _background = color;
    }

    /**
     * Returns the keymap in effect for this look and feel.
     */
    public BKeyMap getKeyMap ()
    {
        return _keymap == null ? _parent.getKeyMap() : _keymap;
    }

    /**
     * Configures the keymap to be used by this look and feel.
     */
    public void setKeyMap (BKeyMap keymap)
    {
        _keymap = keymap;
    }

    /**
     * Creates a button background to use when a button is in the
     * specified state.
     */
    public BBackground createButtonBack (int state)
    {
        String path;
        switch (state) {
        case BButton.DOWN: path = "/rsrc/textures/button_down.png"; break;
        case BButton.OVER: path = "/rsrc/textures/button_up.png"; break;
        default:
        case BButton.UP: path = "/rsrc/textures/button_up.png"; break;
        }
        return new TiledBackground(
            BLookAndFeel.class.getResource(path), 5, 3, 5, 3);
    }

    /**
     * Creates the background used for text components.
     */
    public BBackground createTextBack ()
    {
        String path = "/rsrc/textures/button_up.png";
        return new TiledBackground(
            BLookAndFeel.class.getResource(path), 5, 3, 5, 3);
    }

    /**
     * Creates a look and feel that defaults to the configuration of this
     * look and feel but can be further customized.
     */
    public BLookAndFeel deriveLookAndFeel ()
    {
        return new BLookAndFeel(this);
    }

    protected BLookAndFeel (BLookAndFeel parent)
    {
        _parent = parent;
    }

    /**
     * Returns a basic default look and feel.
     */
    public static BLookAndFeel getDefaultLookAndFeel ()
    {
        BLookAndFeel lnf = new BLookAndFeel();
        lnf.setForeground(ColorRGBA.white);
        lnf.setBackground(ColorRGBA.black);
        URL url = BLookAndFeel.class.getResource("/rsrc/fonts/default.png");
        lnf.setFont(new BBitmapFont(url, 10, 16));
        lnf.setKeyMap(new DefaultKeyMap());
        return lnf;
    }

    protected BLookAndFeel _parent;
    protected BFont _font;
    protected ColorRGBA _foreground, _background;
    protected BKeyMap _keymap;
}
