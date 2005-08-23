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
import java.util.logging.Level;

import com.jmex.bui.layout.BLayoutManager;
import com.jmex.bui.util.Dimension;
import com.jme.renderer.Renderer;

/**
 * A user interface element that is meant to contain other interface
 * elements.
 */
public class BContainer extends BComponent
{
    /**
     * Creates a container with no layout manager. One should subsequently
     * be set via a call to {@link #setLayoutManager}.
     */
    public BContainer ()
    {
    }

    /**
     * Creates a container with the supplied layout manager.
     */
    public BContainer (BLayoutManager layout)
    {
        setLayoutManager(layout);
    }

    /**
     * Configures this container with an entity that will set the size and
     * position of its children.
     */
    public void setLayoutManager (BLayoutManager layout)
    {
        _layout = layout;
    }

    /**
     * Adds a child to this container. This should be used rather than
     * calling {@link #attachChild} directly.
     */
    public void add (BComponent child)
    {
        add(child, null);
    }

    /**
     * Adds a child to this container with the specified layout
     * constraints.
     */
    public void add (BComponent child, Object constraints)
    {
        if (_layout != null) {
            _layout.addLayoutComponent(child, constraints);
        }
        _children.add(child);
        child.setParent(this);

        // if we're already part of the hierarchy, call wasAdded() on our
        // child; otherwise when our parent is added, everyone will have
        // wasAdded() called on them
        if (isAdded()) {
            child.wasAdded();
        }

        // we need to be relayed out
        invalidate();
    }

    /**
     * Removes the specified child from this container.
     */
    public void remove (BComponent child)
    {
        if (!_children.remove(child)) {
            // if the component was not our child, stop now
            return;
        }
        if (_layout != null) {
            _layout.removeLayoutComponent(child);
        }
        child.setParent(null);

        // if we're part of the hierarchy we call wasRemoved() on the
        // child now (which will be propagated to all of its children)
        if (isAdded()) {
            child.wasRemoved();
        }
    }

    /**
     * Returns the number of components contained in this container.
     */
    public int getComponentCount ()
    {
        return _children.size();
    }

    /**
     * Returns the <code>index</code>th component from this container.
     */
    public BComponent getComponent (int index)
    {
        return (BComponent)_children.get(index);
    }

    /**
     * Removes all children of this container.
     */
    public void removeAll ()
    {
        for (int ii = getComponentCount() - 1; ii > 0; ii--) {
            remove(getComponent(ii));
        }
    }

    // documentation inherited
    public BComponent getHitComponent (int mx, int my)
    {
        // if we're not within our bounds, we don't need to check our children
        if (super.getHitComponent(mx, my) != this) {
            return null;
        }

        // translate the coordinate into our children's coordinates
        mx -= _x;
        my -= _y;

        BComponent hit = null;
        for (int ii = 0, ll = getComponentCount(); ii < ll; ii++) {
            BComponent child = getComponent(ii);
            if ((hit = child.getHitComponent(mx, my)) != null) {
                return hit;
            }
        }
        return this;
    }

    // documentation inherited
    public void validate ()
    {
        if (!_valid) {
            // lay ourselves out
            layout();

            // now validate our children
            applyOperation(new ChildOp() {
                public void apply (BComponent child) {
                    child.validate();
                }
            });

            // finally mark ourselves as valid
            _valid = true;
        }
    }

    // documentation inherited
    protected void layout ()
    {
        if (_layout != null) {
            _layout.layoutContainer(this);
        }
    }

    // documentation inherited
    protected void renderComponent (Renderer renderer)
    {
        super.renderComponent(renderer);

        // render our children
        for (int ii = 0, ll = getComponentCount(); ii < ll; ii++) {
            getComponent(ii).render(renderer);
        }
    }

    // documentation inherited
    protected Dimension computePreferredSize ()
    {
        if (_layout != null) {
            return _layout.computePreferredSize(this);
        } else {
            return super.computePreferredSize();
        }
    }

    // documentation inherited
    protected void wasAdded ()
    {
        super.wasAdded();

        // call wasAdded() on all of our existing children; if they are
        // added later (after we are added), they will automatically have
        // wasAdded() called on them at that time
        applyOperation(new ChildOp() {
            public void apply (BComponent child) {
                child.wasAdded();
            }
        });
    }

    // documentation inherited
    protected void wasRemoved ()
    {
        super.wasRemoved();

        // call wasRemoved() on all of our children
        applyOperation(new ChildOp() {
            public void apply (BComponent child) {
                child.wasRemoved();
            }
        });
    }

    /**
     * Returns the next component that should receive focus in this
     * container given the current focus owner. If the supplied current
     * focus owner is null, the container should return its first
     * focusable component. If the container has no focusable components
     * following the current focus, it should call {@link #getNextFocus()}
     * to search further up the hierarchy.
     */
    protected BComponent getNextFocus (BComponent current)
    {
        boolean foundCurrent = (current == null);
        for (int ii = 0, ll = getComponentCount(); ii < ll; ii++) {
            BComponent child = getComponent(ii);
            if (!foundCurrent) {
                if (child == current) {
                    foundCurrent = true;
                }
                continue;
            }
            if (child.acceptsFocus()) {
                return child;
            }
        }
        return getNextFocus();
    }

    /**
     * Returns the previous component that should receive focus in this
     * container given the current focus owner. If the supplied current
     * focus owner is null, the container should return its last focusable
     * component. If the container has no focusable components before the
     * current focus, it should call {@link #getPreviousFocus()} to search
     * further up the hierarchy.
     */
    protected BComponent getPreviousFocus (BComponent current)
    {
        boolean foundCurrent = (current == null);
        for (int ii = getComponentCount()-1; ii >= 0; ii--) {
            BComponent child = getComponent(ii);
            if (!foundCurrent) {
                if (child == current) {
                    foundCurrent = true;
                }
                continue;
            }
            if (child.acceptsFocus()) {
                return child;
            }
        }
        return getPreviousFocus();
    }

    /**
     * Applies an operation to all of our children.
     */
    protected void applyOperation (ChildOp op)
    {
        for (int ii = 0, ll = getComponentCount(); ii < ll; ii++) {
            BComponent child = getComponent(ii);
            try {
                op.apply(child);
            } catch (Exception e) {
                Log.log.log(Level.WARNING, "Child operation choked" +
                            "[op=" + op + ", child=" + child + "].", e);
            }
        }
    }

    /** Used in {@link #wasAdded} and {@link #wasRemoved}. */
    protected static interface ChildOp
    {
        public void apply (BComponent child);
    }

    protected ArrayList _children = new ArrayList();
    protected BLayoutManager _layout;
}
