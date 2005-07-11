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

package com.jme.bui.text;

import java.net.URL;

import com.jme.image.Texture;
import com.jme.renderer.ColorRGBA;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.Text;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

import com.jme.bui.Log;
import com.jme.bui.util.Dimension;

/**
 * Creates instances of {@link JMEBitmapText} for text rendering.
 */
public class JMEBitmapTextFactory extends BTextFactory
{
    /**
     * Creates a bitmap text factory with the specified font URL and the
     * supplied per-character width and height.
     */
    public JMEBitmapTextFactory (URL font, int width, int height)
    {
        _width = width;
        _height = height;

        // create a texture from our font image
        Texture texture = TextureManager.loadTexture(
            font, Texture.MM_NONE, Texture.FM_NEAREST);
        _tstate = DisplaySystem.getDisplaySystem().getRenderer().
            createTextureState();
        _tstate.setEnabled(true);
        _tstate.setTexture(texture);

        // create an alpha state that we'll use to blend our font over the
        // background
        _astate = DisplaySystem.getDisplaySystem().getRenderer().
            createAlphaState();
        _astate.setBlendEnabled(true);
        _astate.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        _astate.setDstFunction(AlphaState.DB_ONE);
        _astate.setEnabled(true);
    }

    // documentation inherited
    public BText createText (String text, ColorRGBA color)
    {
        // compute the dimensions of this text
        final Dimension dims = new Dimension(text.length() * _width, _height);

        // create a text object to display it
        final Text tgeom = new Text("text", text);
        tgeom.setForceView(true);
        tgeom.setTextureCombineMode(TextureState.REPLACE);
        tgeom.setRenderState(_tstate);
        tgeom.setRenderState(_astate);
        tgeom.setTextColor(color);

        // wrap it all up in the right object
        return new BText() {
            public void setLocation (int x, int y) {
                x -= 4; // TEMP: handle Text offset bug
                tgeom.setLocalTranslation(new Vector3f(x, y, 0));
            }
            public Dimension getSize () {
                return dims;
            }
            public Geometry getGeometry () {
                return tgeom;
            }
        };
    }

    // documentation inherited
    public BText wrapText (
        String text, ColorRGBA color, int maxWidth, int[] remain)
    {
        // determine how many characters we can fit (note: JME currently
        // assumes all text is width 10 so we propagate that hack)
        int maxChars = maxWidth / 10;

        // deal with the easy case
        if (text.length() <= maxChars) {
            remain[0] = 0;
            return createText(text, color);
        }

        // scan backwards from maxChars looking for whitespace
        for (int ii = maxChars; ii >= 0; ii--) {
            if (Character.isWhitespace(text.charAt(ii))) {
                // subtract one to absorb the whitespace that we used to wrap
                remain[0] = (text.length() - ii - 1);
                return createText(text.substring(0, ii), color);
            }
        }

        // ugh, found no whitespace, just hard-wrap at maxChars
        remain[0] = (text.length() - maxChars);
        return createText(text.substring(0, maxChars), color);
    }

    protected int _width, _height;
    protected TextureState _tstate;
    protected AlphaState _astate;
}
