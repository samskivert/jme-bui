//
// $Id$
//
// BUI - a user interface library for the JME 3D engine
// Copyright (C) 2005-2006, Michael Bayne, All Rights Reserved
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

import com.jmex.bui.text.BTextFactory;

/**
 * Defines methods and mechanisms common to components that render a string of
 * text.
 */
public abstract class BTextComponent extends BComponent
{
    /**
     * Updates the text displayed by this component.
     */
    public abstract void setText (String text);

    /**
     * Returns the text currently being displayed by this component.
     */
    public abstract String getText ();

    /**
     * Returns a text factory suitable for creating text in the style defined
     * by the component's current state.
     */
    public BTextFactory getTextFactory ()
    {
        BTextFactory textfact = _textfacts[getState()];
        return (textfact != null) ? textfact : _textfacts[DEFAULT];
    }

    /**
     * Returns the horizontal alignment for this component's text.
     */
    public int getHorizontalAlignment ()
    {
        int halign = _haligns[getState()];
        return (halign != -1) ? halign : _haligns[DEFAULT];
    }

    /**
     * Returns the vertical alignment for this component's text.
     */
    public int getVerticalAlignment ()
    {
        int valign = _valigns[getState()];
        return (valign != -1) ? valign : _valigns[DEFAULT];
    }

    // documentation inherited
    protected void configureStyle (BStyleSheet style)
    {
        super.configureStyle(style);

        for (int ii = 0; ii < getStateCount(); ii++) {
            _haligns[ii] = style.getTextAlignment(this, getStatePseudoClass(ii));
            _valigns[ii] =
                style.getVerticalAlignment(this, getStatePseudoClass(ii));
            _textfacts[ii] = style.getTextFactory(this, getStatePseudoClass(ii));
        }
    }

    protected int[] _haligns = new int[getStateCount()];
    protected int[] _valigns = new int[getStateCount()];
    protected BTextFactory[] _textfacts = new BTextFactory[getStateCount()];
}
