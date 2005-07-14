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

import com.jme.image.Texture;
import com.jme.renderer.Renderer;

/**
 * Does something extraordinary.
 */
public class BTextureIcon extends BIcon
{
    public BTextureIcon (Texture texture, int width, int height)
    {
        _texture = texture;
    }

    // documentation inherited
    public int getWidth ()
    {
        return _width;
    }

    // documentation inherited
    public int getHeight ()
    {
        return _height;
    }

    // documentation inherited
    public void render (Renderer renderer, int x, int y)
    {
//         RenderUtil.blendState.apply();
//         GL11.glRasterPos2i(x, y);
//         GL11.glDrawPixels(_image.getWidth(), _image.getHeight(),
//                           GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, _image.getData());
    }

    protected Texture _texture;
    protected int _width, _height;
}
