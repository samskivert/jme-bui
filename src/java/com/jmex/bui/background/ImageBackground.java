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

package com.jmex.bui.background;

import com.jme.image.Image;
import com.jme.renderer.Renderer;

import com.jmex.bui.util.RenderUtil;

/**
 * Supports image backgrounds in a variety of ways. Specifically:
 *
 * <ul>
 * <li> Centering the image either horizontally, vertically or both.
 * <li> Scaling the image either horizontally, vertically or both.
 * <li> Tiling the image either horizontally, vertically or both.
 * <li> Framing the image in a fancy way: the background image is divided into
 * nine sections (three across and three down), the corners are rendered
 * unscaled, the central edges are scaled in one direction and the center
 * section is scaled in both directions.
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
 * </ul>
 */
public class ImageBackground extends BBackground
{
    public static final int CENTER_XY = 0;
    public static final int CENTER_X = 1;
    public static final int CENTER_Y = 2;

    public static final int SCALE_XY = 3;
    public static final int SCALE_X = 4;
    public static final int SCALE_Y = 5;

    public static final int TILE_XY = 6;
    public static final int TILE_X = 7;
    public static final int TILE_Y = 8;

    public static final int FRAME_XY = 9;
    public static final int FRAME_X = 10;
    public static final int FRAME_Y = 11;

    public ImageBackground (int mode, Image image)
    {
        _mode = mode;
        _image = image;
    }

    // documentation inherited
    public int getMinimumWidth ()
    {
        return _image.getWidth();
    }

    /**
     * Returns the minimum height allowed by this background.
     */
    public int getMinimumHeight ()
    {
        return _image.getHeight();
    }

    // documentation inherited
    public void render (Renderer renderer, int x, int y, int width, int height)
    {
        super.render(renderer, x, y, width, height);

        RenderUtil.blendState.apply();

        switch (_mode/3) {
        case CENTER:
            renderCentered(renderer, x, y, width, height);
            break;

        case SCALE:
            renderScaled(renderer, x, y, width, height);
            break;

        case TILE:
            renderTiled(renderer, x, y, width, height);
            break;

        case FRAME:
            renderFramed(renderer, x, y, width, height);
            break;
        }
    }

    protected void renderCentered (
        Renderer renderer, int x, int y, int width, int height)
    {
        if (_mode == CENTER_X || _mode == CENTER_XY) {
            x += (width-_image.getWidth())/2;
        }
        if (_mode == CENTER_Y || _mode == CENTER_XY) {
            y += (height-_image.getHeight())/2;
        }
        RenderUtil.renderImage(
            _image, 0, 0, _image.getWidth(), _image.getHeight(),
            x, y, _image.getWidth(), _image.getHeight());
    }

    protected void renderScaled (
        Renderer renderer, int x, int y, int width, int height)
    {
        switch (_mode) {
        case SCALE_X:
            y = (height-_image.getHeight())/2;
            height = _image.getHeight();
            break;
        case SCALE_Y:
            x = (width-_image.getWidth())/2;
            width = _image.getWidth();
            break;
        }
        RenderUtil.renderImage(
            _image, 0, 0, _image.getWidth(), _image.getHeight(),
            x, y, width, height);
    }

    protected void renderTiled (
        Renderer renderer, int x, int y, int width, int height)
    {
        int iwidth = _image.getWidth(), iheight = _image.getHeight();
        if (_mode == TILE_X) {
            renderRow(renderer, x, y, width, Math.min(height, iheight));

        } else if (_mode == TILE_Y) {
            int up = height / iheight;
            iwidth = Math.min(width, iwidth);
            for (int yy = 0; yy < up; yy++) {
                RenderUtil.renderImage(
                    _image, 0, 0, iwidth, iheight,
                    x, y + yy*iheight, iwidth, iheight);
            }
            int remain = height % iheight;
            if (remain > 0) {
                RenderUtil.renderImage(
                    _image, 0, 0, iwidth, remain,
                    x, y + up*iheight, iwidth, remain);
            }

        } else if (_mode == TILE_XY) {
            int up = height / iheight;
            for (int yy = 0; yy < up; yy++) {
                renderRow(renderer, x, y + yy*iheight, width, iheight);
            }
            int remain = height % iheight;
            if (remain > 0) {
                renderRow(renderer, x, y + up*iheight, width, remain);
            }
        }
    }

    protected void renderRow (
        Renderer renderer, int x, int y, int width, int iheight)
    {
        int iwidth = _image.getWidth();
        int across = width / iwidth;
        for (int xx = 0; xx < across; xx++) {
            RenderUtil.renderImage(_image, 0, 0, iwidth, iheight,
                                   x + xx*iwidth, y, iwidth, iheight);
        }
        int remain = width % iwidth;
        if (remain > 0) {
            RenderUtil.renderImage(_image, 0, 0, remain, iheight,
                                   x + across*iwidth, y, remain, iheight);
        }
    }

    protected void renderFramed (
        Renderer renderer, int x, int y, int width, int height)
    {
        // render each of our image sections appropriately
        int twidth = _image.getWidth(), theight = _image.getHeight();
        int wthird = twidth/3, hthird = theight/3;
        int wmiddle = twidth - 2*wthird, hmiddle = theight - 2*hthird;

        // draw the corners
        RenderUtil.renderImage(
            _image, 0, 0, wthird, hthird, 0, 0);
        RenderUtil.renderImage(
            _image, twidth-wthird, 0, wthird, hthird, width-wthird, 0);
        RenderUtil.renderImage(
            _image, 0, theight-hthird, wthird, hthird, 0, height-hthird);
        RenderUtil.renderImage(
            _image, twidth-wthird, theight-hthird, wthird, hthird,
            width-wthird, height-hthird);

        // draw the "gaps"
        int ghmiddle = width-2*wthird, gvmiddle = height-2*hthird;
        RenderUtil.renderImage(
            _image, wthird, 0, wmiddle, hthird, wthird, 0, ghmiddle, hthird);
        RenderUtil.renderImage(
            _image, wthird, theight-hthird, wmiddle, hthird,
            wthird, height-hthird, ghmiddle, hthird);

        RenderUtil.renderImage(
            _image, 0, hthird, wthird, hmiddle, 0, hthird, wthird, gvmiddle);
        RenderUtil.renderImage(
            _image, twidth-wthird, hthird, wthird, hmiddle,
            width-wthird, hthird, wthird, gvmiddle);

        // draw the center
        RenderUtil.renderImage(
            _image, wthird, hthird, twidth-2*wthird, theight-2*hthird,
            wthird, hthird, width-2*wthird, height-2*hthird);
    }

    protected int _mode;
    protected Image _image;

    protected static final int CENTER = 0;
    protected static final int SCALE = 1;
    protected static final int TILE = 2;
    protected static final int FRAME = 3;
}
