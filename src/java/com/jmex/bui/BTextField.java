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
import com.jmex.bui.event.MouseEvent;
import com.jmex.bui.event.TextEvent;
import com.jmex.bui.text.BKeyMap;
import com.jmex.bui.text.BText;
import com.jmex.bui.text.Document;
import com.jmex.bui.text.EditCommands;
import com.jmex.bui.util.Dimension;
import com.jmex.bui.util.Insets;

/**
 * Displays and allows for the editing of a single line of text.
 */
public class BTextField extends BTextComponent
    implements EditCommands, Document.Listener
{
    /**
     * Creates a blank text field.
     */
    public BTextField ()
    {
        this("");
    }

    /**
     * Creates a text field with the specified starting text.
     */
    public BTextField (String text)
    {
        setDocument(new Document());
        setText(text);
    }

    /**
     * Configures this text field with the specified text for display and
     * editing. The cursor will be adjusted if this text is shorter than
     * its previous position.
     */
    public void setText (String text)
    {
        if (text == null) {
            text = "";
        }
        if (!_text.getText().equals(text)) {
            _text.setText(text);
        }
    }

    // documentation inherited
    public String getText ()
    {
        return _text.getText();
    }

    /**
     * Configures this text field with a custom document.
     */
    public void setDocument (Document document)
    {
        _text = document;
        _text.addListener(this);
    }

    /**
     * Returns the underlying document used by this text field to maintain its
     * state. Changes to the document will be reflected in the text field
     * display.
     */
    public Document getDocument ()
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

    // documentation inherited from interface Document.Listener
    public void textInserted (Document document, int offset, int length)
    {
        // if we're already part of the hierarchy, recreate our glyps
        if (isAdded()) {
            recreateGlyphs();
        }

        // let anyone who is around to hear know that a tree fell in the woods
        dispatchEvent(new TextEvent(this, -1L));
    }

    // documentation inherited from interface Document.Listener
    public void textRemoved (Document document, int offset, int length)
    {
        // confine the cursor to the new text
        if (_cursorPos > _text.getLength()) {
            setCursorPos(_text.getLength());
        }

        // if we're already part of the hierarchy, recreate our glyps
        if (isAdded()) {
            recreateGlyphs();
        }

        // let anyone who is around to hear know that a tree fell in the woods
        dispatchEvent(new TextEvent(this, -1L));
    }

    // documentation inherited
    public boolean acceptsFocus ()
    {
        return isEnabled();
    }

    // documentation inherited
    public void wasAdded ()
    {
        super.wasAdded();

        // create our underlying glyphs
        recreateGlyphs();
    }

    // documentation inherited
    public void dispatchEvent (BEvent event)
    {
        if (event instanceof KeyEvent) {
            KeyEvent kev = (KeyEvent)event;
            if (kev.getType() == KeyEvent.KEY_PRESSED) {
                int modifiers = kev.getModifiers(), keyCode = kev.getKeyCode();
                switch (_keymap.lookupMapping(modifiers, keyCode)) {
                case BACKSPACE:
                    if (_cursorPos > 0 && _text.getLength() > 0) {
                        int pos = _cursorPos-1;
                        if (_text.remove(pos, 1)) { // might change _cursorPos
                            setCursorPos(pos);
                        }
                    }
                    break;

                case DELETE:
                    if (_cursorPos < _text.getLength()) {
                        _text.remove(_cursorPos, 1);
                    }
                    break;

                case CURSOR_LEFT:
                    setCursorPos(Math.max(0, _cursorPos-1));
                    break;

                case CURSOR_RIGHT:
                    setCursorPos(Math.min(_text.getLength(), _cursorPos+1));
                    break;

                case START_OF_LINE:
                    setCursorPos(0);
                    break;

                case END_OF_LINE:
                    setCursorPos(_text.getLength());
                    break;

                case ACTION:
                    dispatchEvent(
                        new ActionEvent(
                            this, kev.getWhen(), kev.getModifiers(), ""));
                    break;

                case RELEASE_FOCUS:
                    getWindow().requestFocus(null);
                    break;

                case CLEAR:
                    _text.setText("");
                    break;

                default:
                    // insert printable and shifted printable characters
                    char c = kev.getKeyChar();
                    if ((modifiers & ~KeyEvent.SHIFT_DOWN_MASK) == 0 &&
                        !Character.isISOControl(c)) {
                        String text = String.valueOf(kev.getKeyChar());
                        if (_text.insertText(_cursorPos, text)) {
                            setCursorPos(_cursorPos + 1);
                        }
                    } else {
                        super.dispatchEvent(event);
                    }
                    break;
                }
            }

        } else if (event instanceof MouseEvent) {
            MouseEvent mev = (MouseEvent)event;
            if (mev.getType() == MouseEvent.MOUSE_PRESSED &&
                // don't adjust the cursor if we have no text
                _text.getLength() > 0) {
                Insets insets = getInsets();
                int mx = mev.getX() - getAbsoluteX() - insets.left,
                    my = mev.getY() - getAbsoluteY() - insets.bottom;
                setCursorPos(_glyphs.getHitPos(mx, my));
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

        } else {
            super.dispatchEvent(event);
        }
    }

    // documentation inherited
    protected String getDefaultStyleClass ()
    {
        return "textfield";
    }

    // documentation inherited
    protected void configureStyle (BStyleSheet style)
    {
        super.configureStyle(style);

        // look up our keymap
        _keymap = style.getKeyMap(this, null);
    }

    // documentation inherited
    protected void layout ()
    {
        super.layout();

        // TODO cope with becoming smaller or larger
    }

    // documentation inherited
    protected void stateDidChange ()
    {
        super.stateDidChange();
        recreateGlyphs();
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
            ColorRGBA c = getColor();
            GL11.glColor4f(c.r, c.g, c.b, c.a);
            GL11.glBegin(GL11.GL_LINE_STRIP);
            GL11.glVertex2f(cx, insets.bottom);
            int cheight = getTextFactory().getHeight();
            GL11.glVertex2f(cx, insets.bottom + cheight);
            GL11.glEnd();
        }
    }

    // documentation inherited
    protected Dimension computePreferredSize (int whint, int hhint)
    {
        Dimension d = (_glyphs == null) ?
            new Dimension(0, getTextFactory().getHeight()) :
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
        if (_text.getLength() == 0) {
            return;
        }
        _glyphs = getTextFactory().createText(getDisplayText(), getColor());
    }

    /**
     * This method allows a derived class (specifically {@link
     * BPasswordField}) to display something other than the actual
     * contents of the text field.
     */
    protected String getDisplayText ()
    {
        return _text.getText();
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

    protected Document _text;
    protected BText _glyphs;
    protected BKeyMap _keymap;

    protected int _prefWidth = -1;
    protected boolean _showCursor;
    protected int _cursorPos, _offset;
}
