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

import java.awt.Dimension;
import java.net.URL;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

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

        // we want transparent parts of our texture to show through
        RenderUtil.makeTransparent(_node);

        // load up the background image as a texture
        Texture texture = TextureManager.loadTexture(
            source, Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR,
            Image.GUESS_FORMAT_NO_S3TC, 1.0f, true);
        texture.setWrap(Texture.WM_WRAP_S_WRAP_T);
        _twidth = texture.getImage().getWidth();
        _theight = texture.getImage().getHeight();
        _tstate = DisplaySystem.getDisplaySystem().getRenderer().
            createTextureState();
        _tstate.setEnabled(true);
        _tstate.setTexture(texture);

        // create quads for our nine sections
        for (int ii = 0; ii < _sections.length; ii++) {
            _sections[ii] = new Quad("section:" + ii, _twidth/3, _theight/3);
            _sections[ii].setRenderState(_tstate);
            _sections[ii].setTextures(_tcoords[ii]);
            _node.attachChild(_sections[ii]);
        }
    }

    // documentation inherited
    public void setBounds (int x, int y, int width, int height)
    {
        super.setBounds(x, y, width, height);

        // reshape our scaled sections
        _sections[1].resize(width-2*_twidth/3, _theight/3);
        _sections[3].resize(_twidth/3, height - 2*_theight/3);
        _sections[4].resize(width-2*_twidth/3, height - 2*_theight/3);
        _sections[5].resize(_twidth/3, height - 2*_theight/3);
        _sections[7].resize(width-2*_twidth/3, _theight/3);

        _node.updateGeometricState(0.0f, true);
        _node.updateRenderState();
    }

    // documentation inherited
    public Dimension getPreferredSize ()
    {
        return new Dimension(_twidth, _theight);
    }

    // documentation inherited
    protected void layout ()
    {
        // position our sections
        float height = _theight/6f;
        _sections[0].setLocalTranslation(
            new Vector3f(_twidth/6f, height, 0));
        _sections[1].setLocalTranslation(
            new Vector3f(_width/2f, height, 0));
        _sections[2].setLocalTranslation(
            new Vector3f(_width - _twidth/6f, height, 0));

        height = _height/2f;
        _sections[3].setLocalTranslation(
            new Vector3f(_twidth/6f, height, 0));
        _sections[4].setLocalTranslation(
            new Vector3f(_width/2f, height, 0));
        _sections[5].setLocalTranslation(
            new Vector3f(_width - _twidth/6f, height, 0));

        height = _height - _theight/6f;
        _sections[6].setLocalTranslation(
            new Vector3f(_twidth/6f, height, 0));
        _sections[7].setLocalTranslation(
            new Vector3f(_width/2f, height, 0));
        _sections[8].setLocalTranslation(
            new Vector3f(_width - _twidth/6f, height, 0));

        _node.updateGeometricState(0.0f, true);
        _node.updateRenderState();
    }

    protected int _twidth, _theight;
    protected TextureState _tstate;
    protected Quad[] _sections = new Quad[9];

    /** Contains texture coordinates for each of the nine sections into
     * which we divide the background image. */
    protected static Vector2f[][] _tcoords = new Vector2f[9][4];

    /**
     * These map points on a 4x4 grid to texture coordinates. Consider the
     * following grid:
     *
     * <pre>
     * 12 13 14 15
     *  8  9 10 11
     *  4  5  6  7
     *  0  1  2  3
     * </pre>
     *
     * Each of the nine sections is defined by four of the grid
     * coordinates. For example, the upper left section is 8, 12, 13, 15 and
     * we proceed in row major order from there.
     */
    protected static final int[] TCOORDS = {
        4, 0, 1, 5,
        5, 1, 2, 6,
        6, 2, 3, 7,
        8, 4, 5, 9,
        9, 5, 6, 10,
        10, 6, 7, 11,
        12, 8, 9, 13,
        13, 9, 10, 14,
        14, 10, 11, 15
    };

    static {
        Vector2f[] coords = new Vector2f[4*4];
        int idx = 0;
        for (int yy = 0; yy < 4; yy++) {
            for (int xx = 0; xx < 4; xx++) {
                coords[idx++] = new Vector2f(xx/3f, yy/3f);
            }
        }
        for (int ii = 0; ii < TCOORDS.length/4; ii++) {
            _tcoords[ii][0] = coords[TCOORDS[4*ii]];
            _tcoords[ii][1] = coords[TCOORDS[4*ii+1]];
            _tcoords[ii][2] = coords[TCOORDS[4*ii+2]];
            _tcoords[ii][3] = coords[TCOORDS[4*ii+3]];
        }
    }
}
