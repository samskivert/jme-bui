//
// $Id$
//
// BUI - a user interface library for the JME 3D engine
// Copyright (C) 2006, Pär Winzell, All Rights Reserved
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

import org.lwjgl.opengl.GL11;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

import com.jme.renderer.Renderer;
import com.jmex.bui.event.MouseWheelListener;
import com.jmex.bui.event.ChangeListener;
import com.jmex.bui.event.ChangeEvent;
import com.jmex.bui.layout.BorderLayout;
import com.jmex.bui.layout.BLayoutManager;
import com.jmex.bui.util.Dimension;
import com.jmex.bui.util.Insets;
import com.jmex.bui.util.Rectangle;
import com.jmex.bui.layout.GroupLayout;

/**
 * Provides a scrollable, lazily instantiated component view of values
 */
public abstract class BScrollingList<V, C extends BComponent> extends BContainer
    implements ChangeListener
{
    /**
     * Instantiates an empty {@link BScrollingList}.
     */
    public BScrollingList ()
    {
        this(new ArrayList<V>());
    }

    /**
     * Instantiates a {@link BScrollingList} with an initial value collection.
     */
    public BScrollingList (Collection<V> values)
    {
        super(new BorderLayout(0, 0));

        _values = new ArrayList<V>(values);
        _lastBottom = 0;

        int last = _values.size()-1;
        _model = new BoundedSnappingRangeModel(0, last-EXTENT, EXTENT, last, 1);
        _model.addChangeListener(this);

        add(_vport = new BViewport(
                GroupLayout.makeVert(GroupLayout.NONE, GroupLayout.BOTTOM,
                                     GroupLayout.STRETCH)),
            BorderLayout.CENTER);
        add(_vbar = new BScrollBar(BScrollBar.VERTICAL, _model),
            BorderLayout.EAST);
    }

    /**
     * Appends a value to our list, possibly scrolling our view to display it.
     */
    public void addValue (V value, boolean snapToBottom)
    {
        addValue(_values.size(), value, snapToBottom);
    }

    /**
     * Inserts a value into our list at the specified position.
     */
    public void addValue (int index, V value)
    {
        addValue(index, value, false);
    }

    /**
     * Clears all the current values and any related components.
     */
    public void removeValues ()
    {
        _values.clear();
        if (isAdded()) {
            updateModel(true);
        }
    }

    // from interface ChangeListener
    public void stateChanged (ChangeEvent event)
    {
        _vport.invalidate();
    }

    @Override // from BComponent
    protected void wasAdded ()
    {
        super.wasAdded();
        addListener(_wheelListener = _model.createWheelListener());
        updateModel(true);
    }

    @Override // from BComponent
    protected void wasRemoved ()
    {
        super.wasRemoved();
        if (_wheelListener != null) {
            removeListener(_wheelListener);
            _wheelListener = null;
        }
    }

    /**
     * Must be implemented by subclasses to instantiate the correct BComponent
     * subclass for a given list value.
     */
    protected abstract C createComponent (V value);

    /**
     * Adds a value to the list and snaps to the bottom of the list if desired.
     */
    protected void addValue (int index, V value, boolean snap)
    {
        _values.add(index, value);
        if (isAdded()) {
            updateModel(snap);
        }
    }

    /**
     * Reconfigures the model whenever underlying data or scrollbar changes.
     */
    protected void updateModel (boolean snap)
    {
        int last = _values.size()-1;
        int pos = snap ? Math.max(0, last - EXTENT) : _model.getValue();
        _model.setRange(0, pos, EXTENT, last);
    }

    /** Does all the heavy lifting for the {@link BScrollingList}. */
    protected class BViewport extends BContainer
    {
        public BViewport (BLayoutManager layout)
        {
            super(layout);
        }

        /**
         * Returns a reference to the vertical scroll bar.
         */
        public BScrollBar getVerticalScrollBar ()
        {
            return _vbar;
        }

        @Override // from BComponent
        public void invalidate ()
        {
            // if we're not attached, don't worry about it
            BWindow window;
            BRootNode root;
            if (!_valid || (window = getWindow()) == null ||
                (root = window.getRootNode()) == null) {
                return;
            }

            _valid = false;
            root.rootInvalidated(this);
        }

        @Override // from BComponent
        public void layout ()
        {
            Insets insets = getInsets();
            int twidth = getWidth() - insets.getHorizontal();
            int theight = getHeight() - insets.getVertical();

            int bottomIx = _model.getValue() + _model.getExtent();
            if (bottomIx < _lastBottom) {
                // the thumb has moved up; remove entries at the end
                int cnt = getComponentCount();
                while (bottomIx < _lastBottom && cnt > 0) {
                    remove(cnt-1);
                    _lastBottom --;
                    cnt --;
                }

            } else if (bottomIx > _lastBottom) {
                // if the thumb has moved down, we need to append
                int appendedHeight = 0;
                int appendPos = getComponentCount();

                int ix = bottomIx;
                for (; ix > _lastBottom && appendedHeight < theight; ix--) {
                    BComponent child = createComponent(_values.get(ix));
                    add(appendPos, child);
                    appendedHeight += child.getPreferredSize(twidth, 0).height;
                }

                if (ix > _lastBottom) {
                    // there's a gap between the new and old range, wipe out old
                    while (appendPos > 0) {
                        remove(0);
                        appendPos --;
                    }
                }
            }
            _lastBottom = bottomIx;

            // now see if we're underfull or overfull
            int cheight = 0;
            int ix = getComponentCount() - 1;
            for (; ix >= 0 && cheight < theight; ix--) {
                cheight += getComponent(ix).getPreferredSize(twidth, 0).height;
            } 

            if (ix >= 0) {
                // we filled up early, remove superluous entries
                do {
                    remove(ix--);
                } while (ix >= 0);

            } else if (_values.size() > 0) {
                // we may need to fill up with more entries
                for (int top = bottomIx - getComponentCount();
                     cheight < theight && top >= 0; top--) {
                    BComponent child = createComponent(_values.get(top));
                    add(0, child);
                    cheight += child.getPreferredSize(twidth, 0).height;
                }
            }

            super.layout();
        }

        @Override // from BComponent
        protected void renderComponent (Renderer renderer)
        {
            Insets insets = getInsets();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(getAbsoluteX() + insets.left,
                           getAbsoluteY() + insets.bottom,
                           _width - insets.getHorizontal(),
                           _height - insets.getVertical());
            try {
                // render our children
                for (int ii = 0, ll = getComponentCount(); ii < ll; ii++) {
                    getComponent(ii).render(renderer);
                }
            } finally {
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            }
        }
    }

    protected MouseWheelListener _wheelListener;
    protected BoundedRangeModel _model;
    protected List<V> _values;
    protected BViewport _vport;
    protected BScrollBar _vbar;
    protected int _lastBottom;

    protected static final int EXTENT = 2;
}
