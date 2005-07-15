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

import com.jme.bui.event.ActionEvent;
import com.jme.bui.event.ActionListener;
import com.jme.bui.layout.BorderLayout;

/**
 * Displays a label with a check-box button next to it.
 */
public class BCheckBox extends BContainer
    implements ActionListener
{
    public BCheckBox (String label)
    {
        super(new BorderLayout(5, 5));
        add(_check = new BToggleButton(""), BorderLayout.WEST);
        _check.addListener(this);
        add(_label = new BLabel(label), BorderLayout.CENTER);;
    }

    /**
     * Returns whether or not this checkbox is in the checked state.
     */
    public boolean isChecked ()
    {
        return _check.isSelected();
    }

    /**
     * Checks or unchecks this checkbox.
     */
    public void setChecked (boolean checked)
    {
        _check.setSelected(checked);
    }

    // documentation inherited
    public void wasAdded ()
    {
        super.wasAdded();

        // load up our icons
        _cicon = getLookAndFeel().createCheckBoxIcon();
        _bicon = new BlankIcon(_cicon.getWidth(), _cicon.getHeight());
        _check.setIcon(isChecked() ? _cicon : _bicon);
    }

    // documentation inherited from interface ActionEvent
    public void actionPerformed (ActionEvent event)
    {
        _check.setIcon(isChecked() ? _cicon : _bicon);
        dispatchEvent(
            new ActionEvent(this, event.getWhen(), event.getModifiers(), ""));
    }

    protected BIcon _cicon, _bicon;
    protected BToggleButton _check;
    protected BLabel _label;
}
