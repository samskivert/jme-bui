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

import org.lwjgl.opengl.GL11;

import com.jme.image.Texture;
import com.jme.scene.Spatial;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.renderer.Renderer;

/**
 * Displays an icon using image data that is already loaded as a texture.
 * This is mainly useful for images created by rendering to a texture.
 */
public class TextureIcon extends BIcon
{
    public TextureIcon (Texture texture, int width, int height)
    {
        _texture = texture;
        _tstate = DisplaySystem.getDisplaySystem().getRenderer().
            createTextureState();
        _tstate.setTexture(_texture);
        _tstate.setEnabled(true);
        _width = width;
        _height = height;
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
        _tstate.apply();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0); GL11.glVertex3f(x, y, 0);
        GL11.glTexCoord2f(0, 1); GL11.glVertex3f(x, y + _height, 0);
        GL11.glTexCoord2f(1, 1); GL11.glVertex3f(x + _width, y + _height, 0);
        GL11.glTexCoord2f(1, 0); GL11.glVertex3f(x + _width, y, 0);
        GL11.glEnd();
    }

    protected Texture _texture;
    protected TextureState _tstate;
    protected int _width, _height;
}
