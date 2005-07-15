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

import com.jme.bui.*;
import com.jme.bui.BComboBox;
import com.jme.bui.BScrollPane;
import com.jme.bui.PolledRootNode;
import com.jme.bui.border.LineBorder;
import com.jme.bui.event.ActionEvent;
import com.jme.bui.event.ActionListener;
import com.jme.bui.layout.BorderLayout;
import com.jme.bui.layout.GroupLayout;
import com.jme.bui.layout.TableLayout;

/**
 * Does something extraordinary.
 */
public class LayoutTest extends SimpleGame
{
    protected void simpleInitGame ()
    {
        InputSystem.createInputSystem(properties.getRenderer());
        _root = new PolledRootNode(timer, input);
        rootNode.attachChild(_root);

        // we don't hide the cursor
        InputSystem.getMouseInput().setCursorVisible(true);

        BLookAndFeel lnf = BLookAndFeel.getDefaultLookAndFeel();
        BWindow window = new BDecoratedWindow(lnf, null);
        URL icon = getClass().getClassLoader().
            getResource("rsrc/textures/button_up.png");
//         BLabel label = new BLabel(new ImageIcon(icon));
//         label.setHorizontalAlignment(BLabel.CENTER);
//         label.setText("NORTH");
//         window.add(label, BorderLayout.NORTH);
//         window.add(new BLabel("EAST"), BorderLayout.EAST);
//         window.add(new BComboBox(new String[] {
//             "One", "Two", "Five!", "Three sir.", "Three!" }),
//                    BorderLayout.SOUTH);
//         window.add(new BLabel("WEST"), BorderLayout.WEST);
//         window.add(new BLabel("CENTER"), BorderLayout.CENTER);
        BTabbedPane pane = new BTabbedPane();
        window.add(pane, BorderLayout.CENTER);
        pane.addTab("One", new BButton("One contents"));
        pane.addTab("Two", new BLabel("Two contents"));
        pane.addTab("Three", new BTextArea());
        _root.addWindow(window);
        window.setSize(200, 150);
        window.setLocation(25, 25);

        window = new BWindow(lnf, new BorderLayout(5, 5));
        window.setBorder(new LineBorder(ColorRGBA.black));
        window.add(_text = new BTextArea(), BorderLayout.CENTER);
        window.add(_input = new BTextField(), BorderLayout.SOUTH);
        window.add(new BScrollBar(BScrollBar.VERTICAL, _text.getScrollModel()),
                   BorderLayout.EAST);
        window.add(new BScrollBar(BScrollBar.HORIZONTAL, 0, 25, 50, 100),
                   BorderLayout.NORTH);
        _input.addListener(new ActionListener() {
            public void actionPerformed (ActionEvent event) {
                _text.appendText("You said: ", ColorRGBA.red);
                _text.appendText(_input.getText() + "\n");
                _input.setText("");
            }
        });
        _root.addWindow(window);
        window.setBounds(200, 125, 400, 250);

        GroupLayout glay = GroupLayout.makeVStretch();
        glay.setGap(0);
        window = new BWindow(lnf, glay);
        window.setBorder(new LineBorder(ColorRGBA.black));
        window.add(new BComboBox(new String[] { "one", "two", "three" }));
        window.add(new BMenuItem("Two", "two"));
        window.add(new BMenuItem("Three", "three"));
        window.add(new BMenuItem("Four", "four"));
        window.add(new BMenuItem("Five", "five"));
        window.add(new BMenuItem("Six", "six"));
        window.add(new BMenuItem("Seven", "seven"));
        window.add(new BMenuItem("Eight", "eight"));
        window.add(new BMenuItem("Nine", "nine"));
        _root.addWindow(window);
        window.pack();
        window.setLocation(100, 400);

        window = new BWindow(lnf, new BorderLayout());
        window.setBorder(new LineBorder(ColorRGBA.black));
        BContainer cont =
            new BContainer(GroupLayout.makeHoriz(GroupLayout.LEFT));
        cont.add(new BToggleButton(new ImageIcon(icon), ""));
        BLabel label = new BLabel("Horizontal");
        label.setIcon(new ImageIcon(icon));
        label.setIconTextGap(3);
        cont.add(label);
        label = new BLabel("Vertical");
        label.setIcon(new ImageIcon(icon));
        label.setIconTextGap(1);
        label.setHorizontalAlignment(BLabel.CENTER);
        label.setOrientation(BLabel.VERTICAL);
        cont.add(label);
        cont.add(new BLabel("Four"));
        cont.add(new BLabel("Five"));
        cont.add(new BLabel("Six"));
        cont.add(new BLabel("Seven"));
        cont.add(new BLabel("Eight"));
        cont.add(new BLabel("Nine"));
//         window.add(new BScrollPane(cont), BorderLayout.CENTER);
        window.add(cont, BorderLayout.CENTER);
        _root.addWindow(window);
        window.pack();
        window.setLocation(300, 400);

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
        LayoutTest test = new LayoutTest();
        test.start();
    }

    protected PolledRootNode _root;
    protected BTextArea _text;
    protected BTextField _input;
}
