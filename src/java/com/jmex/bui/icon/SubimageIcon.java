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

import com.jme.image.Image;
import com.jme.renderer.Renderer;

import com.jmex.bui.util.Rectangle;
import com.jmex.bui.util.RenderUtil;

/**
 * Displays a region of an image as an icon.
 */
public class SubimageIcon extends BIcon
{
    /**
     * Creates an icon that will display the specified region of the supplied
     * image.
     */
    public SubimageIcon (Image image, int x, int y, int width, int height)
    {
        _image = image;
        _region = new Rectangle(x, y, width, height);
    }

    // documentation inherited
    public int getWidth ()
    {
        return _region.width;
    }

    // documentation inherited
    public int getHeight ()
    {
        return _region.height;
    }

    // documentation inherited
    public void render (Renderer renderer, int x, int y)
    {
        super.render(renderer, x, y);

        RenderUtil.blendState.apply();
        RenderUtil.renderImage(
            _image, _region.x, _region.y, _region.width, _region.height, x, y);
    }

    protected Image _image;
    protected Rectangle _region;
}
