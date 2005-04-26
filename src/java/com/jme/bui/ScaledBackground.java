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

import java.awt.Dimension;
import java.net.URL;

import com.jme.image.Texture;
import com.jme.renderer.ColorRGBA;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

/**
 * Displays a scaled texture as a background image.
 */
public class ScaledBackground extends BBackground
{
    /**
     * Creates a scaled background from the specified source image data.
     */
    public ScaledBackground (
        URL source, int left, int top, int right, int bottom)
    {
        super(left, top, right, bottom);

        // load up the background image as a texture
        Texture texture = TextureManager.loadTexture(
            source, Texture.MM_NEAREST, Texture.FM_NEAREST);
        _twidth = texture.getImage().getWidth();
        _theight = texture.getImage().getHeight();
        _tstate = DisplaySystem.getDisplaySystem().getRenderer().
            createTextureState();
        _tstate.setEnabled(true);
        _tstate.setTexture(texture);

        _quad = new Quad(name + ":quad", _twidth, _theight);
//         _quad.setRenderState(_tstate);
        _quad.setSolidColor(ColorRGBA.blue);
        attachChild(_quad);
        _quad.updateRenderState();
    }

    // documentation inherited
    public void setBounds (int x, int y, int width, int height)
    {
        // reshape our scaled sections
        if (_width != width || _height != height) {
            _quad.resize(width, height);
            _quad.setLocalTranslation(
                new Vector3f(width/2, height/2, 0f));
        }
        super.setBounds(x, y, width, height);
        updateGeometricState(0.0f, true);
    }

    // documentation inherited
    public Dimension getPreferredSize ()
    {
        return new Dimension(_twidth, _theight);
    }

    protected int _twidth, _theight;
    protected TextureState _tstate;
    protected Quad _quad;
}
