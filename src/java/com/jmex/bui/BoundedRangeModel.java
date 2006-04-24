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

import com.jmex.bui.event.ChangeEvent;
import com.jmex.bui.event.ChangeListener;

/**
 * Defines the model used by the {@link BScrollBar} to communicate with
 * other components and external entities that wish to be manipulated by a
 * scroll bar.
 *
 * <p> A bounded range model has a minimum and maximum value, a current
 * value and an extent. These are easily visualized by showing how they
 * control a scroll bar:
 *
 * <pre>
 * +-------------------------------------------------------------------+
 * |        +---------------------------------------+                  |
 * |        |                                       |                  |
 * |        +---------------------------------------+                  |
 * +-------------------------------------------------------------------+
 * min      value                        value+extent                max
 * </pre>
 */
public class BoundedRangeModel
{
    /**
     * Creates a bounded range model with the specified minimum value,
     * current value, extent and maximum value.
     */
    public BoundedRangeModel (int min, int value, int extent, int max)
    {
        _min = min;
        _value = value;
        _extent = extent;
        _max = max;
    }

    /**
     * Adds a listener to this model.
     */
    public void addChangeListener (ChangeListener listener)
    {
        _listeners.add(listener);
    }

    /**
     * Removes the specified listener from the model.
     */
    public void removeChangeListener (ChangeListener listener)
    {
        _listeners.remove(listener);
    }

    /**
     * Returns the minimum value this model will allow for its value.
     */
    public int getMinimum ()
    {
        return _min;
    }

    /**
     * Returns the maximum value this model will allow for <code>value +
     * extent</code>.
     */
    public int getMaximum ()
    {
        return _max;
    }

    /**
     * Returns the range of this model (the maximum minus the minimum).
     */
    public int getRange ()
    {
        return _max - _min;
    }

    /**
     * Returns the current value of the model.
     */
    public int getValue ()
    {
        return _value;
    }

    /**
     * Returns the current extent of the model.
     */
    public int getExtent ()
    {
        return _extent;
    }

    /**
     * Configures the minimum value of this model, adjusting the value,
     * extent and maximum as necessary to maintain the consistency of the
     * model.
     */
    public void setMinimum (int minimum)
    {
        int max = Math.max(minimum, _max);
        int val = Math.max(minimum, _value);
        setRange(minimum, val, Math.max(max - val, _extent), max);
    }

    /**
     * Configures the maximum value of this model, adjusting the value,
     * extent and minimum as necessary to maintain the consistency of the
     * model.
     */
    public void setMaximum (int maximum)
    {
        int min = Math.min(maximum, _min);
        int ext = Math.min(maximum - min, _extent);
        setRange(min, Math.max(maximum - ext, _value), ext, maximum);
    }

    /**
     * Configures the value of this model. The new value will be adjusted
     * if it does not fall within the range of <code>min <= value <= max -
     * extent<code>.
     */
    public void setValue (int value)
    {
        int val = Math.min(_max - _extent, Math.max(_min, value));
        setRange(_min, val, _extent, _max);
    }

    /**
     * Configures the extent of this model. The new value will be adjusted
     * if it does not fall within the range of <code>0 <= extent <= max -
     * value<code>.
     */
    public void setExtent (int extent)
    {
        int ext = Math.min(_max - _value, Math.max(0, extent));
        setRange(_min, _value, ext, _max);
    }

    /**
     * Configures this model with a new minimum, maximum, current value
     * and extent.
     *
     * @return true if the range was modified, false if the values were
     * already set to the requested values.
     */
    public boolean setRange (int min, int value, int extent, int max)
    {
        min = Math.min(min, max);
        max = Math.max(max, value);
        min = Math.min(min, value);
        extent = Math.max(Math.min(extent, max - value), 0);

        // if anything has changed
        if (min != _min || _value != value ||
            _extent != extent || _max != max) {
            // update our values
            _min = min;
            _value = value;
            _extent = extent;
            _max = max;

            // and notify our listeners
            for (int ii = 0, ll = _listeners.size(); ii < ll; ii++) {
                ((ChangeListener)_listeners.get(ii)).stateChanged(_event);
            }

            return true;
        }
        return false;
    }

    protected int _min, _max;
    protected int _value, _extent;
    protected ArrayList _listeners = new ArrayList();
    protected ChangeEvent _event = new ChangeEvent(this);
}
