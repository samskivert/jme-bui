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

import java.util.ArrayList;

import com.jmex.bui.background.BBackground;
import com.jmex.bui.event.BEvent;
import com.jmex.bui.event.ChangeEvent;
import com.jmex.bui.event.ChangeListener;
import com.jmex.bui.text.BText;
import com.jmex.bui.text.BTextFactory;
import com.jmex.bui.util.Dimension;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
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
        this(null);
    }

    public BTextArea (String text)
    {
        _model.addChangeListener(new ChangeListener() {
            public void stateChanged (ChangeEvent event) {
                modelDidChange();
            }
        });
        if (text != null) {
            setText(text);
        }
    }

    /**
     * Configures the preferred width of this text area (the preferred height
     * will be calculated from the font).
     */
    public void setPreferredWidth (int width)
    {
        _prefWidth = width;
    }

    /**
     * Returns a model that can be wired to a scroll bar to allow
     * scrolling up and down through the lines in this text area.
     */
    public BoundedRangeModel getScrollModel ()
    {
        return _model;
    }

    /**
     * Clears any text in this text area and appends the supplied text.
     */
    public void setText (String text)
    {
        clearText();
        appendText(text);
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
        refigureContents(getWidth());
    }

    /**
     * Clears out the text displayed in this area.
     */
    public void clearText ()
    {
        _runs.clear();
        refigureContents(getWidth());
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
    public void setEnabled (boolean enabled)
    {
        boolean wasEnabled = isEnabled();
        super.setEnabled(enabled);
        if (isAdded() && wasEnabled != isEnabled()) {
            refigureContents(getWidth());
        }
    }

    // documentation inherited
    public void wasAdded ()
    {
        super.wasAdded();

        // create our background
        _background = getLookAndFeel().createTextBack();
    }

    // documentation inherited
    public void wasRemoved ()
    {
        super.wasRemoved();
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

        refigureContents(getWidth());
    }

    // documentation inherited
    protected void renderComponent (Renderer renderer)
    {
        super.renderComponent(renderer);

        int x = _background.getLeftInset();
        int y = _height - _background.getTopInset();

        int start = _model.getValue(), stop = start + _model.getExtent();
        for (int ii = start; ii < stop; ii++) {
            Line line = (Line)_lines.get(ii);
            y -= line.height;
            line.render(renderer, x, y);
        }
    }

    // documentation inherited
    protected Dimension computePreferredSize (int whint, int hhint)
    {
        int hinset = _background.getLeftInset() + _background.getRightInset();

        // lay out our text if we have not yet done so
        if (_lines.size() == 0) {
            if (_prefWidth > 0) {
                // our preferred width overrides any hint
                whint = _prefWidth;
            } else if (whint == -1) {
                // if we're given no hints and have no preferred width, allow
                // arbitrarily wide lines
                whint = Short.MAX_VALUE;
            }
            whint -= hinset;
            refigureContents(whint);
        }

        // compute our dimensions based on the dimensions of our text
        Dimension d = new Dimension();
        for (int ii = 0, ll = _lines.size(); ii < ll; ii++) {
            Line line = (Line)_lines.get(ii);
            d.width = Math.max(line.getWidth(), d.width);
            d.height += line.height;
        }

        // add our background insets
        d.width += hinset;
        d.height += _background.getTopInset();
        d.height += _background.getBottomInset();

        return d;
    }

    /**
     * Reflows the entirety of our text.
     */
    protected void refigureContents (int width)
    {
        // if we're not yet added to the heirarchy, we can stop now
        if (!isAdded()) {
            return;
        }

        // remove and recreate our existing lines
        _lines.clear();

        BLookAndFeel lnf = getLookAndFeel();
        ColorRGBA fg = lnf.getForeground(isEnabled());
        BTextFactory tfact = lnf.getTextFactory();
        int insets = _background.getLeftInset() + _background.getRightInset();
        int maxWidth = (width - insets);

        // wrap our text into lines
        Line current = null;
        for (int ii = 0, ll = _runs.size(); ii < ll; ii++) {
            Run run = (Run)_runs.get(ii);
            if (current == null) {
                _lines.add(current = new Line());
            }
            int offset = 0;
            ColorRGBA color = (run.color == null) ? fg : run.color;
            while ((offset = current.addRun(
                        tfact, run, color, maxWidth, offset)) > 0) {
                _lines.add(current = new Line());
            }
            if (run.endsLine) {
                current = null;
            }
        }

        // determine how many lines we can display in total
        insets = _background.getTopInset() + _background.getBottomInset();

        // start at the last line and see how many we can fit
        int lines = 0, lheight = 0;
        for (int ll = _lines.size()-1; ll >= 0; ll--) {
            lheight += ((Line)_lines.get(ll)).height;
            if (lheight > _height) {
                break;
            }
            lines++;
        }

        // update our model (which will cause the text to be repositioned)
        int sline = Math.max(0, _lines.size() - lines);
        if (!_model.setRange(0, sline, lines, _lines.size())) {
            // we need to force adjustment of the text even if we didn't
            // change anything because we wiped out and recreated all of
            // our lines
            modelDidChange();
        }
    }

    /**
     * Called when our model has changed (due to scrolling by a scroll bar
     * or a call to {@link #scrollToLine}, etc.).
     */
    protected void modelDidChange ()
    {
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
    protected static class Line
    {
        /** The run that starts this line. */
        public Run start;

        /** The run that ends this line. */
        public Run end;

        /** The current x position at which new text will be appended. */
        public int dx;

        /** The height of this line. */
        public int height;

        /** A list of {@link BText} instances for the text on this line. */
        public ArrayList segments = new ArrayList();

        /**
         * Adds the supplied run to the line using the supplied text
         * factory, returns the offset into the run that must be appeneded
         * to a new line or -1 if the entire run was appended.
         */
        public int addRun (BTextFactory tfact, Run run, ColorRGBA color,
                           int maxWidth, int offset)
        {
            if (dx == 0) {
                start = run;
            }
            String rtext = run.text.substring(offset);
            int[] remainder = new int[1];
            BText text = tfact.wrapText(rtext, color, maxWidth-dx, remainder);
            segments.add(text);
            height = Math.max(height, text.getSize().height);
            dx += text.getSize().width;
            return (remainder[0] == 0) ? -1 : run.text.length() - remainder[0];
        }

        /**
         * Renders this line of text.
         */
        public void render (Renderer renderer, int x, int y)
        {
            int dx = x;
            for (int ii = 0, ll = segments.size(); ii < ll; ii++) {
                BText text = (BText)segments.get(ii);
                text.render(renderer, dx, y);
                dx += text.getSize().width;
            }
        }

        /**
         * Returns the width of this line.
         */
        public int getWidth ()
        {
            int width = 0;
            for (int ii = 0, ll = segments.size(); ii < ll; ii++) {
                width += ((BText)segments.get(ii)).getSize().width;
            }
            return width;
        }
    }

    protected BBackground _background;
    protected BoundedRangeModel _model = new BoundedRangeModel(0, 0, 0, 0);
    protected int _prefWidth = -1;
    protected ArrayList _runs = new ArrayList();
    protected ArrayList _lines = new ArrayList();
}
