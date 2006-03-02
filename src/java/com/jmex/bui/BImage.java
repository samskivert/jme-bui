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

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.imageio.ImageIO;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

/**
 * Contains a texture, its dimensions and a texture state.
 */
public class BImage extends Quad
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
        throws IOException
    {
        this(ImageIO.read(image), true);
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

        // expand the texture data to a power of two if necessary
        int twidth = _width, theight = _height;
        if (!_supportsNonPowerOfTwo) {
            twidth = nextPOT(twidth);
            theight = nextPOT(theight);
        }

        // render the image into a raster of the proper format
        boolean hasAlpha = TextureManager.hasAlpha(image);
        int type = hasAlpha ?
            BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR;
        BufferedImage tex = new BufferedImage(twidth, theight, type);
        AffineTransform tx = null;
        if (flip) {
            tx = AffineTransform.getScaleInstance(1, -1);
            tx.translate(0, -_height);
        }
        Graphics2D gfx = (Graphics2D) tex.getGraphics();
        gfx.drawImage(image, tx, null);
        gfx.dispose();

        // grab the image memory and stuff it into a direct byte buffer
        ByteBuffer scratch = ByteBuffer.allocateDirect(
            4 * twidth * theight).order(ByteOrder.nativeOrder());
        byte data[] = (byte[])tex.getRaster().getDataElements(
            0, 0, twidth, theight, null);
        scratch.clear();
        scratch.put(data);
        scratch.flip();
        Image textureImage = new Image();
        textureImage.setType(hasAlpha ? Image.RGBA8888 : Image.RGB888);
        textureImage.setWidth(twidth);
        textureImage.setHeight(theight);
        textureImage.setData(scratch);

        setImage(textureImage);
    }

    /**
     * Creates an image of the specified size, using the supplied JME image
     * data. The image should be a power of two size if OpenGL requires it.
     *
     * @param width the width of the renderable image.
     * @param height the height of the renderable image.
     * @param image the image data.
     */
    public BImage (int width, int height, Image image)
    {
        this(width, height);
        setImage(image);
    }

    /**
     * Creates an image of the specified size, with the supplied texture. The
     * texture is assumed to have an underlying image from which we can obtain
     * the texture width and height (which must be a power of two and may
     * differ from the renderable image width and height supplied to the
     * constructor).
     */
    public BImage (int width, int height, Texture texture)
    {
        this(width, height);
        setTexture(texture);
    }

    /**
     * Creates an image with the supplied texture.
     *
     * @param width the width of the renderable image.
     * @param height the height of the renderable image.
     * @param twidth the width of the texture (this should be a power of two).
     * @param theight the height of the texture (this should be a power of two).
     */
    public BImage (int width, int height,
                   Texture texture, int twidth, int theight)
    {
        this(width, height);
        setTexture(texture, twidth, theight);
    }

    /**
     * Creates an image with no texture yet assigned. The image will be blank
     * until a texture is provided with a subsequent call to {@link
     * #setTexture}.
     */
    public BImage (int width, int height)
    {
        super("name", width, height);
        _width = width;
        _height = height;
        _tstate = DisplaySystem.getDisplaySystem().getRenderer().
            createTextureState();
        setTransparent(true);
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
        if (transparent) {
            setRenderState(blendState);
        } else {
            clearRenderState(RenderState.RS_ALPHA);
        }
        updateRenderState();
    }

    /**
     * Configures the image data to be used by this image.
     *
     * @param width the width of the renderable image (the image data itself
     * should be a power of two if OpenGL requires it).
     * @param height the height of the renderable image (the image data itself
     * should be a power of two if OpenGL requires it).
     */
    public void setImage (Image image)
    {
        Texture texture = new Texture();
        texture.setImage(image);
        setTexture(texture);
    }

    /**
     * Configures the texture to be used for this image. The texture is assumed
     * to have an underlying image from which we can obtain the texture width
     * and height.
     */
    public void setTexture (Texture texture)
    {
        setTexture(texture, texture.getImage().getWidth(),
                   texture.getImage().getHeight());
    }

    /**
     * Configures the texture to be used for this image.
     *
     * @param twidth the width of the texture image data (must be a power of
     * two if OpenGL requires it).
     * @param theight the height of the texture image data (must be a power of
     * two if OpenGL requires it).
     */
    public void setTexture (Texture texture, int twidth, int theight)
    {
        _twidth = twidth;
        _theight = theight;

        _texture = texture;
        _texture.setFilter(Texture.FM_LINEAR);
        _texture.setMipmapState(Texture.MM_NONE);
        _texture.setCorrection(Texture.CM_AFFINE);

        _tstate.setTexture(_texture);
        _tstate.setEnabled(true);
        setRenderState(_tstate);
        updateRenderState();
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
        render(renderer, 0, 0, _width, _height, tx, ty, twidth, theight);
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
        float lx = sx / (float)_twidth;
        float ly = sy / (float)_theight;
        float ux = (sx+swidth) / (float)_twidth;
        float uy = (sy+sheight) / (float)_theight;

        FloatBuffer tcoords = getTextureBuffer();
        tcoords.clear();
        tcoords.put(lx).put(uy);
        tcoords.put(lx).put(ly);
        tcoords.put(ux).put(ly);
        tcoords.put(ux).put(uy);
        tcoords.flip();

        resize(twidth, theight);
        localTranslation.x = tx + twidth/2f;
        localTranslation.y = ty + theight/2f;
        updateGeometricState(0, true);

        draw(renderer);
    }

    /** Rounds the supplied value up to a power of two. */
    protected static int nextPOT (int value)
    {
        return (Integer.bitCount(value) > 1) ?
            (Integer.highestOneBit(value) << 1) : value;
    }

    protected Quad _quad;
    protected Texture _texture;
    protected TextureState _tstate;
    protected int _width, _height;
    protected int _twidth, _theight;

    protected static boolean _supportsNonPowerOfTwo;

    static {
        blendState = DisplaySystem.getDisplaySystem().getRenderer().
            createAlphaState();
        blendState.setBlendEnabled(true);
        blendState.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        blendState.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        blendState.setEnabled(true);

        _supportsNonPowerOfTwo =
            GLContext.getCapabilities().GL_ARB_texture_non_power_of_two;
    }
}
