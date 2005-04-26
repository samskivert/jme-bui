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

package com.jme.bui;

import java.net.URL;

import com.jme.bui.ScaledBackground;
import com.jme.bui.TiledBackground;
import com.jme.bui.font.BBitmapFont;
import com.jme.bui.font.BFont;
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
        return new ScaledBackground(
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
        lnf.setFont(new BBitmapFont(url, 16, 16));
        return lnf;
    }

    protected BLookAndFeel _parent;
    protected BFont _font;
    protected ColorRGBA _foreground, _background;
}
