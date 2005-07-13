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

package com.jme.bui.border;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import com.jme.bui.util.Insets;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Line;

/**
 * Defines a border that displays a single line around the bordered
 * component in a specified color.
 */
public class LineBorder extends BBorder
{
    public LineBorder (ColorRGBA color)
    {
        _color = color;
//         _border = new Line("border", _coords, null, _colors, null);
    }

    // documentation inherited
    public Insets getInsets ()
    {
        return ONE_PIXEL_INSETS;
    }

//     // documentation inherited
//     public void setSize (int x, int y, int width, int height)
//     {
//         configureCoords(x, y, width, height);
//         _border.reconstruct(_coords, null, _colors, null);
//         _border.updateGeometricState(0, true);
//     }

    // documentation inherited
    public void render (Renderer renderer, int x, int y, int width, int height)
    {
//         renderer.draw(_border);

        GL11.glColor4f(_color.r, _color.g, _color.b, _color.a);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x + width, y);
        GL11.glVertex2f(x + width + 1, y + height);
        GL11.glVertex2f(x, y + height);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
    }

    protected void configureCoords (int x, int y, int width, int height)
    {
        // it seems that we have to tell OpenGL to stroke a line one pixel
        // beyond where we actually want it to stop
        _coords[0] = new Vector3f(x, y + height-1, 0);
        _coords[1] = new Vector3f(x + width, y + height-1, 0);

        _coords[2] = new Vector3f(x + width-1, y, 0);
        _coords[3] = new Vector3f(x + width-1, y + height, 0);

        _coords[4] = new Vector3f(x, y, 0);
        _coords[5] = new Vector3f(x + width, y, 0);

        _coords[6] = new Vector3f(x, y, 0);
        _coords[7] = new Vector3f(x, y + height, 0);
        System.err.println("Configured border " + width + "x" + height +
                           "+" + x + "+" + y + ".");
    }

    protected Line _border;
    protected Vector3f[] _coords = new Vector3f[8];
    protected ColorRGBA _color;

    protected static final Vector3f NORMAL = new Vector3f(0, 0, 1);
    protected static final Vector3f[] NORMALS = new Vector3f[] {
        NORMAL, NORMAL, NORMAL, NORMAL, NORMAL
    };

    protected static final Insets ONE_PIXEL_INSETS = new Insets(1, 1, 1, 1);
}
