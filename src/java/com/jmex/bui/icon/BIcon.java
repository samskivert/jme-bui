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

import java.net.URL;

import java.awt.image.BufferedImage;

import org.lwjgl.opengl.GL11;

import com.jme.image.Image;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

import com.jmex.bui.util.RenderUtil;

/**
 * Provides icon imagery for various components which make use of it.
 */
public abstract class BIcon
{
    /** Returns the width of this icon. */
    public abstract int getWidth ();

    /** Returns the height of this icon. */
    public abstract int getHeight ();

    /** Renders this icon. */
    public void render (Renderer renderer, int x, int y)
    {
        Spatial.applyDefaultStates();
    }
}
