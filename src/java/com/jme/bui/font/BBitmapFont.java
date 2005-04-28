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

package com.jme.bui.font;

import java.net.URL;
import java.util.HashMap;

import com.jme.image.Texture;
import com.jme.math.Vector2f;
import com.jme.scene.Text;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

import com.jme.bui.Log;

/**
 * Provides font information from a 2D grid of character images arranged
 * in a semi-standard format (ie. in a grid, starting from ASCII 32 and
 * going up to ASCII 127 or higher if, say, the ISO 8859-1 character set
 * is to be assumed and used).
 */
public class BBitmapFont extends BFont
{
    /**
     * Creates a bitmap font from the supplied texture data with
     * characters of the specified width and height.
     */
    public BBitmapFont (URL source, int width, int height)
    {
        _width = width;
        _height = height;

        // create a texture from our font image, each character will
        // render using this texture with appropriate texture coordinates
        Texture texture = TextureManager.loadTexture(
            source, Texture.MM_NEAREST, Texture.FM_NEAREST);
        _rows = texture.getImage().getWidth() / width;
        _cols = texture.getImage().getHeight() / height;
        _tstate = DisplaySystem.getDisplaySystem().getRenderer().
            createTextureState();
        _tstate.setEnabled(true);
        _tstate.setTexture(texture);
    }

    // documentation inherited
    public BGlyph createCharacter (char c)
    {
        BGlyph glyph = new BGlyph(c, this);

        // look up (or create and cache) the appropriate texture coordinates
        Character key = Character.valueOf(c);
        Vector2f[] tcoords = (Vector2f[])_tcoords.get(key);
        if (tcoords == null) {
            int ccode = c - ASCII_OFFSET;
            int ccol = ccode % (int)_cols;
            int crow = (int)_rows - (ccode / (int)_cols) - 1;
            float left = ccol / _cols, right = (ccol+1) / _cols;
            float bot = crow / _rows, top = (crow+1) / _rows;
            tcoords = new Vector2f[4];
            tcoords[0] = new Vector2f(left, top);
            tcoords[1] = new Vector2f(left, bot);
            tcoords[2] = new Vector2f(right, bot);
            tcoords[3] = new Vector2f(right, top);
            _tcoords.put(key, tcoords);
        }

        // texture the quad with our character
        glyph.setRenderState(_tstate);
        glyph.setTextures(tcoords);

        return glyph;
    }

    // documentation inherited
    public int getWidth (char c)
    {
        // we only support fixed width fonts
        return _width;
    }

    // documentation inherited
    public int getHeight ()
    {
        return _height;
    }

    // documentation inherited
    public void configure (Text text)
    {
        text.setForceView(true);
        text.setTextureCombineMode(TextureState.REPLACE);
        text.setRenderState(_tstate);
    }

    protected TextureState _tstate;
    protected int _width, _height;
    protected float _rows, _cols;
    protected HashMap _tcoords = new HashMap();

    /** Skip the first 32 non-printable characters. */
    protected static final int ASCII_OFFSET = 32;
}
