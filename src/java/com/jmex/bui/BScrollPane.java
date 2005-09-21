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

/**
 * Provides a scrollable clipped view on a sub-heirarchy of components.
 */
public class BScrollPane extends BContainer
{
    public BScrollPane (BComponent child)
    {
        super(new BorderLayout(0, 0));

        BViewport vport = new BViewport(child);
        add(vport, BorderLayout.CENTER);
        add(new BScrollBar(BScrollBar.VERTICAL, vport.getModel()),
            BorderLayout.EAST);
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
            if (getParent() == null) {
                return;
            }

            // otherwise layout our target component
            layout();
        }

        // documentation inherited
        public void layout ()
        {
            // resize our target component to its preferred size
            Dimension d = _target.getPreferredSize();
            if (_target.getWidth() != d.width ||
                _target.getHeight() != d.height) {
                _target.setBounds(0, 0, d.width, d.height);
                // this will trigger a call up to invalidate() and we'll
                // reenter this same method with the target at its preferred
                // size
                return;
            }

            // lay out our target component
            _target.layout();

            // and recompute our scrollbar range
            _model.setRange(0, _model.getValue(), getHeight(), d.height);
        }

        // documentation inherited
        public Dimension getPreferredSize ()
        {
            return _target.getPreferredSize();
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
            if ((mx < _x) || (my < _y) ||
                (mx >= _x + _width) || (my > _y + _height)) {
                return null;
            }

            // translate the coordinate into our children's coordinates
            mx -= _x;
            my -= (_y + getYOffset());

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
        protected void renderComponent (Renderer renderer)
        {
            // translate by our offset into the viewport
            int offset = getYOffset();
            GL11.glTranslatef(0, offset, 0);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(getAbsoluteX(), getAbsoluteY()-offset,
                           _width, _height);
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
}
