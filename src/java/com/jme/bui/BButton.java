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

package com.jme.bui;

import com.jme.bui.background.BBackground;
import com.jme.bui.event.ActionEvent;
import com.jme.bui.event.BEvent;
import com.jme.bui.event.MouseEvent;
import com.jme.bui.util.Dimension;

/**
 * Displays a simple button that can be depressed and which generates an
 * action event when pressed and released.
 */
public class BButton extends BComponent
{
    /** A button state constant. Used to select a background. */
    public static final int UP = 0;

    /** A button state constant. Used to select a background. */
    public static final int OVER = 1;

    /** A button state constant. Used to select a background. */
    public static final int DOWN = 2;

    /**
     * Creates a button with the specified textual label.
     */
    public BButton (String text)
    {
        this(text, "");
    }

    /**
     * Creates a button with the specified label and action. The action
     * will be dispatched via an {@link ActionEvent} when the button is
     * clicked.
     */
    public BButton (String text, String action)
    {
        _label = new BLabel("");
        _label.setHorizontalAlignment(BLabel.CENTER);
        _action = action;
        setText(text);
    }

    /**
     * Creates a button with the specified icon and action. The action
     * will be dispatched via an {@link ActionEvent} when the button is
     * clicked.
     */
    public BButton (BIcon icon, String action)
    {
        _label = new BLabel(icon);
        _label.setHorizontalAlignment(BLabel.CENTER);
        _action = action;
        invalidate();
    }

    /**
     * Configures the text to be displayed on this button.
     */
    public void setText (String text)
    {
        _label.setText(text);
    }

    /**
     * Configures the icon to be displayed on this button.
     */
    public void setIcon (BIcon icon)
    {
        _label.setIcon(icon);
    }

    /**
     * Configures the action to be generated when this button is clicked.
     */
    public void setAction (String action)
    {
        _action = action;
    }

    /**
     * Returns the action generated when this button is clicked.
     */
    public String getAction ()
    {
        return _action;
    }

    /**
     * Configures the horizontal alignment of this button's text.
     */
    public void setHorizontalAlignment (int align)
    {
        _label.setHorizontalAlignment(align);
    }

    /**
     * Returns the current horizontal alignment of this button's text.
     */
    public int getHorizontalAlignment ()
    {
        return _label.getHorizontalAlignment();
    }

    /**
     * Configures the vertical alignment of this button's text.
     */
    public void setVerticalAlignment (int align)
    {
        _label.setVerticalAlignment(align);
    }

    /**
     * Returns the current vertical alignment of this button's text.
     */
    public int getVerticalAlignment ()
    {
        return _label.getVerticalAlignment();
    }

    // documentation inherited
    protected void wasAdded ()
    {
        super.wasAdded();

        // we can now obtain our backgrounds
        if (_backgrounds == null) {
            int state = getState();
            _backgrounds = new BBackground[getBackgroundCount()];
            for (int ii = 0; ii < _backgrounds.length; ii++) {
                _backgrounds[ii] = getLookAndFeel().createButtonBack(ii);
                _node.attachChild(_backgrounds[ii].getNode());
                _backgrounds[ii].wasAdded();
                _backgrounds[ii].getNode().setForceCull(ii != state);
            }
        }

        // we need to handle our children by hand as we're not a container
        _node.attachChild(_label.getNode());
        _label.setParent(this);
        _label.wasAdded();
    }

    /** Used by the {@link BToggleButton} to add an additional state. */
    protected int getBackgroundCount ()
    {
        return 3;
    }

    // documentation inherited
    protected void layout ()
    {
        super.layout();

        // we need to lay out our children by hand as we're not a container
        _label.layout();
        for (int ii = 0; ii < _backgrounds.length; ii++) {
            _backgrounds[ii].layout();
        }
    }

    // documentation inherited
    public void setBounds (int x, int y, int width, int height)
    {
        super.setBounds(x, y, width, height);
        for (int ii = 0; ii < _backgrounds.length; ii++) {
            _backgrounds[ii].setBounds(0, 0, width, height);
        }
        int left = _backgrounds[0].getLeftInset();
        int top = _backgrounds[0].getTopInset();
        int right = _backgrounds[0].getRightInset();
        int bottom = _backgrounds[0].getBottomInset();
        _label.setBounds(left, top, width - (left+right),
                         height - (top+bottom));
    }

    // documentation inherited
    public void dispatchEvent (BEvent event)
    {
        super.dispatchEvent(event);

        if (event instanceof MouseEvent) {
            int ostate = getState();
            MouseEvent mev = (MouseEvent)event;
            switch (mev.getType()) {
            case MouseEvent.MOUSE_ENTERED:
                _hover = true;
                _armed = _pressed;
                break;

            case MouseEvent.MOUSE_EXITED:
                _hover = false;
                _armed = false;
                break;

            case MouseEvent.MOUSE_PRESSED:
                if (mev.getButton() == 0) {
                    _pressed = true;
                    _armed = true;
                } else if (mev.getButton() == 1) {
                    // clicking the right mouse button after arming the
                    // button disarms it
                    _armed = false;
                }
                break;

            case MouseEvent.MOUSE_RELEASED:
                if (_armed && _pressed) {
                    // create and dispatch an action event
                    fireAction(mev.getWhen(), mev.getModifiers());
                    _armed = false;
                }
                _pressed = false;
                break;
            }

            // update our background image if necessary
            int state = getState();
            if (state != ostate) {
                stateDidChange();
            }
        }
    }

    /**
     * Called when the button is "clicked" which may due to the mouse
     * being pressed and released while over the button or due to keyboard
     * manipulation while the button has focus.
     */
    protected void fireAction (long when, int modifiers)
    {
        dispatchEvent(new ActionEvent(this, when, modifiers, _action));
    }

    /**
     * Returns the "state" this button should be in.
     */
    protected int getState ()
    {
        if (_armed && _pressed) {
            return DOWN;
        } else if (_hover) {
            return OVER;
        } else {
            return UP;
        }
    }

    /**
     * Called when the button's state has changed, switches to the
     * appropriate background for that state.
     */
    protected void stateDidChange ()
    {
        if (_backgrounds == null) {
            return;
        }

        int state = getState();
        for (int ii = 0; ii < _backgrounds.length; ii++) {
            _backgrounds[ii].getNode().setForceCull(ii != state);
        }
        _label.setLocation(_backgrounds[state].getLeftInset(),
                           _backgrounds[state].getTopInset());
    }

    // documentation inherited
    protected Dimension computePreferredSize ()
    {
        Dimension d = new Dimension(_label.getPreferredSize());
        d.width += _backgrounds[0].getLeftInset();
        d.width += _backgrounds[0].getRightInset();
        d.height += _backgrounds[0].getTopInset();
        d.height += _backgrounds[0].getBottomInset();
        return d;
    }

    protected BLabel _label;
    protected BBackground[] _backgrounds;
    protected boolean _hover, _armed, _pressed;
    protected String _action;
}
