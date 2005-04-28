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

package com.jme.bui;

import java.awt.Dimension;

import com.jme.bui.layout.BLayoutManager;

/**
 * A window defines the top-level of a component hierarchy. It must be
 * created with a look and feel and layout manager.
 */
public class BWindow extends BContainer
{
    public BWindow (BLookAndFeel lnf, BLayoutManager layout)
    {
        setLookAndFeel(lnf);
        setLayoutManager(layout);
    }

    /**
     * Sizes this window to its preferred size. This method does not
     * change the window's coordinates.
     */
    public void pack ()
    {
        Dimension ps = getPreferredSize();
        setBounds(_x, _y, ps.width, ps.height);
    }
}
