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

package com.jmex.bui.background;

import java.net.URL;

import org.lwjgl.opengl.GL11;

import com.jme.image.Image;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

import com.jmex.bui.util.Dimension;
import com.jmex.bui.util.RenderUtil;

/**
 * Displays a specially tiled background image. The background image is
 * divided into nine sections (three across and three down), the corners
 * are rendered unscaled, the central edges are scaled in one direction
 * and the center section is scaled in both directions.
 *
 * <pre>
 * +----------+----------------+----------+
 * | unscaled |  <- scaled ->  | unscaled |
 * +----------+----------------+----------+
 * |    ^     |       ^        |    ^     |
 * |  scaled  |  <- scaled ->  |  scaled  |
 * |    v     |       v        |    v     |
 * +----------+----------------+----------+
 * | unscaled |  <- scaled ->  | unscaled |
 * +----------+----------------+----------+
 * </pre>
 */
public class TiledBackground extends BBackground
{
    /**
     * Creates a tiled background from the specified source image data.
     */
    public TiledBackground (URL source, int left, int top,
                            int right, int bottom)
    {
        super(left, top, right, bottom);

        // load up the background image as a texture
        _image = TextureManager.loadImage(source, true);
        _twidth = _image.getWidth();
        _theight = _image.getHeight();
    }

    // documentation inherited
    public void render (Renderer renderer, int x, int y, int width, int height)
    {
        super.render(renderer, x, y, width, height);

        // render each of our image sections appropriately
        int wthird = _twidth/3, hthird = _theight/3;
        int wmiddle = _twidth - 2*wthird, hmiddle = _theight - 2*hthird;

        RenderUtil.blendState.apply();

        // draw the corners
        RenderUtil.renderImage(
            _image, 0, 0, wthird, hthird, 0, 0);
        RenderUtil.renderImage(
            _image, _twidth-wthird, 0, wthird, hthird, width-wthird, 0);
        RenderUtil.renderImage(
            _image, 0, _theight-hthird, wthird, hthird, 0, height-hthird);
        RenderUtil.renderImage(
            _image, _twidth-wthird, _theight-hthird, wthird, hthird,
            width-wthird, height-hthird);

        // draw the "gaps"
        int ghmiddle = width-2*wthird, gvmiddle = height-2*hthird;
        RenderUtil.renderImage(
            _image, wthird, 0, wmiddle, hthird, wthird, 0, ghmiddle, hthird);
        RenderUtil.renderImage(
            _image, wthird, _theight-hthird, wmiddle, hthird,
            wthird, height-hthird, ghmiddle, hthird);

        RenderUtil.renderImage(
            _image, 0, hthird, wthird, hmiddle, 0, hthird, wthird, gvmiddle);
        RenderUtil.renderImage(
            _image, _twidth-wthird, hthird, wthird, hmiddle,
            width-wthird, hthird, wthird, gvmiddle);

        // draw the center
        RenderUtil.renderImage(
            _image, wthird, hthird, _twidth-2*wthird, _theight-2*hthird,
            wthird, hthird, width-2*wthird, height-2*hthird);
    }

    protected Image _image;
    protected int _twidth, _theight;
}
