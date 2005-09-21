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

package com.jmex.bui.border;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import com.jmex.bui.util.Insets;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;

/**
 * Defines a border that displays a single line around the bordered
 * component in a specified color.
 */
public class LineBorder extends BBorder
{
    public LineBorder (ColorRGBA color)
    {
        _color = color;
    }

    // documentation inherited
    public Insets getInsets ()
    {
        return ONE_PIXEL_INSETS;
    }

    // documentation inherited
    public void render (Renderer renderer, int x, int y, int width, int height)
    {
        super.render(renderer, x, y, width, height);

        GL11.glColor4f(_color.r, _color.g, _color.b, _color.a);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x + width - 1, y);
        GL11.glVertex2f(x + width, y + height - 1);
        GL11.glVertex2f(x, y + height - 1);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
    }

    protected ColorRGBA _color;

    protected static final Insets ONE_PIXEL_INSETS = new Insets(1, 1, 1, 1);
}