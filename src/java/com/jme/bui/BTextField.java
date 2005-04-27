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
import com.jme.bui.event.KeyEvent;
import com.jme.bui.text.BKeyMap;
import com.jme.bui.text.EditCommands;

/**
 * Displays and allows for the editing of a single line of text.
 */
public class BTextField extends BComponent
    implements EditCommands
{
    public BTextField ()
    {
        this("");
    }

    public BTextField (String text)
    {
        _label = new BLabel("");
        setText(text);
    }

    /**
     * Configures this text field with the specified text for display and
     * editing. The cursor will be adjusted if this text is shorter than
     * its previous position.
     */
    public void setText (String text)
    {
        _text = text;
        refigureLabelContents();
    }

    /**
     * Returns the contents of this text field.
     */
    public String getText ()
    {
        return _text;
    }

    // documentation inherited
    public void wasAdded ()
    {
        super.wasAdded();

        // look up our keymap
        _keymap = getLookAndFeel().getKeyMap();

        // now that we have a look and feel we can create our background
        _background = getLookAndFeel().createTextBack();
        attachChild(_background);
        _background.wasAdded();
        attachChild(_label);
        _label.wasAdded();

        refigureLabelContents();
    }

    // documentation inherited
    public void wasRemoved ()
    {
        super.wasRemoved();

        if (_background != null) {
            detachChild(_background);
            _background.wasRemoved();
        }
        detachChild(_label);
        _label.wasRemoved();
    }

    // documentation inherited
    public void layout ()
    {
        super.layout();

        // we must lay out our children by hand as we're not a container
        _background.layout();
        _label.layout();
    }

    // documentation inherited
    public void setBounds (int x, int y, int width, int height)
    {
        super.setBounds(x, y, width, height);
        _background.setBounds(0, 0, width, height);
        int left = _background.getLeftInset();
        int top = _background.getTopInset();
        int right = _background.getRightInset();
        int bottom = _background.getBottomInset();
        int vc = computeVisisbleChars();
        _label.setBounds(left, top, width - (left+right), height - (top+bottom));
        if (computeVisisbleChars() != vc) {
            refigureLabelContents();
        }
    }

    // documentation inherited
    public void dispatchEvent (BEvent event)
    {
        super.dispatchEvent(event);

        if (event instanceof KeyEvent) {
            KeyEvent kev = (KeyEvent)event;
            if (kev.getType() == KeyEvent.KEY_PRESSED) {
                int modifiers = kev.getModifiers(), keyCode = kev.getKeyCode();
                switch (_keymap.lookupMapping(modifiers, keyCode)) {
                case BACKSPACE:
                    if (_cursorPos > 0 && _text.length() > 0) {
                        String after = _text.substring(_cursorPos);
                        setText(_text.substring(0, --_cursorPos) + after);
                    }
                    break;

                case DELETE:
                    if (_cursorPos < _text.length()) {
                        String before = _text.substring(0, _cursorPos);
                        setText(before + _text.substring(_cursorPos + 1));
                    }
                    break;

                case CURSOR_LEFT:
                    _cursorPos = Math.max(0, _cursorPos-1);
                    break;

                case CURSOR_RIGHT:
                    _cursorPos = Math.min(_text.length(), _cursorPos+1);
                    break;

                case START_OF_LINE:
                    _cursorPos = 0;
                    break;

                case END_OF_LINE:
                    _cursorPos = _text.length();
                    break;

                default:
                    // append printable and shifted printable characters
                    // to the text
                    char c = kev.getKeyChar();
                    if ((modifiers & ~KeyEvent.SHIFT_DOWN_MASK) == 0 &&
                        !Character.isISOControl(c)) {
                        String before = _text.substring(0, _cursorPos);
                        String after = _text.substring(_cursorPos);
                        setText(before + kev.getKeyChar() + after);
                        _cursorPos++;
                    }
                    break;
                }
            }
        }
    }

    // documentation inherited
    protected Dimension computePreferredSize ()
    {
        Dimension d = new Dimension(_label.getPreferredSize());
        d.width += _background.getLeftInset();
        d.width += _background.getRightInset();
        d.height += _background.getTopInset();
        d.height += _background.getBottomInset();
        return d;
    }

    /**
     * Determines how much of our text can be visible in the label and
     * configures the label with the appropriate substring.
     */
    protected void refigureLabelContents ()
    {
        if (!isAdded()) {
            _label.setText(_text);
            return;
        }

        int vizChars = computeVisisbleChars();
        _label.setText(_text.substring(0, vizChars));
    }

    /**
     * Returns the number of visible characters in our text field given
     * the width of the label we use to display them.
     */
    protected int computeVisisbleChars ()
    {
        // NOTE: giant hack, Text assumes all fonts are 10 pixels wide,
        // this all needs to be fixed
        return Math.max(Math.min(_label.getWidth() / 10, _text.length()), 0);
    }

    protected BBackground _background;
    protected BLabel _label;
    protected BKeyMap _keymap;

    protected int _cursorPos;
    protected String _text;
}
