//
// $Id$
//
// BUI - a user interface library for the JME 3D engine
// Copyright (C) 2005-2006, Michael Bayne, All Rights Reserved
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

package com.jmex.bui;

import java.net.URL;

import org.lwjgl.opengl.GL11;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

/**
 * Contains a texture, its dimensions and a texture state.
 */
public class BImage
{
    /** An alpha state that blends the source plus one minus destination. */
    public static AlphaState blendState;

    /**
     * Configures the supplied spatial with transparency in the standard user
     * interface sense which is that transparent pixels show through to the
     * background but non-transparent pixels are not blended with what is
     * behind them.
     */
    public static void makeTransparent (Spatial target)
    {
        target.setRenderState(blendState);
    }

    /**
     * Creates an image from the supplied source URL.
     */
    public BImage (URL image)
    {
        this(-1, -1);
        setTexture(TextureManager.loadTexture(
                       image, Texture.FM_LINEAR, Texture.MM_NONE,
                       Image.GUESS_FORMAT_NO_S3TC, 1.0f, true));
        _width = _texture.getImage().getWidth();
        _height = _texture.getImage().getHeight();
    }

    /**
     * Creates an image from the supplied source AWT image.
     */
    public BImage (java.awt.Image image)
    {
        this(image, true);
    }

    /**
     * Creates an image from the supplied source AWT image.
     */
    public BImage (java.awt.Image image, boolean flip)
    {
        this(image.getWidth(null), image.getHeight(null));
        setTexture(TextureManager.loadTexture(
                       image, Texture.FM_LINEAR, Texture.MM_NONE, flip));
    }

    /**
     * Creates an image with the supplied texture. The texture is assumed to
     * have an underlying image from which we can obtain our width and height.
     *
     * <p><em>Note:</em> the texture will be placed into
     * <code>AM_REPLACE</code> mode (which is not JME's default) to avoid
     * strange interaction with the current color. If this is not desirable,
     * change it after constructing the icon.
     */
    public BImage (Texture texture)
    {
        this(texture.getImage().getWidth(), texture.getImage().getHeight());
        setTexture(texture);
    }

    /**
     * Creates an image with the supplied texture.
     *
     * <p><em>Note:</em> the texture will be placed into
     * <code>AM_REPLACE</code> mode (which is not JME's default) to avoid
     * strange interaction with the current color. If this is not desirable,
     * change it after constructing the icon.
     */
    public BImage (Texture texture, int width, int height)
    {
        this(width, height);
        setTexture(texture);
    }

    /**
     * Creates an image with no texture yet assigned. The image will be blank
     * until a texture is provided with a subsequent call to {@link
     * #setTexture}.
     */
    public BImage (int width, int height)
    {
        _tstate = DisplaySystem.getDisplaySystem().getRenderer().
            createTextureState();
        _width = width;
        _height = height;
    }

    /**
     * Returns the width of this image.
     */
    public int getWidth ()
    {
        return _width;
    }

    /**
     * Returns the height of this image.
     */
    public int getHeight ()
    {
        return _height;
    }

    /**
     * Configures this image to use transparency or not (true by default).
     */
    public void setTransparent (boolean transparent)
    {
        _transparent = transparent;
    }

    /**
     * Configures the texture to be used for this image.
     */
    public void setTexture (Texture texture)
    {
        _texture = texture;
        _texture.setApply(Texture.AM_REPLACE);
        _texture.setCorrection(Texture.CM_AFFINE);
        _tstate.setTexture(_texture);
        _tstate.setEnabled(true);
    }

    /**
     * Renders this image at the specified coordinates.
     */
    public void render (Renderer renderer, int tx, int ty)
    {
        render(renderer, tx, ty, _width, _height);
    }

    /**
     * Renders this image at the specified coordinates, scaled to the specified
     * size.
     */
    public void render (Renderer renderer, int tx, int ty,
                        int twidth, int theight)
    {
        if (_transparent) {
            blendState.apply();
        }

        _tstate.apply();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0); GL11.glVertex3f(tx, ty, 0);
        GL11.glTexCoord2f(0, 1); GL11.glVertex3f(tx, ty + theight, 0);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex3f(tx + twidth, ty + theight, 0);
        GL11.glTexCoord2f(1, 0); GL11.glVertex3f(tx + twidth, ty, 0);
        GL11.glEnd();
    }

    /**
     * Renders a region of this image at the specified coordinates.
     */
    public void render (Renderer renderer, int sx, int sy,
                        int swidth, int sheight, int tx, int ty)
    {
        render(renderer, sx, sy, swidth, sheight, tx, ty, swidth, sheight);
    }

    /**
     * Renders a region of this image at the specified coordinates, scaled to
     * the specified size.
     */
    public void render (Renderer renderer,
                        int sx, int sy, int swidth, int sheight,
                        int tx, int ty, int twidth, int theight)
    {
        float lx = sx / (float)_width;
        float ly = sy / (float)_height;
        float ux = (sx+swidth) / (float)_width;
        float uy = (sy+sheight) / (float)_height;

        if (_transparent) {
            blendState.apply();
        }

        _tstate.apply();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(lx, ly); GL11.glVertex3f(tx, ty, 0);
        GL11.glTexCoord2f(lx, uy); GL11.glVertex3f(tx, ty+theight, 0);
        GL11.glTexCoord2f(ux, uy); GL11.glVertex3f(tx+twidth, ty+theight, 0);
        GL11.glTexCoord2f(ux, ly); GL11.glVertex3f(tx+twidth, ty, 0);
        GL11.glEnd();
    }

    protected Texture _texture;
    protected TextureState _tstate;
    protected int _width, _height;
    protected boolean _transparent = true;

    static {
        blendState = DisplaySystem.getDisplaySystem().getRenderer().
            createAlphaState();
        blendState.setBlendEnabled(true);
        blendState.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        blendState.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        blendState.setEnabled(true);
    }
}
