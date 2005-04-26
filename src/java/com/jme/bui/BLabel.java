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

import com.jme.bui.font.BFont;
import com.jme.bui.font.BGlyph;
import com.jme.math.Vector3f;
import com.jme.scene.Text;

/**
 * A simple component for displaying a textual label.
 */
public class BLabel extends BComponent
{
    /**
     * Creates a label that will display the supplied text.
     */
    public BLabel (String text)
    {
        setText(text);
    }

    /**
     * Returns the text currently being displayed by this label.
     */
    public String getText ()
    {
        return _text;
    }

    /**
     * Updates the text displayed by this label.
     */
    public void setText (String text)
    {
        _text = text;

        // if we're already part of the hierarchy, recreate our glyps
        if (isAdded()) {
            recreateGlyphs();
        }
    }

    // documentation inherited
    public void wasAdded ()
    {
        super.wasAdded();

        // create our underlying glyphs
        recreateGlyphs();
    }

    /**
     * Clears out old glyphs and creates new ones for our current text.
     */
    protected void recreateGlyphs ()
    {
//         if (_glyphs != null) {
//             for (int ii = 0; ii < _glyphs.length; ii++) {
//                 detachChild(_glyphs[ii]);
//             }
//         }

        if (_tgeom != null) {
            detachChild(_tgeom);
        }

        BLookAndFeel lnf = getLookAndFeel();
        BFont font = lnf.getFont();
//         _tsize = new Dimension(0, (int)font.getHeight());
//         _glyphs = new BGlyph[_text.length()];
//         for (int ii = 0; ii < _glyphs.length; ii++) {
//             char cchar = _text.charAt(ii);
//             int cwidth = font.getWidth(cchar);
//             _glyphs[ii] = font.createCharacter(cchar);
//             _glyphs[ii].setLocalTranslation(
//                 new Vector3f(_tsize.width + cwidth/2, _tsize.height/2, 0));
//             _glyphs[ii].setSolidColor(lnf.getForeground());
//             attachChild(_glyphs[ii]);
//             _tsize.width += cwidth;
//         }

        _tgeom = new Text(name + ":text", _text);
        _tgeom.setSolidColor(lnf.getForeground());
        _tsize = new Dimension((int)_tgeom.getWidth(), (int)_tgeom.getHeight());
        font.configure(_tgeom);
        attachChild(_tgeom);

        updateGeometricState(0.0f, true);
        updateRenderState();
    }

    // documentation inherited
    protected Dimension computePreferredSize ()
    {
        return _tsize;
    }

    protected String _text;
//     protected BGlyph[] _glyphs;
    protected Text _tgeom;
    protected Dimension _tsize;
}
