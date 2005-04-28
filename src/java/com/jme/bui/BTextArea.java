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
import java.util.ArrayList;

import com.jme.bui.event.BEvent;
import com.jme.bui.font.BFont;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.state.AlphaState;
import com.jme.system.DisplaySystem;

/**
 * Displays one or more lines of text which may contain basic formatting
 * (changing of color, toggling bold, italic and underline). Newline
 * characters in the appended text will result in line breaks in the
 * on-screen layout.
 */
public class BTextArea extends BComponent
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
        AlphaState astate = DisplaySystem.getDisplaySystem().getRenderer().
            createAlphaState();
        astate.setBlendEnabled(true);
        astate.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        astate.setDstFunction(AlphaState.DB_ONE);
        astate.setTestEnabled(true);
        astate.setTestFunction(AlphaState.TF_GREATER);
        astate.setEnabled(true);
        setRenderState(astate);
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
        attachChild(_background);
        _background.wasAdded();

        // lay out our text for the first time
        refigureContents();
    }

    // documentation inherited
    public void wasRemoved ()
    {
        super.wasRemoved();

        if (_background != null) {
            detachChild(_background);
            _background.wasRemoved();
        }
    }

    // documentation inherited
    public void layout ()
    {
        super.layout();

        // we must lay out our children by hand as we're not a container
        _background.layout();
    }

    // documentation inherited
    public void setBounds (int x, int y, int width, int height)
    {
        super.setBounds(x, y, width, height);

        _background.setBounds(0, 0, width, height);
        refigureContents();
    }

    // documentation inherited
    public void dispatchEvent (BEvent event)
    {
        super.dispatchEvent(event);

        // TBD
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
            detachChild((Line)_lines.get(ii));
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
            attachChild(line);
            line.updateGeometricState(0.0f, true);
            line.updateRenderState();
            line.setLocalTranslation(new Vector3f(x, y, 0));
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
            // TODO: we need to change the color of the font texture
            text.setSolidColor(run.color == null ? foreground : run.color);
            text.setLocalTranslation(new Vector3f(dx, 0, 0));
            attachChild(text);
            length += used;
            return (used < ravail) ? offset + used : -1;
        }
    }

    protected BBackground _background;
    protected ArrayList _runs = new ArrayList();
    protected ArrayList _lines = new ArrayList();
}
