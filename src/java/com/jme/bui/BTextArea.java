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

import java.awt.Dimension;
import java.util.ArrayList;

import com.jme.bui.event.BEvent;
import com.jme.bui.font.BFont;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.system.DisplaySystem;

/**
 * Displays one or more lines of text which may contain basic formatting
 * (changing of color, toggling bold, italic and underline). Newline
 * characters in the appended text will result in line breaks in the
 * on-screen layout.
 */
public class BTextArea extends BContainer
{
    /** A font style constant. */
    public static final int PLAIN = 0;

    /** A font style constant. */
    public static final int BOLD = 1;

    /** A font style constant. */
    public static final int ITALIC = 2;

    /** A font style constant. */
    public static final int UNDERLINE = 3;

    public BTextArea ()
    {
    }

    /**
     * Appends text with the foreground color in the plain style.
     */
    public void appendText (String text)
    {
        appendText(text, null);
    }

    /**
     * Appends text with the specified color in the plain style.
     */
    public void appendText (String text, ColorRGBA color)
    {
        appendText(text, color, PLAIN);
    }

    /**
     * Appends text with the foreground color in the specified style.
     */
    public void appendText (String text, int style)
    {
        appendText(text, null, style);
    }

    /**
     * Appends text with the specified color and style.
     */
    public void appendText (String text, ColorRGBA color, int style)
    {
        int offset = 0, nlidx;
        while ((nlidx = text.indexOf("\n", offset)) != -1) {
            String line = text.substring(offset, nlidx);
            _runs.add(new Run(line, color, style, true));
            offset = nlidx+1;
        }
        if (offset < text.length()) {
            _runs.add(new Run(text.substring(offset), color, style, false));
        }
        // TODO: optimize appending
        refigureContents();
    }

    /**
     * Clears out the text displayed in this area.
     */
    public void clearText ()
    {
        _runs.clear();
        refigureContents();
    }

    /**
     * Scrolls our display such that the sepecified line is visible.
     */
    public void scrollToLine (int line)
    {
        // TODO
    }

    /**
     * Returns the number of lines of text contained in this area.
     */
    public int getLineCount ()
    {
        return _lines.size();
    }

    // documentation inherited
    public void wasAdded ()
    {
        super.wasAdded();

        // create our background
        _background = getLookAndFeel().createTextBack();
        add(_background);
        _background.wasAdded();

        // create a node that will contain our text
        _text = new Node(name + ":text");
        attachChild(_text);
        _text.updateRenderState();
    }

    // documentation inherited
    public void wasRemoved ()
    {
        super.wasRemoved();

        if (_background != null) {
            remove(_background);
            _background = null;
        }

        if (_text != null) {
            detachChild(_text);
            _text = null;
        }
    }

    // documentation inherited
    public void dispatchEvent (BEvent event)
    {
        super.dispatchEvent(event);

        // TBD
    }

    // documentation inherited
    protected void layout ()
    {
        super.layout();

        // our background occupies our entire dimensions
        _background.setBounds(0, 0, _width, _height);

        refigureContents();
    }

    // documentation inherited
    protected Dimension computePreferredSize ()
    {
        Dimension d = new Dimension(100, 25); // TBD
        d.width += _background.getLeftInset();
        d.width += _background.getRightInset();
        d.height += _background.getTopInset();
        d.height += _background.getBottomInset();
        return d;
    }

    /**
     * Reflows the entirety of our text.
     */
    protected void refigureContents ()
    {
        // remove and recreate our existing lines
        for (int ii = 0, ll = _lines.size(); ii < ll; ii++) {
            _text.detachChild((Line)_lines.get(ii));
        }
        _lines.clear();

        // determine our active font and the number of characters per line
        ColorRGBA fg = getLookAndFeel().getForeground();
        BFont font = getLookAndFeel().getFont();
        int insets = _background.getLeftInset() + _background.getTopInset();
        int cpl = (_width - insets) / font.getWidth('A');

        // wrap our text into lines
        Line current = null;
        for (int ii = 0, ll = _runs.size(); ii < ll; ii++) {
            Run run = (Run)_runs.get(ii);
            if (current == null) {
                _lines.add(current = new Line(_lines.size()));
            }
            int offset = 0;
            while ((offset = current.addRun(font, fg, cpl, run, offset)) > 0) {
                _lines.add(current = new Line(_lines.size()));
            }
            if (run.endsLine) {
                current = null;
            }
        }

        // determine how many lines we can display in total
        insets = _background.getTopInset() + _background.getBottomInset();
        int lines = (_height - insets) / font.getHeight();

        // now position each of our lines properly
        int x = _background.getLeftInset();
        int y = _height - font.getHeight() - _background.getTopInset();
        int sline = Math.max(0, _lines.size() - lines);
        for (int ii = sline, ll = _lines.size(); ii < ll; ii++) {
            Line line = (Line)_lines.get(ii);
            _text.attachChild(line);
            line.updateGeometricState(0.0f, true);
            line.updateRenderState();
            // TEMP: handle Text offset bug
            int fx = x - 4;
            line.setLocalTranslation(new Vector3f(fx, y, 0));
            y -= font.getHeight();
        }
    }

    /** Used to associate a style with a run of text. */
    protected static class Run
    {
        public String text;
        public ColorRGBA color;
        public int style;
        public boolean endsLine;

        public Run (String text, ColorRGBA color, int style, boolean endsLine) {
            this.text = text;
            this.color = color;
            this.style = style;
            this.endsLine = endsLine;
        }
    }

    /** Contains the segments of text on a single line. */
    protected static class Line extends Node
    {
        /** The run that starts this line. */
        public Run start;

        /** The offset into the run at which this line starts. */
        public int startOffset;

        /** The run that ends this line. */
        public Run end;

        /** The number of characters on this line. */
        public int length;

        public Line (int lineNo)
        {
            super("line" + lineNo);
        }

        /**
         * Adds the supplied run to the line using the supplied font,
         * returns the offset into the run that must be appeneded to a new
         * line or -1 if the entire run was appended.
         */
        public int addRun (BFont font, ColorRGBA foreground, int charsPerLine,
                           Run run, int offset)
        {
            if (length == 0) {
                start = run;
                startOffset = offset;
            }
            int dx = length * font.getWidth('A');
            int ravail = run.text.length()-offset;
            int used = Math.min(ravail, charsPerLine-length);
            String rtext = run.text.substring(offset, offset+used);
            Text text = new Text("line", rtext);
            font.configure(text);
            text.setTextColor(run.color == null ? foreground : run.color);
            text.setLocalTranslation(new Vector3f(dx, 0, 0));
            attachChild(text);
            length += used;
            return (used < ravail) ? offset + used : -1;
        }
    }

    protected BBackground _background;
    protected Node _text;
    protected ArrayList _runs = new ArrayList();
    protected ArrayList _lines = new ArrayList();
}
