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
import com.jme.bui.event.FocusEvent;
import com.jme.bui.event.KeyEvent;
import com.jme.bui.text.BKeyMap;
import com.jme.bui.text.EditCommands;
import com.jme.bui.util.Dimension;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Line;

/**
 * Displays and allows for the editing of a single line of text.
 */
public class BTextField extends BContainer
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
        // confine the cursor to the new text
        if (_cursorPos > _text.length()) {
            setCursorPos(_text.length());
        }
        refigureContents();
    }

    /**
     * Returns the contents of this text field.
     */
    public String getText ()
    {
        return _text;
    }

    /**
     * Configures the preferred width of this text field (the preferred
     * height will be calculated from the font).
     */
    public void setPreferredWidth (int width)
    {
        _prefWidth = width;
    }

    /**
     * Returns a reference to the background used by this text field.
     */
    public BBackground getBackground ()
    {
        return _background;
    }

    // documentation inherited
    public boolean acceptsFocus ()
    {
        return true;
    }

    // documentation inherited
    public void wasAdded ()
    {
        super.wasAdded();

        // look up our keymap
        _keymap = getLookAndFeel().getKeyMap();

        // create our background
        add(_background = getLookAndFeel().createTextBack());

        // add our label over the background
        add(_label);

        // HACK: we need a better way to get our font height
        int fontHeight = 16;

        // create our cursor
        Vector3f[] ends = new Vector3f[] {
            new Vector3f(0, 0, 0), new Vector3f(0, fontHeight, 0) };
        ColorRGBA[] colors = new ColorRGBA[] {
            getLookAndFeel().getForeground(),
            getLookAndFeel().getForeground() };
        _cursor = new Line("cursor", ends, null, colors, null);
        _cursor.setSolidColor(getLookAndFeel().getForeground());
        _node.attachChild(_cursor);
        _cursor.updateRenderState();
        _cursor.setForceCull(true);
    }

    // documentation inherited
    public void wasRemoved ()
    {
        super.wasRemoved();

        if (_background != null) {
            remove(_background);
            _background = null;
        }
        remove(_label);
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
                        String before = _text.substring(0, _cursorPos - 1);
                        String after = _text.substring(_cursorPos);
                        setCursorPos(_cursorPos - 1);
                        setText(before + after);
                    }
                    break;

                case DELETE:
                    if (_cursorPos < _text.length()) {
                        String before = _text.substring(0, _cursorPos);
                        setText(before + _text.substring(_cursorPos + 1));
                    }
                    break;

                case CURSOR_LEFT:
                    setCursorPos(Math.max(0, _cursorPos-1));
                    break;

                case CURSOR_RIGHT:
                    setCursorPos(Math.min(_text.length(), _cursorPos+1));
                    break;

                case START_OF_LINE:
                    setCursorPos(0);
                    break;

                case END_OF_LINE:
                    setCursorPos(_text.length());
                    break;

                case ACTION:
                    ActionEvent aev = new ActionEvent(
                        this, kev.getWhen(), kev.getModifiers(), "");
                    dispatchEvent(aev);
                    break;

                case RELEASE_FOCUS:
                    getWindow().requestFocus(null);
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
                        setCursorPos(_cursorPos + 1);
                    }
                    break;
                }
            }

        } else if (event instanceof FocusEvent) {
            FocusEvent fev = (FocusEvent)event;
            switch (fev.getType()) {
            case FocusEvent.FOCUS_GAINED:
                _cursor.setForceCull(false);
                setCursorPos(_cursorPos);
                break;

            case FocusEvent.FOCUS_LOST:
                _cursor.setForceCull(true);
                break;
            }
        }
    }

    // documentation inherited
    protected void layout ()
    {
        super.layout();

        // our background covers our entire display
        _background.setBounds(0, 0, _width, _height);

        // the label is inset based on the background's insets
        int left = _background.getLeftInset();
        int top = _background.getTopInset();
        int right = _background.getRightInset();
        int bottom = _background.getBottomInset();
        int vc = computeVisisbleChars();
        _label.setBounds(left, top, _width - (left+right),
                         _height - (top+bottom));

        // if our size changed, we may have a different visible set of chars
        if (computeVisisbleChars() != vc) {
            refigureContents();
        }
    }

    // documentation inherited
    protected Dimension computePreferredSize ()
    {
        Dimension d = new Dimension(_label.getPreferredSize());
        if (_prefWidth != -1) {
            d.width = _prefWidth;
        }
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
    protected void refigureContents ()
    {
        if (!isAdded()) {
            _label.setText(getDisplayText());
        } else {
            int vizChars = computeVisisbleChars();
            _label.setText(
                getDisplayText().substring(_offset, _offset+vizChars), false);
        }
    }

    /**
     * This method allows a derived class (specifically {@link
     * BPasswordField}) to display something other than the actual
     * contents of the text field.
     */
    protected String getDisplayText ()
    {
        return _text;
    }

    /**
     * Updates the cursor position, moving the visible representation as
     * well as the insertion and deletion point.
     */
    protected void setCursorPos (int cursorPos)
    {
        int vizChars = computeVisisbleChars();
        _cursorPos = cursorPos;
        if (_cursorPos < _offset) {
            _offset = _cursorPos;
            refigureContents();
        } else if (_cursorPos > (_offset + vizChars)) {
            _offset = (_cursorPos-vizChars);
            refigureContents();
        } else if (_offset > 0 && (_cursorPos < (_offset + vizChars))) {
            _offset = (_cursorPos-vizChars);
            refigureContents();
        }

        int xpos = _label.getX() + 10 * (_cursorPos - _offset);
        int ypos = (_height - 16) / 2;
        _cursor.setLocalTranslation(new Vector3f(xpos, ypos, 0));
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

    protected int _prefWidth = -1;
    protected Line _cursor;
    protected int _cursorPos, _offset;
    protected String _text;
}
