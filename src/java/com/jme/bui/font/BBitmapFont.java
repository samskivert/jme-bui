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

package com.jme.bui.font;

import java.net.URL;
import java.util.HashMap;

import com.jme.image.Texture;
import com.jme.math.Vector2f;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

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
            int ccol = ccode % (int)_cols, crow = ccode / (int)_cols;
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
    public float getWidth (char c)
    {
        // we only support fixed width fonts
        return _width;
    }

    // documentation inherited
    public float getHeight ()
    {
        return _height;
    }

    protected TextureState _tstate;
    protected int _width, _height;
    protected float _rows, _cols;
    protected HashMap _tcoords = new HashMap();

    /** Skip the first 32 non-printable characters. */
    protected static final int ASCII_OFFSET = 32;
}
