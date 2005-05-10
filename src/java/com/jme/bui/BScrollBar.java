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

import com.jme.bui.event.ActionListener;
import com.jme.bui.event.ActionEvent;
import com.jme.bui.event.ChangeEvent;
import com.jme.bui.event.ChangeListener;
import com.jme.bui.layout.BorderLayout;

/**
 * Displays a scrollbar for all your horizontal and vertical scrolling
 * needs.
 */
public class BScrollBar extends BContainer
    implements BConstants
{
    /**
     * Creates a vertical scroll bar with the default range, value and
     * extent.
     */
    public BScrollBar ()
    {
        this(VERTICAL);
    }

    /**
     * Creates a scroll bar with the default range, value and extent.
     */
    public BScrollBar (int orientation)
    {
        this(orientation, 0, 100, 0, 10);
    }

    /**
     * Creates a scroll bar with the specified orientation, range, value
     * and extent.
     */
    public BScrollBar (int orientation, int min, int value, int extent, int max)
    {
        super(new BorderLayout());
        _orient = orientation;
        _model = new BoundedRangeModel(min, value, extent, max);
        _model.addChangeListener(_updater);
    }

    // documentation inherited
    public void wasAdded ()
    {
        super.wasAdded();

        // create our buttons and backgrounds
        BLookAndFeel lnf = getLookAndFeel();
        add(_well = lnf.createScrollWell(_orient), BorderLayout.CENTER);
        add(_thumb = lnf.createScrollThumb(_orient), BorderLayout.IGNORE);
        add(_less = lnf.createScrollButton(_orient, true),
            _orient == HORIZONTAL ? BorderLayout.WEST : BorderLayout.NORTH);
        _less.addListener(_buttoner);
        _less.setAction("less");
        add(_more = lnf.createScrollButton(_orient, false),
            _orient == HORIZONTAL ? BorderLayout.EAST : BorderLayout.SOUTH);
        _more.addListener(_buttoner);
        _more.setAction("more");
    }

    // documentation inherited
    public void wasRemoved ()
    {
        super.wasRemoved();

        if (_well != null) {
            remove(_well);
            _well = null;
        }
        if (_thumb != null) {
            remove(_thumb);
            _thumb = null;
        }
        if (_less != null) {
            remove(_less);
            _less = null;
        }
        if (_more != null) {
            remove(_more);
            _more = null;
        }
    }

    /**
     * Recomputes and repositions the scrollbar thumb to reflect the
     * current configuration of the model.
     */
    protected void update ()
    {
        int tx = 0, ty = 0;
        int twidth = _well.getContentWidth(), theight = _well.getContentHeight();
        int range = _model.getRange();
        int extent = Math.max(_model.getExtent(), 1); // avoid div0
        if (_orient == HORIZONTAL) {
            int wellSize = _well.getContentWidth();
            tx = _model.getValue() * wellSize / range;
            twidth = extent * wellSize / range;
        } else {
            int wellSize = _well.getContentHeight();
            ty = (range-extent-_model.getValue()) * wellSize / range;
            theight = extent * wellSize / range;
        }
        _thumb.setBounds(_well.getX() + _well.getLeftInset() + tx,
                         _well.getY() + _well.getBottomInset() + ty,
                         twidth, theight);
    }

    // documentation inherited
    protected void layout ()
    {
        super.layout();

        // reposition our thumb
        update();
    }

    protected ChangeListener _updater = new ChangeListener() {
        public void stateChanged (ChangeEvent event) {
            update();
        }
    };

    protected ActionListener _buttoner = new ActionListener() {
        public void actionPerformed (ActionEvent event) {
            int delta = _model.getRange()/10;
            if (event.getAction().equals("less")) {
                _model.setValue(_model.getValue() - delta);
            } else {
                _model.setValue(_model.getValue() + delta);
            }
        }
    };

    protected BoundedRangeModel _model;
    protected int _orient;

    protected BButton _less, _more;
    protected BBackground _well, _thumb;
}
