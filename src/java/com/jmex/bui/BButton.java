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

package com.jmex.bui;

import com.jme.renderer.Renderer;

import com.jmex.bui.background.BBackground;
import com.jmex.bui.event.ActionEvent;
import com.jmex.bui.event.ActionListener;
import com.jmex.bui.event.BEvent;
import com.jmex.bui.event.MouseEvent;
import com.jmex.bui.icon.BIcon;
import com.jmex.bui.util.Dimension;

/**
 * Displays a simple button that can be depressed and which generates an
 * action event when pressed and released.
 */
public class BButton extends BComponent
    implements BConstants
{
    /** A button state constant. Used to select a background. */
    public static final int UP = 0;

    /** A button state constant. Used to select a background. */
    public static final int OVER = 1;

    /** A button state constant. Used to select a background. */
    public static final int DOWN = 2;

    /** A button state constant. Used to select a background. */
    public static final int DISABLED = 3;

    /** The total number of backgrounds available for the button. */
    public static final int BACKGROUND_COUNT = 4;

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
        this(text, null, action);
    }

    /**
     * Creates a button with the specified label and action. The action will be
     * dispatched via an {@link ActionEvent} to the specified {@link
     * ActionListener} when the button is clicked.
     */
    public BButton (String text, ActionListener listener, String action)
    {
        _label = new BLabel("");
        _label.setHorizontalAlignment(BLabel.CENTER);
        _action = action;
        setText(text);
        if (listener != null) {
            addListener(listener);
        }
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
     * Returns the text being displayed on this button.
     */
    public String getText ()
    {
        return _label.getText();
    }

    /**
     * Configures the text to be displayed on this button.
     */
    public void setText (String text)
    {
        _label.setText(text);
    }

    /**
     * Returns the icon being displayed on this button.
     */
    public BIcon getIcon ()
    {
        return _label.getIcon();
    }

    /**
     * Configures the icon to be displayed on this button.
     */
    public void setIcon (BIcon icon)
    {
        _label.setIcon(icon);
    }

    /**
     * Sets the orientation of the label with respect to its icon. If the
     * horizontal (the default) the text is displayed to the right of the
     * icon, if vertical the text is displayed below it.
     */
    public void setOrientation (int orient)
    {
        _label.setOrientation(orient);
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
    public void setBounds (int x, int y, int width, int height)
    {
        super.setBounds(x, y, width, height);

        _label.setBounds(_background.getLeftInset(), _background.getTopInset(),
                         _background.getContentWidth(width),
                         _background.getContentHeight(height));
    }

    // documentation inherited
    public void setEnabled (boolean enabled)
    {
        int ostate = getState();
        super.setEnabled(enabled);
        _label.setEnabled(enabled);
        int state = getState();
        if (state != ostate) {
            stateDidChange();
        }
    }

    // documentation inherited
    public void dispatchEvent (BEvent event)
    {
        super.dispatchEvent(event);

        if (_enabled && event instanceof MouseEvent) {
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
            }
            _background = _backgrounds[getState()];
        }

        // we need to handle our children by hand as we're not a container
        _label.setParent(this);
        _label.wasAdded();
    }

    /** Used by the {@link BToggleButton} to add an additional state. */
    protected int getBackgroundCount ()
    {
        return BACKGROUND_COUNT;
    }

    // documentation inherited
    protected void layout ()
    {
        super.layout();

        // we need to lay out our children by hand as we're not a container
        _label.layout();
    }

    // documentation inherited
    protected void renderComponent (Renderer renderer)
    {
        super.renderComponent(renderer);
        _label.render(renderer);
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
        if (!_enabled) {
            return DISABLED;
        } else if (_armed && _pressed) {
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
        _background = _backgrounds[state];
        _label.setLocation(_background.getLeftInset(),
                           _background.getTopInset());
    }

    // documentation inherited
    protected Dimension computePreferredSize ()
    {
        return new Dimension(_label.getPreferredSize());
    }

    protected BLabel _label;
    protected BBackground[] _backgrounds;
    protected boolean _hover, _armed, _pressed;
    protected String _action;
}
