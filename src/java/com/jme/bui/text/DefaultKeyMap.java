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

package com.jme.bui.text;

import com.jme.bui.event.InputEvent;
import com.jme.input.KeyInput;

/**
 * Defines a default key mapping for our text editing components.
 */
public class DefaultKeyMap extends BKeyMap
{
    public DefaultKeyMap ()
    {
        addMapping(0, KeyInput.KEY_BACK, EditCommands.BACKSPACE);
        addMapping(0, KeyInput.KEY_DELETE, EditCommands.DELETE);

        addMapping(0, KeyInput.KEY_LEFT, EditCommands.CURSOR_LEFT);
        addMapping(0, KeyInput.KEY_RIGHT, EditCommands.CURSOR_RIGHT);

        addMapping(0, KeyInput.KEY_HOME, EditCommands.START_OF_LINE);
        addMapping(0, KeyInput.KEY_END, EditCommands.END_OF_LINE);

        // some emacs commands because I love them so
        addMapping(InputEvent.CTRL_DOWN_MASK, KeyInput.KEY_A,
                   EditCommands.START_OF_LINE);
        addMapping(InputEvent.CTRL_DOWN_MASK, KeyInput.KEY_E,
                   EditCommands.END_OF_LINE);
    }
}
