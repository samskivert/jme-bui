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

package com.jme.bui.layout;

import java.awt.Dimension;

import com.jme.bui.BComponent;
import com.jme.bui.BContainer;
import com.jme.bui.Log;

/**
 * Lays out components in a simple grid arrangement, wherein the width and
 * height of each column and row is defined by the widest preferred width
 * and height of any component in that column and row.
 */
public class TableLayout extends BLayoutManager
{
    /**
     * Creates a table layout with the specified number of columns.
     */
    public TableLayout (int columns)
    {
        this(columns, 0, 0);
    }

    /**
     * Creates a table layout with the specified number of columns and the
     * specifeid gap between rows and columns.
     */
    public TableLayout (int columns, int rowgap, int colgap)
    {
        _columnWidths = new int[columns];
        _rowgap = rowgap;
        _colgap = colgap;
    }

    // documentation inherited
    public Dimension computePreferredSize (BContainer target)
    {
        computeMetrics(target);
        int cx = (_columnWidths.length-1) * _colgap;
        int rx = (computeRows(target)-1) * _rowgap;
        return new Dimension(sum(_columnWidths) + cx, sum(_rowHeights) + rx);
    }

    // documentation inherited
    public void layoutContainer (BContainer target)
    {
        computeMetrics(target);

        int row = 0, col = 0, x = 0, y = target.getHeight();
        for (int ii = 0, ll = target.getQuantity(); ii < ll; ii++) {
            BComponent child = (BComponent)target.getChild(ii);
            child.setBounds(x, y - _rowHeights[row],
                            _columnWidths[col], _rowHeights[row]);
            x += (_columnWidths[col] + _colgap);
            if (++col == _columnWidths.length) {
                y -= (_rowHeights[row] + _rowgap);
                row++;
                col = 0;
                x = 0;
            }
        }
    }

    protected void computeMetrics (BContainer target)
    {
        int rows = computeRows(target);
        if (_rowHeights == null || _rowHeights.length != rows) {
            _rowHeights = new int[rows];
        }

        int row = 0, col = 0;
        for (int ii = 0, ll = target.getQuantity(); ii < ll; ii++) {
            BComponent child = (BComponent)target.getChild(ii);
            Dimension psize = child.getPreferredSize();
            if (psize.height > _rowHeights[row]) {
                _rowHeights[row] = psize.height;
            }
            if (psize.width > _columnWidths[col]) {
                _columnWidths[col] = psize.width;
            }
            if (++col == _columnWidths.length) {
                col = 0;
                row++;
            }
        }
    }

    protected int computeRows (BContainer target)
    {
        int ccount = target.getQuantity();
        int rows = ccount / _columnWidths.length;
        if (ccount % _columnWidths.length != 0) {
            rows++;
        }
        return rows;
    }

    protected int sum (int[] values)
    {
        int total = 0;
        for (int ii = 0; ii < values.length; ii++) {
            total += values[ii];
        }
        return total;
    }

    protected int _rowgap, _colgap;
    protected int[] _columnWidths;
    protected int[] _rowHeights;
}
