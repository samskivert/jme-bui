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

package com.jmex.bui.icon;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.jmex.bui.util.RenderUtil;
import com.jme.image.Texture;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.renderer.Renderer;

/**
 * Displays an icon using image data that is already loaded as a texture.
 * This is mainly useful for images created by rendering to a texture.
 */
public class TextureIcon extends BIcon
{
    /**
     * Creates a texture icon with the supplied texture. <em>Note:</em>
     * the texture will be placed into <code>AM_REPLACE</code> mode (which
     * is not JME's default) to avoid strange interaction with the current
     * color. If this is not desirable, change it after constructing the
     * icon.
     */
    public TextureIcon (Texture texture, int width, int height)
    {
        this(width, height);
        setTexture(texture);
    }

    /**
     * Creates a texture icon with no texture yet assigned. The icon will be
     * blank until a texture is provided with a subsequent call to {@link
     * #setTexture}.
     */
    public TextureIcon (int width, int height)
    {
        _tstate = DisplaySystem.getDisplaySystem().getRenderer().
            createTextureState();
        _width = width;
        _height = height;
    }

    /**
     * Configures the texture to be used for this icon.
     */
    public void setTexture (Texture texture)
    {
        _texture = texture;
        _texture.setApply(Texture.AM_REPLACE);
        _tstate.setTexture(_texture);
        _tstate.setEnabled(true);
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
        super.render(renderer, x, y);

        if (_texture != null) {
            RenderUtil.blendState.apply();
            _tstate.apply();
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0, 0); GL11.glVertex3f(x, y, 0);
            GL11.glTexCoord2f(0, 1); GL11.glVertex3f(x, y + _height, 0);
            GL11.glTexCoord2f(1, 1); GL11.glVertex3f(x + _width, y + _height, 0);
            GL11.glTexCoord2f(1, 0); GL11.glVertex3f(x + _width, y, 0);
            GL11.glEnd();
        }
    }

    protected Texture _texture;
    protected TextureState _tstate;
    protected int _width, _height;
}
