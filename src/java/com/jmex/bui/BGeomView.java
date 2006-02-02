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

import org.lwjgl.opengl.GL11;

import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;

import com.jmex.bui.util.Dimension;

/**
 * Displays a 3D geometry {@link Spatial} inside a normal user interface.
 */
public class BGeomView extends BComponent
{
    /**
     * Creates a node view with the specified {@link Spatial} to be rendered.
     */
    public BGeomView (Spatial node)
    {
        _geom = node;
    }

    /**
     * Called every frame (while we're added to the view hierarchy) by the
     * {@link BRootNode}.
     */
    public void update (float frameTime)
    {
        _geom.updateGeometricState(frameTime, true);
    }

    // documentation inherited
    protected void wasAdded ()
    {
        super.wasAdded();
        _root = getWindow().getRootNode();
        _root.registerGeomView(this);
    }

    // documentation inherited
    protected void wasRemoved ()
    {
        super.wasRemoved();
        _root.unregisterGeomView(this);
        _root = null;
    }

    // documentation inherited
    protected void renderComponent (Renderer renderer)
    {
        super.renderComponent(renderer);

        int ax = getAbsoluteX(), ay = getAbsoluteY();
        float width = renderer.getWidth(), height = renderer.getHeight();
        float left =  ax / width, right = left + _width / width;
        float bottom = ay / height, top = bottom + _height / height;
        Camera cam = renderer.getCamera();

        try {
            // now set up the custom viewport and render our node
            renderer.unsetOrtho();
            cam.setViewPort(left, right, bottom, top);
            cam.update();

            renderer.draw(_geom);

        } finally {
            // restore the viewport
            cam.setViewPort(0, 1, 0, 1);
            cam.update();
            renderer.setOrtho();

            // we need to restore the GL translation as that got wiped out when
            // we left and re-entered ortho mode
            GL11.glTranslatef(ax, ay, 0);
        }
    }

    protected BRootNode _root;
    protected Spatial _geom;
    protected Camera _camera;
}
