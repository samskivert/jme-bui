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

package com.jme.bui.background;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Quad;

import com.jme.bui.util.Dimension;
import com.jme.bui.util.RenderUtil;

/**
 * Displays a partially transparent solid color in the background.
 */
public class TintedBackground extends BBackground
{
    public TintedBackground (int left, int top, int right, int bottom,
                             ColorRGBA color)
    {
        super(left, top, right, bottom);

        _quad = new Quad("quad", 10, 10);
        _quad.setSolidColor(color);
        RenderUtil.makeTransparent(_quad);

        _node.attachChild(_quad);
        _quad.updateRenderState();
    }

    // documentation inherited
    public void setBounds (int x, int y, int width, int height)
    {
        // reshape our scaled sections
        if (_width != width || _height != height) {
            _quad.resize(width, height);
            _quad.setLocalTranslation(
                new Vector3f(width/2, height/2, 0f));
        }
        super.setBounds(x, y, width, height);
        _node.updateGeometricState(0.0f, true);
    }

    // documentation inherited
    public Dimension getPreferredSize ()
    {
        return new Dimension(10, 10);
    }

    protected Quad _quad;
}
