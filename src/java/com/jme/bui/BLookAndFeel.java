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

import java.awt.Font;
import java.net.URL;

import com.jme.bui.background.BBackground;
import com.jme.bui.background.TiledBackground;
import com.jme.bui.background.TintedBackground;
import com.jme.bui.border.BBorder;
import com.jme.bui.border.LineBorder;
import com.jme.bui.text.AWTTextFactory;
import com.jme.bui.text.BKeyMap;
import com.jme.bui.text.BText;
import com.jme.bui.text.BTextFactory;
import com.jme.bui.text.DefaultKeyMap;
import com.jme.bui.text.JMEBitmapTextFactory;
import com.jme.renderer.ColorRGBA;

/**
 * Defines the look and feel of a hierarchy of user interface components.
 * Every component has an associated look and feel, if one has not been
 * specified explicitly, the component's parent will be queried for its
 * look and feel and that instance will be used to control the component's
 * look and behavior.
 */
public class BLookAndFeel
    implements BConstants
{
    /**
     * Creates an unconfigured look and feel.
     */
    public BLookAndFeel ()
    {
    }

    /**
     * Returns the factory used to create text instances.
     */
    public BTextFactory getTextFactory ()
    {
        return _tfact == null ? _parent.getTextFactory() : _tfact;
    }

    /**
     * Configures the font used by this look and feel.
     */
    public void setTextFactory (BTextFactory tfact)
    {
        _tfact = tfact;
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
     * Creates a border for use by a decorated top-level window.
     */
    public BBorder createWindowBorder ()
    {
        return new LineBorder(ColorRGBA.white);
    }

    /**
     * Creates a background for use by a decorated top-level window.
     */
    public BBackground createWindowBackground ()
    {
        return new TintedBackground(BLACK_TINT, 10, 10, 10, 10);
    }

    /**
     * Creates a border for use by a popup window.
     */
    public BBorder createPopupBorder ()
    {
        return new LineBorder(ColorRGBA.white);
    }

    /**
     * Creates a background for use by a popup window.
     */
    public BBackground createPopupBackground ()
    {
        return new TintedBackground(ColorRGBA.darkGray, 0, 0, 0, 0);
    }

    /**
     * Creates a button background to use when a button is in the
     * specified state.
     */
    public BBackground createButtonBack (int state)
    {
        String path;
        int dx = 0, dy = 0;
        switch (state) {
        case BButton.DOWN:
            path = "rsrc/textures/button_down.png";
            dx = -1; dy = -1;
            break;
        case BButton.OVER: path = "rsrc/textures/button_up.png"; break;
        case BToggleButton.SELECTED:
            path = "rsrc/textures/button_down.png";
            break;
        default:
        case BButton.UP: path = "rsrc/textures/button_up.png"; break;
        }
        return new TiledBackground(getResource(path),
                                   5 + dx, 3 + dy, 5 - dx, 3 - dy);
    }

    /**
     * Creates a background to use for a combo box.
     */
    public BBackground createComboBoxBackground ()
    {
        return new TiledBackground(
            getResource("rsrc/textures/button_up.png"), 5, 3, 5, 3);
    }

    /**
     * Creates the background used for text components.
     */
    public BBackground createTextBack ()
    {
//         String path = "rsrc/textures/button_up.png";
//         return new TiledBackground(getResource(path), 5, 3, 5, 3);
        return new TintedBackground(BLACK_TINT, 5, 5, 5, 5);
    }

    /**
     * Creates either the less or more button for a {@link BScrollBar}.
     */
    public BButton createScrollButton (int orientation, boolean less)
    {
        String path = "rsrc/textures/scroll_";
        switch (orientation) {
        case HORIZONTAL:
            path += (less ? "left" : "right");
            break;
        case VERTICAL:
            path += (less ? "up" : "down");
            break;
        }
        path += ".png";
        return new BButton(new BIcon(getResource(path)), "");
    }

    /**
     * Creates the background for rendering the well of a {@link BScrollBar}.
     */
    public BBackground createScrollWell (int orientation)
    {
        return new TiledBackground(
            getResource("rsrc/textures/button_up.png"), 5, 3, 5, 3);
    }

    /**
     * Creates the background for rendering the thumb of a {@link BScrollBar}.
     */
    public BBackground createScrollThumb (int orientation)
    {
        return new TintedBackground(ColorRGBA.white, 0, 0, 0, 0);
    }

    /**
     * Creates the checbox icon used by the {@link BCheckBox}.
     */
    public BIcon createCheckBoxIcon ()
    {
        return new BIcon(getResource("rsrc/textures/checkbox.png"));
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
        configureDefaultLookAndFeel(lnf);
        return lnf;
    }

    /**
     * Returns a basic default look and feel.
     */
    public static void configureDefaultLookAndFeel (BLookAndFeel lnf)
    {
        lnf.setForeground(ColorRGBA.white);
        lnf.setBackground(ColorRGBA.black);
//         URL url = getResource("rsrc/fonts/default.png");
//         lnf.setTextFactory(new JMEBitmapTextFactory(url, 10, 16));
        lnf.setTextFactory(new AWTTextFactory(new Font("Dialog", Font.PLAIN, 16)));
        lnf.setKeyMap(new DefaultKeyMap());
    }

    protected static URL getResource (String path)
    {
        URL url = BLookAndFeel.class.getClassLoader().getResource(path);
        if (url == null) {
            Log.log.warning("Failed to locate resource [path=" + path + "].");
        }
        return url;
    }

    protected BLookAndFeel _parent;
    protected BTextFactory _tfact;
    protected ColorRGBA _foreground, _background;
    protected BKeyMap _keymap;

    protected static final ColorRGBA BLACK_TINT = new ColorRGBA(0, 0, 0, 0.5f);
}
