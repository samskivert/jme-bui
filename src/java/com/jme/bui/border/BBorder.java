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

import com.jme.bui.BComponent;
import com.jme.bui.util.Insets;
import com.jme.renderer.Renderer;

/**
 * Configures a border around a component that may or may not have
 * associated geometric elements. <em>Note:</em> a border must only be
 * used with a single component at a time.
 */
public abstract class BBorder
{
    /** Returns the insets needed by this border. */
    public abstract Insets getInsets ();

    /** Renders this border. */
    public abstract void render (
        Renderer renderer, int x, int y, int width, int height);
}
