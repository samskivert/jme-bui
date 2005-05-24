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

import com.jme.bui.event.ActionEvent;
import com.jme.bui.event.ActionListener;
import com.jme.bui.event.BEvent;
import com.jme.bui.event.MouseEvent;

/**
 * Displays a selected value and allows that value to be changed by
 * selecting from a popup menu.
 */
public class BComboBox extends BLabel
{
    /**
     * Creates a combo box with the supplied set of items. The result of
     * {@link Object#toString} for each item will be displayed in the
     * list.
     */
    public BComboBox (Object[] items)
    {
        super("");
        for (int ii = 0; ii < items.length; ii++) {
            addItem(items[ii]);
        }
    }

    /**
     * Appends an item to our list of items. The result of {@link
     * Object#toString} for the item will be displayed in the list.
     */
    public void addItem (Object item)
    {
        addItem(_items.size(), item);
    }

    /**
     * Inserts an item into our list of items at the specified position
     * (zero being before all other items and so forth).  The result of
     * {@link Object#toString} for the item will be displayed in the list.
     */
    public void addItem (int index, Object item)
    {
        boolean select = (_items.size() == 0);
        _items.add(index, new ComboMenuItem(item));
        if (select) {
            selectItem(index);
        }

        // clear out our cached menu if we have one
        if (_menu != null) {
            _menu.removeAll();
            _menu = null;
        }
    }

    /**
     * Returns the index of the selected item or -1 if no item is
     * selected.
     */
    public int getSelectedIndex ()
    {
        return _selidx;
    }

    /**
     * Returns the selected item or null if no item is selected.
     */
    public Object getSelectedItem ()
    {
        return _selidx == -1 ? null : ((ComboMenuItem)_items.get(_selidx)).item;
    }

    /**
     * Selects the item with the specified index.
     */
    public void selectItem (int index)
    {
        _selidx = index;
        Object item = getSelectedItem();
        setText(item == null ? "" : item.toString());
    }

//     /**
//      * Selects the item with the specified index.
//      */
//     public void selectItem (Object item)
//     {
//         _selidx = index;
//         setText(getSelectedItem().toString());
//     }

    // documentation inherited
    public void dispatchEvent (BEvent event)
    {
        super.dispatchEvent(event);

        if (event instanceof MouseEvent) {
            MouseEvent mev = (MouseEvent)event;
            switch (mev.getType()) {
            case MouseEvent.MOUSE_PRESSED:
                if (_menu == null) {
                    Log.log.info("New pop! " + event);
                    _menu = new BPopupMenu(getWindow());
                    _menu.addListener(_listener);
                    for (int ii = 0; ii < _items.size(); ii++) {
                        _menu.addMenuItem((ComboMenuItem)_items.get(ii));
                    }
                } else {
                    Log.log.info("Old pop! " + event);
                }
                _menu.popup(getAbsoluteX(), getAbsoluteY(), false);
                break;

            case MouseEvent.MOUSE_RELEASED:
                break;
            }
        }
    }

    protected class ComboMenuItem extends BMenuItem
    {
        public Object item;

        public ComboMenuItem (Object item)
        {
            super(item.toString(), "select");
            this.item = item;
        }
    }

    protected ActionListener _listener = new ActionListener() {
        public void actionPerformed (ActionEvent event) {
            selectItem(_items.indexOf(event.getSource()));
        }
    };

    protected int _selidx = -1;
    protected ArrayList _items = new ArrayList();
    protected BPopupMenu _menu;
}
