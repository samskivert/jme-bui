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

package com.jmex.bui.util;

import org.lwjgl.opengl.GL11;

import com.jme.image.Image;
import com.jme.scene.Spatial;
import com.jme.scene.state.AlphaState;
import com.jme.system.DisplaySystem;

/**
 * Useful rendering functions.
 */
public class RenderUtil
{
    /** An alpha state that blends the source plus one minus destination. */
    public static AlphaState blendState;

    /**
     * Configures the supplied spatial with transparency in the standard
     * user interface sense which is that transparent pixels show through
     * to the background but non-transparent pixels are not blended with
     * what is behind them.
     */
    public static void makeTransparent (Spatial target)
    {
        target.setRenderState(blendState);
    }

    /**
     * Renders an image at the specified coordinates.
     */
    public static void renderImage (Image image, int x, int y)
    {
        GL11.glRasterPos2i(x, y);
        GL11.glDrawPixels(image.getWidth(), image.getHeight(),
                          IMAGE_FORMATS[image.getType()],
                          GL11.GL_UNSIGNED_BYTE, image.getData());
    }

    /**
     * Renders an image at the specified coordinates and scaled to the
     * specified size.
     */
    public static void renderImage (
        Image image, int tx, int ty, int twidth, int theight)
    {
        renderImage(image, 0, 0, image.getWidth(), image.getHeight(),
                    tx, ty, twidth, theight);
    }

    /**
     * Renders a region of an image at the specified coordinates.
     */
    public static void renderImage (
        Image image, int sx, int sy, int swidth, int sheight, int tx, int ty)
    {
        renderImage(image, sx, sy, swidth, sheight, tx, ty, swidth, sheight);
    }

    /**
     * Renders a region of an image, positioned and scaled as specified.
     */
    public static void renderImage (
        Image image, int sx, int sy, int swidth, int sheight,
        int tx, int ty, int twidth, int theight)
    {
        boolean scale = (twidth != swidth || theight != sheight);
        if (scale) {
            GL11.glPixelZoom(twidth/(float)swidth, theight/(float)sheight);
        }
        boolean skip = (sx > 0 || sy > 0 || swidth != image.getWidth());
        if (skip) {
            GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, image.getWidth());
            GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, sx);
            GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, sy);
        }
        GL11.glRasterPos2i(tx, ty);
        GL11.glDrawPixels(swidth, sheight, IMAGE_FORMATS[image.getType()],
                          GL11.GL_UNSIGNED_BYTE, image.getData());
        if (skip) {
            GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0);
            GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, 0);
            GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, 0);
        }
        if (scale) {
            GL11.glPixelZoom(1f, 1f);
        }
    }

    static {
        blendState = DisplaySystem.getDisplaySystem().getRenderer().
            createAlphaState();
        blendState.setBlendEnabled(true);
        blendState.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        blendState.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        blendState.setEnabled(true);
    }

    protected static int[] IMAGE_FORMATS = {
        GL11.GL_RGBA, GL11.GL_RGB, GL11.GL_RGBA, GL11.GL_RGBA,
        GL11.GL_LUMINANCE_ALPHA, GL11.GL_RGB, GL11.GL_RGBA, GL11.GL_RGBA,
        GL11.GL_RGBA };
}
