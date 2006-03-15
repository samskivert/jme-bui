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

package com.jmex.bui.tests;

import java.util.logging.Level;

import com.jme.util.LoggingSystem;

import com.jmex.bui.BRootNode;
import com.jmex.bui.BStyleSheet;
import com.jmex.bui.BWindow;
import com.jmex.bui.layout.BorderLayout;
import com.jmex.bui.text.HTMLView;

/**
 * Tests our HTML view.
 */
public class HTMLTest extends BaseTest
{
    protected void createWindows (BRootNode root, BStyleSheet style)
    {
        BWindow window = new BWindow(style, new BorderLayout(5, 5));
        String html = "<html><body>" +
            "<font face=\"Serif\">This is some test <b>HTML!</b></font>"+
            "<p><table border=1 width=\"100%\">" +
            "<tr><td>We</td><td>even</td><td>support</td></tr>" +
            "<tr><td colspan=3 align=center><i>tables!</i></td></tr></table>" +
            "</body></html>";
        window.add(new HTMLView("", html), BorderLayout.CENTER);
        root.addWindow(window);
        window.setBounds(100, 100, 300, 300);
        // window.pack(-1, 500);
        // window.pack(500, -1);
        // window.pack();
        window.center();
    }

    public static void main (String[] args)
    {
        LoggingSystem.getLogger().setLevel(Level.WARNING);
        HTMLTest test = new HTMLTest();
        test.start();
    }
}
