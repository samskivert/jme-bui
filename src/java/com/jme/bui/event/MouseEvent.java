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

package com.jme.bui.event;

/**
 * Encapsulates the information associated with a mouse event.
 */
public class MouseEvent extends InputEvent
{
    /** An event generated when a mouse button is pressed. */
    public static final int MOUSE_PRESSED = 0;

    /** An event generated when a mouse button is released. */
    public static final int MOUSE_RELEASED = 1;

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

    public MouseEvent (Object source, long when, int modifiers, int type,
                       int mx, int my)
    {
        this(source, when, modifiers, type, -1, mx, my);
    }

    public MouseEvent (Object source, long when, int modifiers, int type,
                       int button, int mx, int my)
    {
        super(source, when, modifiers);
        _type = type;
        _button = button;
        _mx = mx;
        _my = my;
    }

    /**
     * Returns the type of this event, one of {@link #MOUSE_PRESSED},
     * {#link MOUSE_RELEASE}, etc.
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

    // documentation inherited
    public void dispatch (ComponentListener listener)
    {
        super.dispatch(listener);
        switch (_type) {
        case MOUSE_PRESSED:
            if (listener instanceof MouseListener) {
                ((MouseListener)listener).mousePressed(this);
            }
            break;

        case MOUSE_RELEASED:
            if (listener instanceof MouseListener) {
                ((MouseListener)listener).mouseReleased(this);
            }
            break;

        case MOUSE_ENTERED:
            if (listener instanceof MouseListener) {
                ((MouseListener)listener).mouseEntered(this);
            }
            break;

        case MOUSE_EXITED:
            if (listener instanceof MouseListener) {
                ((MouseListener)listener).mouseExited(this);
            }
            break;

        case MOUSE_MOVED:
            if (listener instanceof MouseMotionListener) {
                ((MouseMotionListener)listener).mouseMoved(this);
            }
            break;

        case MOUSE_DRAGGED:
            if (listener instanceof MouseMotionListener) {
                ((MouseMotionListener)listener).mouseDragged(this);
            }
            break;
        }
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
