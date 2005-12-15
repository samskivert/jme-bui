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

import org.lwjgl.opengl.GL11;

import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;

import com.jmex.bui.background.BBackground;
import com.jmex.bui.event.ActionEvent;
import com.jmex.bui.event.BEvent;
import com.jmex.bui.event.FocusEvent;
import com.jmex.bui.event.KeyEvent;
import com.jmex.bui.text.BKeyMap;
import com.jmex.bui.text.BText;
import com.jmex.bui.text.EditCommands;
import com.jmex.bui.util.Dimension;
import com.jmex.bui.util.Insets;

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

        // if we're already part of the hierarchy, recreate our glyps
        if (isAdded()) {
            recreateGlyphs();
        }

        // confine the cursor to the new text
        if (_cursorPos > _text.length()) {
            setCursorPos(_text.length());
        }
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

    // documentation inherited
    public boolean acceptsFocus ()
    {
        return isEnabled();
    }

    // documentation inherited
    public void setEnabled (boolean enabled)
    {
        boolean wasEnabled = isEnabled();
        super.setEnabled(enabled);
        if (isAdded() && wasEnabled != isEnabled()) {
            recreateGlyphs();
        }
    }

    // documentation inherited
    public void wasAdded ()
    {
        super.wasAdded();

        // look up our keymap
        _keymap = getLookAndFeel().getKeyMap();

        // create our background
        _background = getLookAndFeel().createTextBack();

        // create our underlying glyphs
        recreateGlyphs();
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
                _showCursor = true;
                setCursorPos(_cursorPos);
                break;

            case FocusEvent.FOCUS_LOST:
                _showCursor = false;
                break;
            }
        }
    }

    // documentation inherited
    protected void layout ()
    {
        super.layout();

        // TODO cope with becoming smaller or larger
    }

    // documentation inherited
    protected void renderComponent (Renderer renderer)
    {
        super.renderComponent(renderer);

        Insets insets = getInsets();
        int tx = insets.left, ty = insets.bottom, cx = tx;

        // render our text
        if (_glyphs != null) {
            _glyphs.render(renderer, tx, ty);

            // locate the cursor position
            if (_showCursor) {
                cx += _glyphs.getCursorPos(_cursorPos);
            }
        }

        // render the cursor if we have focus
        if (_showCursor) {
            ColorRGBA c = ColorRGBA.white;
            GL11.glColor4f(c.r, c.g, c.b, c.a);
            GL11.glBegin(GL11.GL_LINE_STRIP);
            GL11.glVertex2f(cx, insets.bottom);
            int cheight = getLookAndFeel().getTextFactory().getHeight();
            GL11.glVertex2f(cx, insets.bottom + cheight);
            GL11.glEnd();
        }
    }

    // documentation inherited
    protected Dimension computePreferredSize (int whint, int hhint)
    {
        Dimension d = (_glyphs == null) ?
            new Dimension(0, getLookAndFeel().getTextFactory().getHeight()) :
            new Dimension(_glyphs.getSize());
        if (_prefWidth != -1) {
            d.width = _prefWidth;
        }
        return d;
    }

    /**
     * Recreates the entity that we use to render our text.
     */
    protected void recreateGlyphs ()
    {
        if (_glyphs != null) {
            _glyphs = null;
        }

        if (_text == null) {
            return;
        }

        BLookAndFeel lnf = getLookAndFeel();
        _glyphs = lnf.getTextFactory().createText(
            getDisplayText(), lnf.getForeground(isEnabled()));
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
        int vizChars = 10; // TODO computeVisibleChars();
        _cursorPos = cursorPos;
        if (_cursorPos < _offset) {
            _offset = _cursorPos;
        } else if (_cursorPos > (_offset + vizChars)) {
            _offset = (_cursorPos-vizChars);
        } else if (_offset > 0 && (_cursorPos < (_offset + vizChars))) {
            _offset = (_cursorPos-vizChars);
        }
    }

    protected String _text;
    protected BText _glyphs;
    protected BKeyMap _keymap;

    protected int _prefWidth = -1;
    protected boolean _showCursor;
    protected int _cursorPos, _offset;
}
