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
        this(GroupLayout.LEFT);
    }

    public BTabbedPane (GroupLayout.Justification tabJustification)
    {
        super(new BorderLayout());

        _buttons = GroupLayout.makeHBox(tabJustification);
        add(_buttons, BorderLayout.NORTH);
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
        tbutton.addListener(_selector);
        _buttons.add(tbutton);
        _tabs.add(tab);

        // if we have no selected tab, select this one
        if (_selidx == -1) {
            selectTab(0);
        }
    }

    /**
     * Removes the tab at the specified index.
     */
    public void removeTab (int tabidx)
    {
        _buttons.remove(_buttons.getComponent(tabidx));
        _tabs.remove(tabidx);
        
        // update the selected index
        if (_selidx == tabidx) {
            _selidx = -1;
            if (tabidx < _tabs.size()) {
                selectTab(tabidx);
                
            } else {
                selectTab(tabidx - 1); // no-op if -1
            }
            
        } else if (_selidx > tabidx) {
            _selidx--;
        }
    }
    
    /**
     * Removes all tabs.
     */
    public void removeAllTabs ()
    {
        if (_selidx != -1) {
            remove((BComponent)_tabs.get(_selidx));
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
            BToggleButton btn = (BToggleButton)_buttons.getComponent(ii);
            btn.setSelected(ii == tabidx);
        }
        // remove the current tab and add the requested one
        if (_selidx != -1) {
            remove((BComponent)_tabs.get(_selidx));
        }
        add((BComponent)_tabs.get(tabidx), BorderLayout.CENTER);
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

    protected BContainer _buttons;
    protected ArrayList _tabs = new ArrayList();
    protected int _selidx = -1;
}
