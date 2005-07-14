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

package com.jme.bui.text;

import com.jme.bui.util.Dimension;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.shape.Quad;

/**
 * Contains a "run" of text, which will be rendered to a {@link Quad}.
 * Specializations of this class render text in different ways, for
 * example using JME's internal bitmapped font support or by using the AWT
 * to render the run of text to an image and texturing the quad with that
 * entire image.
 */
public abstract class BText
{
    /** Returns the screen dimensions of this text. */
    public abstract Dimension getSize ();

    /** Renders this text to the display. */
    public abstract void render (Renderer render, int x, int y);
}