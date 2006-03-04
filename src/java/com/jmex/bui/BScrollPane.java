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

import org.lwjgl.opengl.GL11;

import com.jme.renderer.Renderer;
import com.jmex.bui.layout.BorderLayout;
import com.jmex.bui.util.Dimension;
import com.jmex.bui.util.Insets;

/**
 * Provides a scrollable clipped view on a sub-heirarchy of components.
 */
public class BScrollPane extends BContainer
{
    public BScrollPane (BComponent child)
    {
        super(new BorderLayout(0, 0));

        add(_vport = new BViewport(child), BorderLayout.CENTER);
        add(_vbar = new BScrollBar(BScrollBar.VERTICAL, _vport.getModel()),
            BorderLayout.EAST);
    }

    /**
     * Returns a reference to the child of this scroll pane.
     */
    public BComponent getChild ()
    {
        return _vport.getTarget();
    }
    
    /**
     * Returns a reference to the vertical scroll bar.
     */
    public BScrollBar getVerticalScrollBar ()
    {
        return _vbar;
    }
    
    /** Does all the heavy lifting for the {@link BScrollPane}. TODO: support
     * horizontal scrolling as well. */
    protected static class BViewport extends BContainer
    {
        public BViewport (BComponent target)
        {
            _model = new BoundedRangeModel(0, 0, 10, 10);
            add(_target = target);
        }

        /**
         * Returns a reference to the target of this viewport.
         */
        public BComponent getTarget ()
        {
            return _target;
        }
        
        /**
         * Returns the range model defined by this viewport's size and the
         * preferred size of its target component.
         */
        public BoundedRangeModel getModel ()
        {
            return _model;
        }
        
        // documentation inherited
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

        // documentation inherited
        public void layout ()
        {
            // resize our target component to the larger of our size and its
            // preferred size
            Insets insets = getInsets();
            int twidth = getWidth() - insets.getHorizontal();
            Dimension d = _target.getPreferredSize(twidth, -1);
            d.width = twidth;
            d.height = Math.max(d.height, getHeight() - insets.getVertical());
            if (_target.getWidth() != d.width ||
                _target.getHeight() != d.height) {
                _target.setBounds(insets.left, insets.bottom, d.width,
                    d.height);
            }

            // lay out our target component
            _target.layout();

            // and recompute our scrollbar range
            _model.setRange(0, _model.getValue(),
                getHeight() - insets.getVertical(), d.height);
        }

//         // documentation inherited
//         public int getAbsoluteX ()
//         {
//             return super.getAbsoluteX();
//         }

        // documentation inherited
        public int getAbsoluteY ()
        {
            return super.getAbsoluteY() + getYOffset();
        }

        // documentation inherited
        public BComponent getHitComponent (int mx, int my)
        {
            // if we're not within our bounds, we needn't check our target
            Insets insets = getInsets();
            if ((mx < _x + insets.left) || (my < _y + insets.bottom) ||
                (mx >= _x + _width - insets.right) ||
                (my > _y + _height - insets.top)) {
                return null;
            }

            // translate the coordinate into our children's coordinates
            mx -= (_x + insets.left);
            my -= (_y + insets.bottom + getYOffset());

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
        protected Dimension computePreferredSize (int whint, int hhint)
        {
            return new Dimension(_target.getPreferredSize(whint, hhint));
        }
        
        // documentation inherited
        protected void renderComponent (Renderer renderer)
        {
            // translate by our offset into the viewport
            Insets insets = getInsets();
            int offset = getYOffset();
            GL11.glTranslatef(0, offset, 0);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(getAbsoluteX() + insets.left,
                           (getAbsoluteY() + insets.bottom) - offset,
                           _width - insets.getHorizontal(),
                           _height - insets.getVertical());
            try {
                // and then render our target component
                _target.render(renderer);
            } finally {
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                GL11.glTranslatef(0, -offset, 0);
            }
        }

        protected final int getYOffset ()
        {
            return _model.getValue() -
                (_model.getMaximum() - _model.getExtent());
        }

        protected BoundedRangeModel _model;
        protected BComponent _target;
    }
    
    protected BViewport _vport;
    protected BScrollBar _vbar;
}
