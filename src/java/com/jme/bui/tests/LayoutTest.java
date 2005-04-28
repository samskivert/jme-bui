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

package com.jme.bui.tests;

import java.net.URL;
import java.util.logging.Level;

import com.jme.app.SimpleGame;
import com.jme.input.InputSystem;
import com.jme.input.KeyBindingManager;
import com.jme.renderer.ColorRGBA;
import com.jme.util.LoggingSystem;

import com.jme.bui.BButton;
import com.jme.bui.BLabel;
import com.jme.bui.BLookAndFeel;
import com.jme.bui.BTextArea;
import com.jme.bui.BTextField;
import com.jme.bui.BWindow;
import com.jme.bui.event.ActionEvent;
import com.jme.bui.event.ActionListener;
import com.jme.bui.event.InputDispatcher;
import com.jme.bui.layout.BorderLayout;

/**
 * Does something extraordinary.
 */
public class LayoutTest extends SimpleGame
{
    protected void simpleInitGame ()
    {
        InputSystem.createInputSystem(properties.getRenderer());
        _dispatcher = new InputDispatcher(timer, input);

        // we don't hide the cursor
        InputSystem.getMouseInput().setCursorVisible(true);

        BLookAndFeel lnf = BLookAndFeel.getDefaultLookAndFeel();
        BWindow window = new BWindow(lnf, new BorderLayout());
        window.addChild(new BLabel("NORTH"), BorderLayout.NORTH);
        window.addChild(new BLabel("EAST"), BorderLayout.EAST);
        window.addChild(new BLabel("SOUTH"), BorderLayout.SOUTH);
        window.addChild(new BLabel("WEST"), BorderLayout.WEST);
        window.addChild(new BLabel("CENTER"), BorderLayout.CENTER);
        window.pack();
        window.setLocation(25, 25);
        window.layout();
        rootNode.attachChild(window);
        _dispatcher.addWindow(window);

        window = new BWindow(lnf, new BorderLayout(2, 2));
        window.addChild(_text = new BTextArea(), BorderLayout.CENTER);
        window.addChild(_input = new BTextField(), BorderLayout.SOUTH);
        _input.addListener(new ActionListener() {
            public void actionPerformed (ActionEvent event) {
                _text.appendText("You said: ", ColorRGBA.blue);
                _text.appendText(_input.getText() + "\n");
                _input.setText("");
            }
        });
        window.setBounds(100, 100, 300, 150);
        window.layout();
        rootNode.attachChild(window);
        _dispatcher.addWindow(window);

        // these just get in the way
        KeyBindingManager.getKeyBindingManager().remove("toggle_pause");
        KeyBindingManager.getKeyBindingManager().remove("toggle_wire");
        KeyBindingManager.getKeyBindingManager().remove("toggle_lights");
        KeyBindingManager.getKeyBindingManager().remove("toggle_bounds");
        KeyBindingManager.getKeyBindingManager().remove("camera_out");

        lightState.setEnabled(false);
    }

    protected void simpleUpdate ()
    {
        _dispatcher.update(tpf);
    }

    public static void main (String[] args)
    {
        LoggingSystem.getLogger().setLevel(Level.OFF);
        LayoutTest test = new LayoutTest();
        test.start();
    }

    protected InputDispatcher _dispatcher;
    protected BTextArea _text;
    protected BTextField _input;
}
