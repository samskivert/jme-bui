//
// $Id$
//
// BUI - a user interface library for the JME 3D engine
// Copyright (C) 2005, Michael Bayne, All Rights Reserved
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// Redistributions of source code must retain the above copyright notice, this
// list of conditions and the following disclaimer.
//
// Redistributions in binary form must reproduce the above copyright notice,
// this list of conditions and the following disclaimer in the documentation
// and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package com.jme.bui.tests;

import java.util.logging.Level;

import com.jme.app.SimpleGame;
import com.jme.input.InputSystem;
import com.jme.util.LoggingSystem;

import com.jme.bui.BLabel;
import com.jme.bui.BLookAndFeel;
import com.jme.bui.BWindow;
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
        _dispatcher = new InputDispatcher(timer);

        // we don't hide the cursor
        InputSystem.getMouseInput().setCursorVisible(true);

        BLookAndFeel lnf = BLookAndFeel.getDefaultLookAndFeel();
        BWindow window = new BWindow(lnf, new BorderLayout());
        window.addChild(new BLabel("NORTH"), BorderLayout.NORTH);
        window.addChild(new BLabel("EAST"), BorderLayout.EAST);
        window.addChild(new BLabel("SOUTH"), BorderLayout.SOUTH);
        window.addChild(new BLabel("WEST"), BorderLayout.WEST);
        window.addChild(new BLabel("CENTER"), BorderLayout.CENTER);
        window.setBounds(25, 25, 300, 48);
        window.layout();
        rootNode.attachChild(window);
        _dispatcher.addWindow(window);
    }

    protected void simpleUpdate ()
    {
        _dispatcher.update();
    }

    public static void main (String[] args)
    {
        LoggingSystem.getLogger().setLevel(Level.OFF);
        LayoutTest test = new LayoutTest();
        test.start();
    }

    protected InputDispatcher _dispatcher;
}
