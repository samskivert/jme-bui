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

package com.jme.bui;

import java.awt.Dimension;

import com.jme.bui.event.BEvent;
import com.jme.bui.event.MouseEvent;

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
        _label = new BLabel("");
        _label.setHorizontalAlignment(BLabel.CENTER);
        setText(text);
    }

    /**
     * Configures the text to be displayed on this button.
     */
    public void setText (String text)
    {
        _label.setText(text);
        relayout();
    }

    // documentation inherited
    public void wasAdded ()
    {
        super.wasAdded();

        // we can now obtain our backgrounds
        if (_backgrounds == null) {
            _backgrounds = new BBackground[3];
            for (int ii = 0; ii < _backgrounds.length; ii++) {
                _backgrounds[ii] = getLookAndFeel().createButtonBack(ii);
                attachChild(_backgrounds[ii]);
                _backgrounds[ii].wasAdded();
                _backgrounds[ii].setForceCull(ii != 0);
            }
        }

        // we need to handle our children by hand as we're not a container
        attachChild(_label);
        _label.wasAdded();
    }

    // documentation inherited
    public void layout ()
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
        _label.setBounds(left, top, width - (left+right), height - (top+bottom));
    }

    // documentation inherited
    public void dispatchEvent (BEvent event)
    {
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

            case MouseEvent.BUTTON_PRESSED:
                if (mev.getButton() == 0) {
                    _pressed = true;
                    _armed = true;
                } else if (mev.getButton() == 1) {
                    // clicking the right mouse button after arming the
                    // button disarms it
                    _armed = false;
                }
                break;

            case MouseEvent.BUTTON_RELEASED:
                if (_armed && _pressed) {
                    // TODO: issue clicked event
                    _label.setHorizontalAlignment(
                        (_label.getHorizontalAlignment()+1)%3);
                    _armed = false;
                }
                _pressed = false;
                break;
            }

            // update our background image if necessary
            int state = getState();
            if (state != ostate) {
                for (int ii = 0; ii < _backgrounds.length; ii++) {
                    _backgrounds[ii].setForceCull(ii != state);
                }
                int dl = (state == DOWN) ? 1 : 0;
                _label.setLocation(_backgrounds[0].getLeftInset() + dl,
                                   _backgrounds[0].getTopInset() - dl);
            }
        }
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
}
