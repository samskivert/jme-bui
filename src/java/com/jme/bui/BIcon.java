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

import org.lwjgl.opengl.GL11;

import com.jme.image.Image;
import com.jme.renderer.Renderer;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

import com.jme.bui.util.RenderUtil;

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
        this(TextureManager.loadImage(image, true));
    }

    /**
     * Creates an icon from the supplied source image.
     */
    public BIcon (BufferedImage image)
    {
        this(TextureManager.loadImage(image, true));
    }

    /**
     * Creates an icon from the supplied source texture.
     */
    public BIcon (Image image)
    {
        _image = image;
    }

    /**
     * Returns the width of this icon.
     */
    public int getWidth ()
    {
        return _image.getWidth();
    }

    /**
     * Returns the height of this icon.
     */
    public int getHeight ()
    {
        return _image.getHeight();
    }

    /**
     * Renders this icon.
     */
    public void render (Renderer renderer, int x, int y)
    {
        RenderUtil.blendState.apply();
        GL11.glRasterPos2i(x, y);
        GL11.glDrawPixels(_image.getWidth(), _image.getHeight(),
                          GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, _image.getData());
    }

    protected Image _image;
}
