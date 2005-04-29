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

import com.jme.bui.font.BFont;
import com.jme.bui.font.BGlyph;
import com.jme.renderer.ColorRGBA;
import com.jme.math.Vector3f;
import com.jme.scene.Text;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.system.DisplaySystem;

/**
 * A simple component for displaying a textual label.
 */
public class BLabel extends BComponent
    implements BConstants
{
    /**
     * Creates a label that will display the supplied text.
     */
    public BLabel (String text)
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
     * Configures this label's horizontal alignment.
     */
    public void setHorizontalAlignment (int align)
    {
        if (_halign != align) {
            _halign = align;
            if (isAdded()) {
                layout();
            }
        }
    }

    /**
     * Returns this label's horizontal alignment setting.
     */
    public int getHorizontalAlignment ()
    {
        return _halign;
    }

    /**
     * Configures this label's vertical alignment.
     */
    public void setVerticalAlignment (int align)
    {
        if (_valign != align) {
            _valign = align;
            if (isAdded()) {
                layout();
            }
        }
    }

    /**
     * Returns this label's vertical alignment setting.
     */
    public int getVerticalAlignment ()
    {
        return _valign;
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
            relayout();
        }
    }

    // documentation inherited
    public void wasAdded ()
    {
        super.wasAdded();

        // create our underlying glyphs
        recreateGlyphs();
    }

    // documentation inherited
    public void layout ()
    {
        super.layout();

        float xoff;
        switch (_halign) {
        case CENTER: xoff = (_width - _tgeom.getWidth()) / 2; break;
        case RIGHT: xoff = _width - _tgeom.getWidth(); break;
        default:
        case LEFT: xoff = 0;
        }

        float yoff;
        switch (_valign) {
        default:
        case CENTER: yoff = (_height - _tgeom.getHeight()) / 2; break;
        case TOP: yoff = _height - _tgeom.getHeight(); break;
        case BOTTOM: yoff = 0; break;
        }

        _slab.setLocalTranslation(
            new Vector3f(xoff + _tsize.width/2, yoff + _tsize.height/2, 0));
        // TEMP: handle Text offset bug
        xoff -= 4f;
        _tgeom.setLocalTranslation(new Vector3f(xoff, yoff, 0));
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
            detachChild(_slab);
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

        _slab = new Quad("foo", _tsize.width, _tsize.height);
        _slab.setSolidColor(ColorRGBA.red);
        _slab.updateRenderState();
        attachChild(_slab);

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
    protected Quad _slab;
    protected Text _tgeom;
    protected Dimension _tsize;
    protected int _halign = LEFT, _valign = CENTER;
}
