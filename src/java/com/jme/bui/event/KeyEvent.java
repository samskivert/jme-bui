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
 * Encapsulates the information associated with a keyboard event.
 */
public class KeyEvent extends InputEvent
{
    /** Indicates that an event represents a key pressing. */
    public static final int KEY_PRESSED = 0;

    /** Indicates that an event represents a key release. */
    public static final int KEY_RELEASED = 1;

    public KeyEvent (Object source, long when, int modifiers,
                     int type, char keyChar, int keyCode)
    {
        super(source, when, modifiers);
        _type = type;
        _keyChar = keyChar;
        _keyCode = keyCode;
    }

    /**
     * Indicates whether this was a {@link #KEY_PRESSED} or {@link
     * #KEY_RELEASED} event.
     */
    public int getType ()
    {
        return _type;
    }

    /**
     * Returns the character associated with the key. <em>Note:</em> this
     * is only valid for {@link #KEY_PRESSED} events, however {@link
     * #getKeyCode} works in all cases.
     */
    public char getKeyChar ()
    {
        return _keyChar;
    }

    /**
     * Returns the numeric identifier associated with the key.
     *
     * @see KeyInput
     */
    public int getKeyCode ()
    {
        return _keyCode;
    }

    protected void toString (StringBuffer buf)
    {
        super.toString(buf);
        buf.append(", type=").append(_type);
        buf.append(", char=").append(_keyChar);
        buf.append(", code=").append(_keyCode);
    }

    protected int _type;
    protected char _keyChar;
    protected int _keyCode;
}