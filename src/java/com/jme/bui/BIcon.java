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

import java.awt.image.BufferedImage;

import com.jme.image.Texture;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

/**
 * Provides icon imagery for various components which make use of it.
 */
public class BIcon
{
    /**
     * Creates an icon from the image referenced by the supplied URL.
     */
    public BIcon (URL image)
    {
        this(TextureManager.loadTexture(image, Texture.MM_LINEAR_LINEAR,
                                        Texture.FM_LINEAR));
    }

    /**
     * Creates an icon from the supplied source image.
     */
    public BIcon (BufferedImage image)
    {
        this(TextureManager.loadTexture(image, Texture.MM_LINEAR_LINEAR,
                                        Texture.FM_LINEAR, false));
    }

    /**
     * Creates an icon from the supplied source texture.
     */
    public BIcon (Texture texture)
    {
        _texture = texture;
        _tstate = DisplaySystem.getDisplaySystem().getRenderer().
            createTextureState();
        _tstate.setEnabled(true);
        _tstate.setTexture(_texture);

        _quad = new Quad("icon", getWidth(), getHeight());
        _quad.setRenderState(_tstate);

        // we want transparent parts of our texture to show through
        RenderUtil.makeTransparent(_quad);
    }

    /**
     * Returns the width of this icon.
     */
    public int getWidth ()
    {
        return _texture.getImage().getWidth();
    }

    /**
     * Returns the height of this icon.
     */
    public int getHeight ()
    {
        return _texture.getImage().getHeight();
    }

    /**
     * Returns the textured quad that is used to display this icon.
     */
    public Quad getQuad ()
    {
        return _quad;
    }

    protected Texture _texture;
    protected TextureState _tstate;
    protected Quad _quad;
}
