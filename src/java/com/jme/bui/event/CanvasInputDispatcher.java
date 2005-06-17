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

import java.awt.Canvas;

import com.jme.scene.Node;

import com.jme.bui.BComponent;
import com.jme.bui.Log;

/**
 * Bridges between the AWT and the BUI input event system when we are
 * being used in an AWT canvas.
 */
public class CanvasInputDispatcher extends InputDispatcher
    implements java.awt.event.MouseListener, java.awt.event.MouseMotionListener,
               java.awt.event.MouseWheelListener
{
    public CanvasInputDispatcher (Node rootNode, Canvas canvas)
    {
        super(rootNode);
        _canvas = canvas;

        // we want to hear about mouse movement and clicking
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
        canvas.addMouseWheelListener(this);
    }

    // documentation inherited from interface MouseListener
    public void mouseClicked (java.awt.event.MouseEvent e) {
        // N/A
    }

    // documentation inherited from interface MouseListener
    public void mouseEntered (java.awt.event.MouseEvent e) {
        // N/A
    }

    // documentation inherited from interface MouseListener
    public void mouseExited (java.awt.event.MouseEvent e) {
        // N/A
    }

    // documentation inherited from interface MouseListener
    public void mousePressed (java.awt.event.MouseEvent e)
    {
        updateState(e);

        BComponent tcomponent = getTargetComponent();
        if (tcomponent != null) {
            _ccomponent = tcomponent;
            tcomponent.dispatchEvent(
                new MouseEvent(this, e.getWhen(), _modifiers,
                               MouseEvent.MOUSE_PRESSED,
                               convertButton(e), _mouseX, _mouseY));
        }
    }

    // documentation inherited from interface MouseListener
    public void mouseReleased (java.awt.event.MouseEvent e)
    {
        updateState(e);

        BComponent tcomponent = getTargetComponent();
        if (tcomponent != null) {
            tcomponent.dispatchEvent(
                new MouseEvent(this, e.getWhen(), _modifiers,
                               MouseEvent.MOUSE_RELEASED,
                               convertButton(e), _mouseX, _mouseY));
        }

        _ccomponent = null;
    }

    // documentation inherited from interface MouseMotionListener
    public void mouseDragged (java.awt.event.MouseEvent e)
    {
        mouseMoved(e);
    }

    // documentation inherited from interface MouseMotionListener
    public void mouseMoved (java.awt.event.MouseEvent e)
    {
        boolean mouseMoved = updateState(e);

        // if the mouse has moved, generate a moved or dragged event
        if (mouseMoved) {
            BComponent tcomponent = getTargetComponent();
            if (tcomponent != null) {
                int type = (tcomponent == _ccomponent) ?
                    MouseEvent.MOUSE_DRAGGED : MouseEvent.MOUSE_MOVED;
                tcomponent.dispatchEvent(
                    new MouseEvent(this, e.getWhen(), _modifiers,
                                   type, _mouseX, _mouseY));
            }
        }
    }

    // documentation inherited from interface MouseWheelListener
    public void mouseWheelMoved (java.awt.event.MouseWheelEvent e)
    {
        updateState(e);

        BComponent tcomponent = getTargetComponent();
        if (tcomponent != null) {
            tcomponent.dispatchEvent(
                new MouseEvent(this, e.getWhen(), _modifiers,
                               MouseEvent.MOUSE_WHEELED,
                               convertButton(e), _mouseX, _mouseY,
                               e.getWheelRotation()));
        }
    }

    protected boolean updateState (java.awt.event.MouseEvent e)
    {
        // update our modifiers
        _modifiers = convertModifiers(e.getModifiers());

        // determine whether the mouse moved
        int mx = e.getX(), my = _canvas.getHeight() - e.getY();
        if (_mouseX != mx || _mouseY != my) {
            _mouseX = mx;
            _mouseY = my;
            computeHoverComponent(mx, my);
            return true;
        }

        return false;
    }

    protected BComponent getTargetComponent ()
    {
        // mouse press and mouse motion events do not necessarily go to
        // the component under the mouse. when the mouse is clicked down
        // on a component (any button), it becomes the "clicked"
        // component, the target for all subsequent click and motion
        // events (which become drag events) until all buttons are
        // released
        if (_ccomponent != null) {
            return _ccomponent;
        }
        // if there's no clicked component, use the hover component
        if (_hcomponent != null) {
            return _hcomponent;
        }
        // if there's no hover component, use the default event target
        return _dcomponent;
    }

    protected int convertModifiers (int modifiers)
    {
        // TODO
        return modifiers;
    }

    protected int convertButton (java.awt.event.MouseEvent e)
    {
        // OpenGL and the AWT disagree about mouse button numbering
        switch (e.getButton()) {
        case 2: return 3;
        case 3: return 2;
        default: return e.getButton();
        }
    }

    protected Canvas _canvas;
}
