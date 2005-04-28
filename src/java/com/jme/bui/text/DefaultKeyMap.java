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
        addMapping(0, KeyInput.KEY_RETURN, EditCommands.ACTION);
        addMapping(0, KeyInput.KEY_NUMPADENTER, EditCommands.ACTION);
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
