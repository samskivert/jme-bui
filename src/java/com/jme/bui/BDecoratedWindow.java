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

import com.jme.bui.layout.BorderLayout;

/**
 * A top-level window with a border, a background and a title bar. Note
 * that a decorated window always uses a {@link BorderLayout} and makes
 * use of the {@link BorderLayout#NORTH} position to display its title bar
 * (if a title was specified).
 */
public class BDecoratedWindow extends BWindow
{
    /**
     * Creates a decorated window using the supplied look and feel.
     *
     * @param title the title of the window or null if no title bar is
     * desired.
     */
    public BDecoratedWindow (BLookAndFeel lnf, String title)
    {
        super(lnf, new BorderLayout(5, 5));

        // set up our background and border from the look and feel
        setBackground(lnf.createWindowBackground());
        setBorder(lnf.createWindowBorder());

        if (title != null) {
            add(new BLabel(title), BorderLayout.NORTH);
        }
    }
}
