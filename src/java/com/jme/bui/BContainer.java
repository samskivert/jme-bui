//
// $Id$
//
// BUI - a user interface library for the JME 3D engine
// Copyright (C) 2005, Michael Bayne, All Rights Reserved
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// Redistributions of source code must retain the above copyright notice, this
// list of conditions and the following disclaimer.
//
// Redistributions in binary form must reproduce the above copyright notice,
// this list of conditions and the following disclaimer in the documentation
// and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

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
