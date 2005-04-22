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
 * Contains information common to all input (keyboard and mouse) events.
 * This includes the state of the modifier keys at the time the event was
 * generated.
 */
public class BInputEvent extends BEvent
{
    /** A modifier mask indicating that the first mouse button was down at
     * the time this event was generated. */
    public static final int BUTTON1_DOWN_MASK = (1 << 0);

    /** A modifier mask indicating that the second mouse button was down
     * at the time this event was generated. */
    public static final int BUTTON2_DOWN_MASK = (1 << 1);

    /** A modifier mask indicating that the third mouse button was down at
     * the time this event was generated. */
    public static final int BUTTON3_DOWN_MASK = (1 << 2);

    /** A modifier mask indicating that the shift key was down at the time
     * this event was generated. */
    public static final int SHIFT_DOWN_MASK = (1 << 3);

    /** A modifier mask indicating that the control key was down at the
     * time this event was generated. */
    public static final int CTRL_DOWN_MASK = (1 << 4);

    /** A modifier mask indicating that the alt key was down at the time
     * this event was generated. */
    public static final int ALT_DOWN_MASK = (1 << 5);

    /** A modifier mask indicating that the meta key (Windows Logo key on
     * some keyboards) was down at the time this event was generated. */
    public static final int META_DOWN_MASK = (1 << 6);

    /**
     * Returns the modifier mask associated with this event.
     */
    public int getModifiers ()
    {
        return _modifiers;
    }

    protected BInputEvent (Object source, long when, int modifiers)
    {
        super(source, when);
        _modifiers = modifiers;
    }

    protected void toString (StringBuffer buf)
    {
        super.toString(buf);
        buf.append(", mods=").append(_modifiers);
    }

    protected int _modifiers;
}
