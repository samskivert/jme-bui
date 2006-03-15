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

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Box;
import com.jme.util.LoggingSystem;

import com.jmex.bui.*;
import com.jmex.bui.event.ActionEvent;
import com.jmex.bui.event.ActionListener;
import com.jmex.bui.icon.ImageIcon;
import com.jmex.bui.layout.AbsoluteLayout;
import com.jmex.bui.layout.BorderLayout;
import com.jmex.bui.layout.GroupLayout;
import com.jmex.bui.layout.TableLayout;
import com.jmex.bui.text.IntegerDocument;
import com.jmex.bui.text.LengthLimitedDocument;
import com.jmex.bui.util.Dimension;
import com.jmex.bui.util.Point;
import com.jmex.bui.util.Rectangle;

/**
 * Does something extraordinary.
 */
public class LayoutTest extends BaseTest
{
    protected void createWindows (BRootNode root, BStyleSheet style)
    {
        BWindow window;
        BContainer cont;

        BImage icon = null;
        try {
            icon = new BImage(getClass().getClassLoader().
                              getResource("rsrc/textures/scroll_up.png"));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        window = new BDecoratedWindow(style, null);
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
        window.add(pane);
        BButton button = new BButton("One contents");
        pane.addTab("One", button);
        button.setEnabled(false);

        Box box = new Box("box", new Vector3f(), 2, 2, 2);
        Quaternion quat45 = new Quaternion();
        quat45.fromAngleAxis(0.7854f, new Vector3f(1, 1, 1));
        box.setLocalRotation(quat45);

        BGeomView nview = new BGeomView(box);
        pane.addTab("Two", nview);
        pane.addTab("Three", new BTextArea());
        pane.addTab("Four", new BLabel("Four contents"));
        root.addWindow(window);
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
        root.addWindow(window);
        window.setBounds(300, 140, 400, 250);

        window = new BWindow(style, GroupLayout.makeVStretch());
        GroupLayout glay = GroupLayout.makeVStretch();
        glay.setGap(0);
        cont = new BContainer(glay);
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
        root.addWindow(window);
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
        root.addWindow(window);
        window.pack();
        window.setLocation(300, 400);

        window = new BWindow(style, new AbsoluteLayout());
        window.add(new BLabel("+0+0"), new Point(0, 0));
        final BLabel lbl = new BLabel("+10+35");
        window.add(lbl, new Point(10, 35));
        ActionListener list = new ActionListener() {
            public void actionPerformed (ActionEvent event) {
                _count += 9;
                lbl.setText(String.valueOf(_count));
            }
            protected int _count;
        };
        window.add(new BButton("250x25+50+75", list, ""),
                   new Rectangle(50, 75, 250, 25));
        root.addWindow(window);
        window.pack();
        window.setLocation(300, 25);

        window = new BWindow(style, new BorderLayout());
        window.add(new BLabel("This is some styled text.\n" +
                              "@b(bold) @i(italic) @u(underline) @s(strike)\n" +
                              "@#FFCC99(colored)\n" +
                              "@bu#99CCFF(bold, underlined and colored)"),
                   BorderLayout.CENTER);
        root.addWindow(window);
        window.pack();
        window.setLocation(300, 450);
    }

    public static void main (String[] args)
    {
        LoggingSystem.getLogger().setLevel(Level.WARNING);
        LayoutTest test = new LayoutTest();
        test.start();
    }

    protected BTextArea _text;
    protected BTextField _input;
}
