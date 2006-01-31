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
import java.net.URL;
import java.util.logging.Level;

import com.jme.app.SimpleGame;
import com.jme.input.KeyBindingManager;
import com.jme.input.MouseInput;
import com.jme.renderer.ColorRGBA;
import com.jme.util.LoggingSystem;

import com.jmex.bui.*;
import com.jmex.bui.event.ActionEvent;
import com.jmex.bui.event.ActionListener;
import com.jmex.bui.icon.ImageIcon;
import com.jmex.bui.layout.AbsoluteLayout;
import com.jmex.bui.layout.BorderLayout;
import com.jmex.bui.layout.GroupLayout;
import com.jmex.bui.layout.TableLayout;
import com.jmex.bui.util.Dimension;
import com.jmex.bui.util.Point;
import com.jmex.bui.util.Rectangle;

/**
 * Does something extraordinary.
 */
public class LayoutTest extends SimpleGame
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
        URL icon = getClass().getClassLoader().
            getResource("rsrc/textures/scroll_up.png");
//         BLabel label = new BLabel(new ImageIcon(icon));
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
        BButton button = new BButton("One contents");
        pane.addTab("One", button);
        button.setEnabled(false);
        pane.addTab("Two", new BLabel("Two contents"));
        pane.addTab("Three", new BTextArea());
        _root.addWindow(window);
        window.setSize(200, 150);
        window.setLocation(25, 25);

        window = new BWindow(style, new BorderLayout(5, 5));
        window.add(_text = new BTextArea(), BorderLayout.CENTER);
        window.add(_input = new BTextField(), BorderLayout.SOUTH);
        window.add(new BScrollBar(BScrollBar.VERTICAL, _text.getScrollModel()),
                   BorderLayout.EAST);
        window.add(new BScrollBar(BScrollBar.HORIZONTAL, 0, 25, 50, 100),
                   BorderLayout.NORTH);
        _input.addListener(new ActionListener() {
            public void actionPerformed (ActionEvent event) {
                String input = _input.getText();
                if (input != null && !input.equals("")) {
                    _text.appendText("You said: ", ColorRGBA.red);
                    _text.appendText(_input.getText() + "\n");
                    _input.setText("");
                }
            }
        });
        _root.addWindow(window);
        window.setBounds(300, 125, 400, 250);

        window = new BWindow(style, GroupLayout.makeVStretch());

        GroupLayout glay = GroupLayout.makeVStretch();
        glay.setGap(0);
        BContainer cont = new BContainer(glay);
        cont.add(new BComboBox(new String[] { "one", "two", "three" }));
        cont.add(new BButton("Two"));
        cont.add(new BMenuItem("Three", "three"));
        cont.add(new BMenuItem("Four", "four"));
        cont.add(new BMenuItem("Five", "five"));
        cont.add(new BMenuItem("Six", "six"));
        cont.add(new BMenuItem("Seven", "seven"));
        cont.add(new BMenuItem("Eight", "eight"));
        cont.add(new BButton("Nine", "nine"));

        window.add(new BScrollPane(cont));
        _root.addWindow(window);
        Dimension ps = window.getPreferredSize(-1, -1);
        window.setBounds(100, 300, ps.width, 2*ps.height/3);

        window = new BWindow(style, new BorderLayout());
        cont = new BContainer(GroupLayout.makeHoriz(GroupLayout.LEFT));
        cont.add(new BToggleButton(new ImageIcon(icon), ""));
        BLabel label = new BLabel("Horizontal");
        label.setIcon(new ImageIcon(icon));
        label.setIconTextGap(3);
        cont.add(label);
        label = new BLabel("Vertical");
        label.setIcon(new ImageIcon(icon));
        label.setIconTextGap(1);
        label.setOrientation(BLabel.VERTICAL);
        cont.add(label);
        cont.add(new BCheckBox("Four"));
        cont.add(new BLabel("Five"));
        cont.add(new BLabel("Six"));
        cont.add(new BLabel("Seven"));
        cont.add(new BLabel("Eight"));
        cont.add(new BLabel("Nine"));
        window.add(cont, BorderLayout.CENTER);
        _root.addWindow(window);
        window.pack();
        window.setLocation(300, 400);

        window = new BWindow(style, new AbsoluteLayout());
        window.add(new BLabel("+0+0"), new Point(0, 0));
        window.add(new BLabel("+10+35"), new Point(10, 35));
        window.add(new BButton("200x25+50+75"), new Rectangle(50, 75, 200, 25));
        _root.addWindow(window);
        window.pack();
        window.setLocation(300, 450);

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
