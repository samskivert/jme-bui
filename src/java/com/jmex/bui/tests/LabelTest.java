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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import com.jme.app.SimpleGame;
import com.jme.input.KeyBindingManager;
import com.jme.input.MouseInput;
import com.jme.renderer.ColorRGBA;
import com.jme.util.LoggingSystem;

import com.jmex.bui.*;
import com.jmex.bui.layout.GroupLayout;
import com.jmex.bui.icon.ImageIcon;

/**
 * Does something extraordinary.
 */
public class LabelTest extends SimpleGame
    implements BConstants
{
    protected void simpleInitGame ()
    {
        _root = new PolledRootNode(timer, input);
        rootNode.attachChild(_root);

        // we don't hide the cursor
        MouseInput.get().setCursorVisible(true);

        // load up the default BUI stylesheet
        BStyleSheet style = null;
        try {
            InputStream stin = getClass().getClassLoader().
                getResourceAsStream("rsrc/style.bss");
            style = new BStyleSheet(new InputStreamReader(stin),
                                    new BStyleSheet.DefaultResourceProvider());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(-1);
        }

        BWindow window = new BDecoratedWindow(style, null);
        window.setLayoutManager(GroupLayout.makeVStretch());

        ImageIcon icon = new ImageIcon(
            getClass().getClassLoader().getResource(
                "rsrc/textures/scroll_right.png"));
        String[] aligns = { "left", "center", "right" };
        int[] orients = { HORIZONTAL, VERTICAL, OVERLAPPING };

        for (int yy = 0; yy < 3; yy++) {
            BContainer cont = new BContainer(GroupLayout.makeHStretch());
            window.add(cont);
            for (int xx = 0; xx < 3; xx++) {
                BLabel label = new BLabel("This is a lovely label " +
                                          aligns[xx] + "/" + orients[yy] + ".",
                                          aligns[xx]);
                label.setIcon(icon);
                label.setOrientation(orients[yy]);
                cont.add(label);
            }
        }

        _root.addWindow(window);
        window.setSize(400, 400);
        window.setLocation(25, 25);

        // these just get in the way
        KeyBindingManager.getKeyBindingManager().remove("toggle_pause");
        KeyBindingManager.getKeyBindingManager().remove("toggle_wire");
        KeyBindingManager.getKeyBindingManager().remove("toggle_lights");
        KeyBindingManager.getKeyBindingManager().remove("toggle_bounds");
        KeyBindingManager.getKeyBindingManager().remove("camera_out");

        lightState.setEnabled(false);

        display.getRenderer().setBackgroundColor(ColorRGBA.gray);
    }

    protected void simpleUpdate ()
    {
    }

    public static void main (String[] args)
    {
        LoggingSystem.getLogger().setLevel(Level.OFF);
        LabelTest test = new LabelTest();
        test.start();
    }

    protected PolledRootNode _root;
    protected BTextArea _text;
    protected BTextField _input;
}
