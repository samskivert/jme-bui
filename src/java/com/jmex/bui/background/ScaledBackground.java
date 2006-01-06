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

import com.jme.image.Image;
import com.jme.renderer.Renderer;

import com.jmex.bui.util.Dimension;
import com.jmex.bui.util.RenderUtil;

/**
 * Displays a scaled texture as a background image.
 */
public class ScaledBackground extends BBackground
{
    /**
     * Creates a scaled background from the specified source image data.
     */
    public ScaledBackground (Image image)
    {
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
        RenderUtil.renderImage(
            _image, 0, 0, _image.getWidth(), _image.getHeight(),
            x, y, width, height);
    }

    protected Image _image;
}
