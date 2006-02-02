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
     * Creates a node view with the specified viewport dimensions and the
     * specified {@link Spatial} to be rendered.
     */
    public BGeomView (int width, int height, Spatial node)
    {
        _size = new Dimension(width, height);
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
    protected Dimension computePreferredSize (int whint, int hhint)
    {
        return new Dimension(_size);
    }

    // documentation inherited
    protected void renderComponent (Renderer renderer)
    {
        super.renderComponent(renderer);

        // we need to create our camera the first time through
        if (_camera == null) {
            _camera = renderer.createCamera(_size.width, _size.height);
            int ax = getAbsoluteX(), ay = getAbsoluteY();
            int sw = renderer.getWidth(), sh = renderer.getHeight();
            _camera.setViewPort(ax, ax + _size.width, ay, ay + _size.height);
            System.out.println("Creating camera: +" + ax + "+" + ay +
                               " in " + sw + "x" + sh);
        }

        // now set up our custom camera and render our node
        Camera cam = renderer.getCamera();
        try {
            renderer.setCamera(_camera);
            _camera.update();
            renderer.draw(_geom);
        } finally {
            renderer.setCamera(cam);
            cam.update();
        }
    }

    protected Dimension _size;
    protected BRootNode _root;
    protected Spatial _geom;
    protected Camera _camera;
}
