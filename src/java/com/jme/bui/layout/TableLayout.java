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

import com.jme.bui.BComponent;
import com.jme.bui.BContainer;
import com.jme.bui.Log;
import com.jme.bui.util.Dimension;
import com.jme.bui.util.Insets;

/**
 * Lays out components in a simple grid arrangement, wherein the width and
 * height of each column and row is defined by the widest preferred width
 * and height of any component in that column and row.
 */
public class TableLayout extends BLayoutManager
{
    /** An enumeration class represnting layout modes. */
    public static class Mode
    {
    }

    /** Left justifies the table contents within the container. */
    public static final Mode LEFT = new Mode();

    /** Centers the table contents within the container. */
    public static final Mode CENTER = new Mode();

    /** Right justifies the table contents within the container. */
    public static final Mode RIGHT = new Mode();

    /** Divides the column space among the columns in proportion to their
     * preferred size. */
    public static final Mode STRETCH = new Mode();

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
        this(columns, rowgap, colgap, LEFT);
    }

    /**
     * Creates a table layout with the specified number of columns and the
     * specifeid gap between rows and columns.
     */
    public TableLayout (int columns, int rowgap, int colgap, Mode mode)
    {
        _columnWidths = new int[columns];
        _rowgap = rowgap;
        _colgap = colgap;
        _mode = mode;
    }

    // documentation inherited
    public Dimension computePreferredSize (BContainer target)
    {
        computeMetrics(target);
        Insets insets = target.getInsets();
        int cx = (_columnWidths.length-1) * _colgap + insets.getHorizontal();
        int rx = (computeRows(target)-1) * _rowgap + insets.getVertical();
        return new Dimension(sum(_columnWidths) + cx, sum(_rowHeights) + rx);
    }

    // documentation inherited
    public void layoutContainer (BContainer target)
    {
        computeMetrics(target);

        Insets insets = target.getInsets();
        int x = insets.left, y = target.getHeight() - insets.top;
        int row = 0, col = 0;
        for (int ii = 0, ll = target.getComponentCount(); ii < ll; ii++) {
            BComponent child = target.getComponent(ii);
            child.setBounds(x, y - _rowHeights[row],
                            _columnWidths[col], _rowHeights[row]);
            x += (_columnWidths[col] + _colgap);
            if (++col == _columnWidths.length) {
                y -= (_rowHeights[row] + _rowgap);
                row++;
                col = 0;
                x = insets.left;
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
        for (int ii = 0, ll = target.getComponentCount(); ii < ll; ii++) {
            BComponent child = target.getComponent(ii);
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

        // if we are stretching, adjust the column widths accordingly
        if (_mode == STRETCH) {
            int naturalWidth = sum(_columnWidths);
            int avail = target.getWidth() - target.getInsets().getHorizontal() -
                naturalWidth - (_colgap * (_columnWidths.length-1));
            int used = 0;
            for (int ii = 0; ii < _columnWidths.length; ii++) {
                int adjust = _columnWidths[ii] * avail / naturalWidth;
                _columnWidths[ii] += adjust;
                used += adjust;
            }
            // add any rounding error to the first column
            if (_columnWidths.length > 0) {
                _columnWidths[0] += (avail - used);
            }
        }
    }

    protected int computeRows (BContainer target)
    {
        int ccount = target.getComponentCount();
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

    protected Mode _mode;
    protected int _rowgap, _colgap;
    protected int[] _columnWidths;
    protected int[] _rowHeights;
}
