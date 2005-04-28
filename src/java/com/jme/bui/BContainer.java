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

import java.awt.Dimension;
import java.util.logging.Level;

import com.jme.bui.layout.BLayoutManager;

/**
 * A user interface element that is meant to contain other interface
 * elements.
 */
public class BContainer extends BComponent
{
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
    public void addChild (BComponent child)
    {
        addChild(child, null);
    }

    /**
     * Adds a child to this container with the specified layout
     * constraints.
     */
    public void addChild (BComponent child, Object constraints)
    {
        if (_layout != null) {
            _layout.addLayoutComponent(child, constraints);
        }
        attachChild(child);

        // if we're already part of the hierarchy, call wasAdded() on our
        // child; otherwise when our parent is added, everyone will have
        // wasAdded() called on them
        if (isAdded()) {
            child.wasAdded();
        }
    }

    // documentation inherited
    public void layout ()
    {
        if (_layout != null) {
            _layout.layoutContainer(this);
        }

        // now layout our children
        applyOperation(new ChildOp() {
            public void apply (BComponent child) {
                child.layout();
            }
        });
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
        for (int ii = 0, ll = getQuantity(); ii < ll; ii++) {
            BComponent child = (BComponent)getChild(ii);
            if ((hit = child.getHitComponent(mx, my)) != null) {
                return hit;
            }
        }
        return this;
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
     * Applies an operation to all of our children.
     */
    protected void applyOperation (ChildOp op)
    {
        for (int ii = 0, ll = getQuantity(); ii < ll; ii++) {
            BComponent child = (BComponent)getChild(ii);
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

    protected BLayoutManager _layout;
}
