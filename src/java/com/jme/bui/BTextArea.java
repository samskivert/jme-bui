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

import java.util.ArrayList;

import com.jme.bui.background.BBackground;
import com.jme.bui.event.BEvent;
import com.jme.bui.event.ChangeEvent;
import com.jme.bui.event.ChangeListener;
import com.jme.bui.text.BText;
import com.jme.bui.text.BTextFactory;
import com.jme.bui.util.Dimension;
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
        _model.addChangeListener(new ChangeListener() {
            public void stateChanged (ChangeEvent event) {
                modelDidChange();
            }
        });
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

    /**
     * Returns a reference to the background used by this text area.
     */
    public BBackground getBackground ()
    {
        return _background;
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
        _text = new Node("text");
        _node.attachChild(_text);
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
            _node.detachChild(_text);
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

        ColorRGBA fg = getLookAndFeel().getForeground();
        BTextFactory tfact = getLookAndFeel().getTextFactory();
        int insets = _background.getLeftInset() + _background.getRightInset();
        int maxWidth = (_width - insets);

        // wrap our text into lines
        Line current = null;
        for (int ii = 0, ll = _runs.size(); ii < ll; ii++) {
            Run run = (Run)_runs.get(ii);
            if (current == null) {
                _lines.add(current = new Line(_lines.size()));
            }
            int offset = 0;
            while ((offset =
                    current.addRun(tfact, fg, maxWidth, run, offset)) > 0) {
                _lines.add(current = new Line(_lines.size()));
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
        for (int ii = 0, ll = _lines.size(); ii < ll; ii++) {
            _text.detachChild((Line)_lines.get(ii));
        }

        int x = _background.getLeftInset();
        int y = _height - _background.getTopInset();

        int start = _model.getValue(), stop = start + _model.getExtent();
        for (int ii = start; ii < stop; ii++) {
            Line line = (Line)_lines.get(ii);
            y -= line.height;
            _text.attachChild(line);
            line.updateGeometricState(0.0f, true);
            line.updateRenderState();
            line.setLocalTranslation(new Vector3f(x, y, 0));
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

        /** The run that ends this line. */
        public Run end;

        /** The current x position at which new text will be appended. */
        public int dx;

        /** The height of this line. */
        public int height;

        public Line (int lineNo)
        {
            super("line" + lineNo);
        }

        /**
         * Adds the supplied run to the line using the supplied text
         * factory, returns the offset into the run that must be appeneded
         * to a new line or -1 if the entire run was appended.
         */
        public int addRun (BTextFactory tfact, ColorRGBA foreground,
                           int maxWidth, Run run, int offset)
        {
            if (dx == 0) {
                start = run;
            }
            String rtext = run.text.substring(offset);
            int[] remainder = new int[1];
            BText text = tfact.wrapText(rtext, maxWidth-dx, remainder);
            text.setLocation(dx, 0);
            attachChild(text.getGeometry());
            height = Math.max(height, text.getSize().height);
            dx += text.getSize().width;
            return (remainder[0] == 0) ? -1 : run.text.length() - remainder[0];
        }
    }

    protected BBackground _background;
    protected BoundedRangeModel _model = new BoundedRangeModel(0, 0, 0, 0);
    protected Node _text;
    protected ArrayList _runs = new ArrayList();
    protected ArrayList _lines = new ArrayList();
}
