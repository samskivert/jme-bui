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

package com.jme.bui.background;

import org.lwjgl.opengl.GL11;

import com.jme.image.Image;
import com.jme.renderer.Renderer;

import com.jme.bui.util.Dimension;

/**
 * Displays a scaled texture as a background image.
 */
public class ScaledBackground extends BBackground
{
    /**
     * Creates a scaled background from the specified source image data.
     */
    public ScaledBackground (
        Image image, int left, int top, int right, int bottom)
    {
        super(left, top, right, bottom);
        _image = image;
    }

    /**
     * Returns the "natural" size of our background image.
     */
    public Dimension getNaturalSize ()
    {
        return new Dimension(_image.getWidth(), _image.getHeight());
    }

    // documentation inherited
    public void render (Renderer renderer, int x, int y, int width, int height)
    {
        drawImage(0, 0, _image.getWidth(), _image.getHeight(),
                  x, y, width, height);
    }

    protected void drawImage (int sx, int sy, int swidth, int sheight,
                              int tx, int ty, int twidth, int theight)
    {
        GL11.glPixelZoom(twidth/(float)swidth, theight/(float)sheight);
        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, _image.getWidth());
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, sx);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, sy);
        GL11.glRasterPos2i(tx, ty);
        GL11.glDrawPixels(swidth, sheight, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
                          _image.getData());
        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, 0);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, 0);
        GL11.glPixelZoom(1f, 1f);
    }

    protected Image _image;
}
