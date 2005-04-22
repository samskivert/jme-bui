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

package com.jme.bui.event;

/**
 * Encapsulates the information associated with a mouse event.
 */
public class BMouseEvent extends BInputEvent
{
    /** An event generated when a mouse button is pressed. */
    public static final int BUTTON_PRESSED = 0;

    /** An event generated when a mouse button is released. */
    public static final int BUTTON_RELEASED = 1;

    /** An event generated when the mouse enters a component's bounds. */
    public static final int MOUSE_ENTERED = 2;

    /** An event generated when the mouse exits a component's bounds. */
    public static final int MOUSE_EXITED = 3;

    /** An event generated when the mouse is moved. */
    public static final int MOUSE_MOVED = 4;

    /** An event generated when the mouse is dragged. Drag events are
     * dispatched to the component in which a mouse is clicked and held
     * for all movement until all buttons are released. */
    public static final int MOUSE_DRAGGED = 5;

    public BMouseEvent (Object source, long when, int modifiers, int type,
                        int mx, int my)
    {
        this(source, when, modifiers, type, -1, mx, my);
    }

    public BMouseEvent (Object source, long when, int modifiers, int type,
                        int button, int mx, int my)
    {
        super(source, when, modifiers);
        _type = type;
        _button = button;
        _mx = mx;
        _my = my;
    }

    /**
     * Returns the type of this event, one of {@link #BUTTON_PRESSED},
     * {#link BUTTON_RELEASE}, etc.
     */
    public int getType ()
    {
        return _type;
    }

    /**
     * Returns the index of the button pertaining to this event (0, 1 or
     * 2) or -1 if this is not a button related event.
     */
    public int getButton ()
    {
        return _button;
    }

    /**
     * Returns the x coordinates of the mouse at the time this event was
     * generated.
     */
    public int getX ()
    {
        return _mx;
    }

    /**
     * Returns the y coordinates of the mouse at the time this event was
     * generated.
     */
    public int getY ()
    {
        return _my;
    }

    protected void toString (StringBuffer buf)
    {
        super.toString(buf);
        buf.append(", type=").append(_type);
        buf.append(", button=").append(_button);
        buf.append(", x=").append(_mx);
        buf.append(", y=").append(_my);
    }

    protected int _type;
    protected int _button;
    protected int _mx;
    protected int _my;
}
