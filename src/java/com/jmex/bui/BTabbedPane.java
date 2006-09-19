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

import com.jmex.bui.event.ActionEvent;
import com.jmex.bui.event.ActionListener;
import com.jmex.bui.layout.BorderLayout;
import com.jmex.bui.layout.GroupLayout;

/**
 * Displays one of a set of containers depending on which tab is selected.
 */
public class BTabbedPane extends BContainer
{
    public BTabbedPane ()
    {
        this(false);
    }

    public BTabbedPane (boolean hasCloseButton)
    {
        this(GroupLayout.LEFT, hasCloseButton);
    }

    public BTabbedPane (GroupLayout.Justification tabJustification)
    {
        this(tabJustification, false);
    }

    public BTabbedPane (
        GroupLayout.Justification tabJustification, boolean hasCloseButton)
    {
        super(new BorderLayout());

        _buttons = GroupLayout.makeHBox(tabJustification);
        add(_buttons, BorderLayout.NORTH);
        setHasCloseButton(hasCloseButton);
    }

    /**
     * Returns true if we display a close button after our tabs.
     */
    public boolean hasCloseButton ()
    {
        return _close != null;
    }

    /**
     * Controls whether or not to display a close button after our tabs.
     */
    public void setHasCloseButton (boolean hasCloseButton)
    {
        if (hasCloseButton) {
            if (_close == null) {
                _close = new BButton("", _closer, "close");
                _close.setStyleClass("tabbedpane_close");
                int n = _buttons.getComponentCount();
                if (n > 0) {
                    _buttons.add(n-1, _close);
                }
            }

        } else if (_close != null) {
            _buttons.remove(_close);
            _close = null;
        }
    }

    /**
     * Adds a tab to the pane using the specified tile.
     */
    public void addTab (String title, BComponent tab)
    {
        BToggleButton tbutton = new BToggleButton(
            title, String.valueOf(_tabs.size())) {
            protected void fireAction (long when, int modifiers) {
                if (!_selected) {
                    super.fireAction(when, modifiers);
                }
            }
        };
        tbutton.setStyleClass("tab");
        tbutton.addListener(_selector);

        if (_close != null) {
            // add tab before close button, which we may also need to add
            int n = _buttons.getComponentCount();
            if (n == 0) {
                _buttons.add(_close);
                n += 1;
            }
            _buttons.add(n-1, tbutton);
        } else {
            _buttons.add(tbutton);
        }
        _tabs.add(tab);

        // if we have no selected tab, select this one
        if (_selidx == -1) {
            selectTab(0);
        }
    }

    /**
     * Removes the specified tab.
     */
    public void removeTab (BComponent tab)
    {
        int idx = indexOfTab(tab);
        if (idx != -1) {
            removeTab(idx);
        } else {
            Log.log.warning("Requested to remove non-added tab " +
                "[pane=" + this + ", tab=" + tab + "].");
        }
    }

    /**
     * Removes the tab at the specified index.
     */
    public void removeTab (int tabidx)
    {
        removeTab(tabidx, 0, 0);
    }

    /**
     * Removes the tab at the specified index.
     */
    public void removeTab (int tabidx, long when, int modifiers)
    {
        _buttons.remove(_buttons.getComponent(tabidx));
        if (_buttons.getComponentCount() == 1) {
            // only the close button left, nuke it
            _buttons.remove(0);
        }
        BComponent tab = _tabs.remove(tabidx);

        // if we're removing the selected tab...
        if (_selidx == tabidx) {
            // remove the tab component
            remove(tab);
            _selidx = -1;

            // now display a new tab component
            if (tabidx < _tabs.size()) {
                selectTab(tabidx);
            } else {
                selectTab(tabidx - 1); // no-op if -1
            }

        } else if (_selidx > tabidx) {
            _selidx--;
        }

        // and let interested parties know what happened
        tabWasRemoved(tab);
    }

    /**
     * Removes all tabs.
     */
    public void removeAllTabs ()
    {
        if (_selidx != -1) {
            remove(_tabs.get(_selidx));
        }
        _selidx = -1;
        _buttons.removeAll();
        _tabs.clear();
    }

    /**
     * Returns the number of tabs in this pane.
     */
    public int getTabCount ()
    {
        return _tabs.size();
    }

    /**
     * Selects the specified tab.
     */
    public void selectTab (BComponent tab)
    {
        selectTab(indexOfTab(tab));
    }

    /**
     * Selects the tab with the specified index.
     */
    public void selectTab (int tabidx)
    {
        // no NOOPing
        if (tabidx == _selidx) {
            return;
        }

        // make sure the appropriate button is selected
        for (int ii = 0; ii < _tabs.size(); ii++) {
            getTabButton(ii).setSelected(ii == tabidx);
        }
        // remove the current tab and add the requested one
        if (_selidx != -1) {
            remove(_tabs.get(_selidx));
        }
        add(_tabs.get(tabidx), BorderLayout.CENTER);
        _selidx = tabidx;
    }

    /**
     * Returns the selected tab component.
     */
    public BComponent getSelectedTab ()
    {
        return (_selidx == -1) ? null : (BComponent)_tabs.get(_selidx);
    }

    /**
     * Returns the index of the selected tab.
     */
    public int getSelectedTabIndex ()
    {
        return _selidx;
    }

    /**
     * Returns a reference to the tab button for the given tab.
     */
    public BToggleButton getTabButton (BComponent tab)
    {
        int idx = indexOfTab(tab);
        return (idx == -1) ? null : getTabButton(idx);
    }

    /**
     * Returns a reference to the tab button at the given index.
     */
    public BToggleButton getTabButton (int idx)
    {
        return (BToggleButton)_buttons.getComponent(idx);
    }

    /**
     * Returns the index of the given tab.
     */
    public int indexOfTab (BComponent tab)
    {
        return _tabs.indexOf(tab);
    }

    /**
     * Called when a tab was removed.
     */
    protected void tabWasRemoved (BComponent tab)
    {
    }

    // documentation inherited
    protected String getDefaultStyleClass ()
    {
        return "tabbedpane";
    }

    protected ActionListener _selector = new ActionListener() {
        public void actionPerformed (ActionEvent event) {
            try {
                selectTab(Integer.parseInt(event.getAction()));
            } catch (Exception e) {
                Log.log.warning("Got weird action event " + event + ".");
            }
        }
    };

    protected ActionListener _closer = new ActionListener() {
        public void actionPerformed (ActionEvent event) {
            if (_selidx >= 0) {
                removeTab(_selidx);
            }
        }
    };

    protected BContainer _buttons;
    protected ArrayList<BComponent> _tabs = new ArrayList<BComponent>();
    protected int _selidx = -1;

    /** A reference to our close button, if we use one, or null otherwise */
    protected BButton _close;
}
