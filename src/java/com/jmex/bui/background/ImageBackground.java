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

import com.jme.renderer.Renderer;

import com.jmex.bui.BImage;

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

    public ImageBackground (int mode, BImage image)
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
    public void render (Renderer renderer, int x, int y, int width, int height,
        float alpha)
    {
        super.render(renderer, x, y, width, height, alpha);

        switch (_mode/3) {
        case CENTER:
            renderCentered(renderer, x, y, width, height, alpha);
            break;

        case SCALE:
            renderScaled(renderer, x, y, width, height, alpha);
            break;

        case TILE:
            renderTiled(renderer, x, y, width, height, alpha);
            break;

        case FRAME:
            renderFramed(renderer, x, y, width, height, alpha);
            break;
        }
    }

    // documentation inherited
    public void wasAdded ()
    {
        super.wasAdded();
        _image.reference();
    }

    // documentation inherited
    public void wasRemoved ()
    {
        super.wasRemoved();
        _image.release();
    }

    protected void renderCentered (
        Renderer renderer, int x, int y, int width, int height, float alpha)
    {
        if (_mode == CENTER_X || _mode == CENTER_XY) {
            x += (width-_image.getWidth())/2;
        }
        if (_mode == CENTER_Y || _mode == CENTER_XY) {
            y += (height-_image.getHeight())/2;
        }
        _image.render(renderer, x, y, alpha);
    }

    protected void renderScaled (
        Renderer renderer, int x, int y, int width, int height, float alpha)
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
        _image.render(renderer, x, y, width, height, alpha);
    }

    protected void renderTiled (
        Renderer renderer, int x, int y, int width, int height, float alpha)
    {
        int iwidth = _image.getWidth(), iheight = _image.getHeight();
        if (_mode == TILE_X) {
            renderRow(renderer, x, y, width, Math.min(height, iheight), alpha);

        } else if (_mode == TILE_Y) {
            int up = height / iheight;
            iwidth = Math.min(width, iwidth);
            for (int yy = 0; yy < up; yy++) {
                _image.render(renderer, 0, 0, iwidth, iheight,
                              x, y + yy*iheight, iwidth, iheight, alpha);
            }
            int remain = height % iheight;
            if (remain > 0) {
                _image.render(renderer, 0, 0, iwidth, remain,
                              x, y + up*iheight, iwidth, remain, alpha);
            }

        } else if (_mode == TILE_XY) {
            int up = height / iheight;
            for (int yy = 0; yy < up; yy++) {
                renderRow(renderer, x, y + yy*iheight, width, iheight, alpha);
            }
            int remain = height % iheight;
            if (remain > 0) {
                renderRow(renderer, x, y + up*iheight, width, remain, alpha);
            }
        }
    }

    protected void renderRow (
        Renderer renderer, int x, int y, int width, int iheight, float alpha)
    {
        int iwidth = _image.getWidth();
        int across = width / iwidth;
        for (int xx = 0; xx < across; xx++) {
            _image.render(renderer, 0, 0, iwidth, iheight,
                          x + xx*iwidth, y, iwidth, iheight, alpha);
        }
        int remain = width % iwidth;
        if (remain > 0) {
            _image.render(renderer, 0, 0, remain, iheight,
                          x + across*iwidth, y, remain, iheight, alpha);
        }
    }

    protected void renderFramed (
        Renderer renderer, int x, int y, int width, int height, float alpha)
    {
        // render each of our image sections appropriately
        int twidth = _image.getWidth(), theight = _image.getHeight();
        int wthird = twidth/3, hthird = theight/3;
        int wmiddle = twidth - 2*wthird, hmiddle = theight - 2*hthird;

        // draw the corners
        _image.render(renderer, 0, 0, wthird, hthird, x, y, alpha);
        _image.render(renderer, twidth-wthird, 0, wthird, hthird,
                      x+width-wthird, y, alpha);
        _image.render(renderer, 0, theight-hthird, wthird, hthird,
                      x, y+height-hthird, alpha);
        _image.render(renderer, twidth-wthird, theight-hthird, wthird, hthird,
                      x+width-wthird, y+height-hthird, alpha);

        // draw the "gaps"
        int ghmiddle = width-2*wthird, gvmiddle = height-2*hthird;
        _image.render(renderer, wthird, 0, wmiddle, hthird, x+wthird, y,
                      ghmiddle, hthird, alpha);
        _image.render(renderer, wthird, theight-hthird, wmiddle, hthird,
                      x+wthird, y+height-hthird, ghmiddle, hthird, alpha);

        _image.render(renderer, 0, hthird, wthird, hmiddle, x, y+hthird,
                      wthird, gvmiddle, alpha);
        _image.render(renderer, twidth-wthird, hthird, wthird, hmiddle,
                      x+width-wthird, y+hthird, wthird, gvmiddle, alpha);

        // draw the center
        _image.render(renderer, wthird, hthird, twidth-2*wthird,
                      theight-2*hthird, x+wthird, y+hthird, width-2*wthird,
                      height-2*hthird, alpha);
    }

    protected int _mode;
    protected BImage _image;

    protected static final int CENTER = 0;
    protected static final int SCALE = 1;
    protected static final int TILE = 2;
    protected static final int FRAME = 3;
}
