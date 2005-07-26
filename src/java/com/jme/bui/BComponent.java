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
import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import com.jme.bui.background.BBackground;
import com.jme.bui.border.BBorder;
import com.jme.bui.event.BEvent;
import com.jme.bui.event.ComponentListener;
import com.jme.bui.event.KeyEvent;
import com.jme.bui.util.Dimension;
import com.jme.bui.util.Insets;
import com.jme.bui.util.Rectangle;
import com.jme.input.KeyInput;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;

/**
 * The basic entity in the BUI user interface system. A hierarchy of
 * components and component derivations make up a user interface.
 */
public class BComponent
{
    /**
     * Configures this component with a look and feel that will be used to
     * render it and all of its children (unless those children are
     * configured themselves with a custom look and feel).
     */
    public void setLookAndFeel (BLookAndFeel lnf)
    {
        _lnf = lnf;
    }

    /**
     * Informs this component of its parent in the interface heirarchy.
     */
    public void setParent (BComponent parent)
    {
        _parent = parent;
    }

    /**
     * Returns the parent of this component in the interface hierarchy.
     */
    public BComponent getParent ()
    {
        return _parent;
    }

    /**
     * Returns the preferred size of this component.
     */
    public Dimension getPreferredSize ()
    {
        Dimension ps = _preferredSize;
        if (ps == null) {
            ps = computePreferredSize();
            ps.width += getInsets().getHorizontal();
            ps.height += getInsets().getVertical();
        }
        return ps;
    }

    /**
     * Configures the preferred size of this component. This will override
     * any information provided by derived classes that have opinions
     * about their preferred size.
     */
    public void setPreferredSize (Dimension preferredSize)
    {
        _preferredSize = preferredSize;
    }

    /** Returns the x coordinate of this component. */
    public int getX ()
    {
        return _x;
    }

    /** Returns the y coordinate of this component. */
    public int getY ()
    {
        return _y;
    }

    /** Returns the width of this component. */
    public int getWidth ()
    {
        return _width;
    }

    /** Returns the height of this component. */
    public int getHeight ()
    {
        return _height;
    }

    /** Returns the x position of this component in absolute screen
     * coordinates. */
    public int getAbsoluteX ()
    {
        return _x + ((_parent == null) ? 0 : _parent.getAbsoluteX());
    }

    /** Returns the y position of this component in absolute screen
     * coordinates. */
    public int getAbsoluteY ()
    {
        return _y + ((_parent == null) ? 0 : _parent.getAbsoluteY());
    }

    /** Returns the bounds of this component in a new rectangle. */
    public Rectangle getBounds ()
    {
        return new Rectangle(_x, _y, _width, _height);
    }

    /**
     * Returns the insets configured on this component. If a component has
     * a border, that border will provide insets for the component.
     * <code>null</code> will never be returned, an {@link Insets}
     * instance with all fields set to zero will be returned instead.
     */
    public Insets getInsets ()
    {
        Insets insets = (_border == null) ? ZERO_INSETS : _border.getInsets();
        if (_background != null) {
            insets = _background.adjustInsets(insets);
        }
        return insets;
    }

    /**
     * Returns our bounds as a nicely formatted string.
     */
    public String boundsToString ()
    {
        return _width + "x" + _height + "+" + _x + "+" + _y;
    }

    /**
     * Configures this component with the specified border. Pass null to
     * clear out this component's border.
     */
    public void setBorder (BBorder border)
    {
        BBorder oborder = _border;
        _border = border;
//         _border.setSize(0, 0, _width, _height);
        if (oborder != border) {
            invalidate();
        }
    }

    /**
     * Configures this component with a background. This should be called
     * before any components are added to the window to ensure proper
     * render order.
     */
    public void setBackground (BBackground background)
    {
        _background = background;
    }

    /**
     * Returns a reference to the background used by this component.
     */
    public BBackground getBackground ()
    {
        return _background;
    }

    /**
     * Sets a user defined property on this component. User defined
     * properties allow the association of arbitrary additional data with
     * a component for application specific purposes.
     */
    public void setProperty (String key, Object value)
    {
        if (_properties == null) {
            _properties = new HashMap();
        }
        _properties.put(key, value);
    }

    public Object getProperty (String key)
    {
        return (_properties == null) ? null : _properties.get(key);
    }

    /**
     * Returns whether or not this component accepts the keyboard focus.
     */
    public boolean acceptsFocus ()
    {
        return false;
    }

    /**
     * Returns the component that should receive focus if this component
     * is clicked. If this component does not accept focus, its parent
     * will be checked and so on.
     */
    public BComponent getFocusTarget ()
    {
        if (acceptsFocus()) {
            return this;
        } else if (_parent != null) {
            return _parent.getFocusTarget();
        } else {
            return null;
        }
    }

    /**
     * Requests that this component be given the input focus.
     */
    public void requestFocus ()
    {
        // sanity check
        if (!acceptsFocus()) {
            Log.log.warning("Unfocusable component requested focus: " + this);
            Thread.dumpStack();
            return;
        }

        BWindow window = getWindow();
        if (window == null) {
            Log.log.warning("Focus requested for un-added component: " + this);
            Thread.dumpStack();
        } else {
            window.requestFocus(this);
        }
    }

    /**
     * Sets the upper left position of this component in absolute screen
     * coordinates.
     */
    public void setLocation (int x, int y)
    {
        setBounds(x, y, _width, _height);
    }

    /**
     * Sets the width and height of this component in screen coordinates.
     */
    public void setSize (int width, int height)
    {
        setBounds(_x, _y, width, height);
    }

    /**
     * Sets the bounds of this component in screen coordinates.
     *
     * @see #setLocation
     * @see #setSize
     */
    public void setBounds (int x, int y, int width, int height)
    {
        if (_x != x || _y != y) {
            _x = x;
            _y = y;
        }
        if (_width != width || _height != height) {
            _width = width;
            _height = height;
//             if (_border != null) {
//                 _border.setSize(0, 0, _width, _height);
//             }
            invalidate();
        }
    }

    /**
     * Adds a listener to this component. The listener will be notified
     * when events of the appropriate type are dispatched on this
     * component.
     */
    public void addListener (ComponentListener listener)
    {
        if (_listeners == null) {
            _listeners = new ArrayList();
        }
        _listeners.add(listener);
    }

    /**
     * Removes a listener from this component. Returns true if the
     * listener was in fact in the listener list for this component, false
     * if not.
     */
    public boolean removeListener (ComponentListener listener)
    {
        if (_listeners != null) {
            return _listeners.remove(listener);
        } else {
            return false;
        }
    }

    /**
     * Returns true if this component is added to a hierarchy of
     * components that culminates in a top-level window.
     */
    public boolean isAdded ()
    {
        BWindow win = getWindow();
        return (win != null && win.isAdded());
    }

    /**
     * Instructs this component to lay itself out and then mark itself as
     * valid.
     */
    public void validate ()
    {
        if (!_valid) {
            layout();
            _valid = true;
        }
    }

    /**
     * Marks this component as invalid and needing a relayout. If the
     * component is valid, its parent will also be marked as invalid.
     */
    public void invalidate ()
    {
        if (_valid) {
            _valid = false;
            if (_parent != null) {
                _parent.invalidate();
            }
        }
    }

    /**
     * Renders this component. The default implementation does nothing.
     */
    public void render (Renderer renderer)
    {
        GL11.glTranslatef(_x, _y, 0);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(getAbsoluteX(), getAbsoluteY(), _width, _height);

        try {
            // render our background
            if (_background != null) {
                _background.render(renderer, 0, 0, _width, _height);
            }

            // render our border
            if (_border != null) {
                _border.render(renderer, 0, 0, _width, _height);
            }

            // render any custom component bits
            renderComponent(renderer);

        } finally {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GL11.glTranslatef(-_x, -_y, 0);
        }
    }

    /**
     * Returns the component "hit" by the specified mouse coordinates
     * which might be this component or any of its children. This method
     * should return null if the supplied mouse coordinates are outside
     * the bounds of this component.
     */
    public BComponent getHitComponent (int mx, int my)
    {
	if ((mx >= _x) && (my >= _y) &&
            (mx < _x + _width) && (my < _y + _height)) {
            return this;
        }
        return null;
    }

    /**
     * Instructs this component to process the supplied event.
     */
    public void dispatchEvent (BEvent event)
    {
        // handle focus traversal
        if (event instanceof KeyEvent) {
            KeyEvent kev = (KeyEvent)event;
            if (kev.getType() == KeyEvent.KEY_PRESSED) {
                int modifiers = kev.getModifiers(), keyCode = kev.getKeyCode();
                if (keyCode == KeyInput.KEY_TAB) {
                    if (modifiers == 0) {
                        // TODO: can getWindow() be null here?
                        getWindow().requestFocus(getNextFocus());
                    } else if (modifiers == KeyEvent.SHIFT_DOWN_MASK) {
                        getWindow().requestFocus(getPreviousFocus());
                    }
                }
            }
        }

        // dispatch this event to our listeners
        if (_listeners != null) {
            for (int ii = 0, ll = _listeners.size(); ii < ll; ii++) {
                event.dispatch((ComponentListener)_listeners.get(ii));
            }
        }
    }

    /**
     * Instructs this component to lay itself out. This is called as a
     * result of the component changing size.
     */
    protected void layout ()
    {
        // we have nothing to do by default
    }

    /**
     * Computes and returns a preferred size for this component. This
     * method is called if no overriding preferred size has been supplied.
     */
    protected Dimension computePreferredSize ()
    {
        return new Dimension(0, 0);
    }

    /**
     * This method is called when we are added to a hierarchy that is
     * connected to a top-level window (at which point we can rely on
     * having a look and feel and can set ourselves up).
     */
    protected void wasAdded ()
    {
    }

    /**
     * This method is called when we are removed from a hierarchy that is
     * connected to a top-level window. If we wish to clean up after
     * things done in {@link #wasAdded}, this is a fine place to do so.
     */
    protected void wasRemoved ()
    {
        // mark ourselves as invalid so that if this component is again
        // added to an interface heirarchy it will revalidate at that time
        _valid = false;
    }

    /**
     * Renders any custom bits for this component. This is called with the
     * graphics context translated to (0, 0) relative to this component.
     */
    protected void renderComponent (Renderer renderer)
    {
        Spatial.applyDefaultStates();
    }

    /**
     * Returns a reference to the look and feel in scope for this
     * component.
     */
    protected BLookAndFeel getLookAndFeel ()
    {
        return (_lnf == null && _parent != null) ?
            _parent.getLookAndFeel() : _lnf;
    }

    /**
     * Returns the window that defines the root of our component
     * hierarchy.
     */
    protected BWindow getWindow ()
    {
        if (this instanceof BWindow) {
            return (BWindow)this;
        } else if (_parent != null) {
            return _parent.getWindow();
        } else {
            return null;
        }
    }

    /**
     * Searches for the next component that should receive the keyboard
     * focus. If such a component can be found, it will be returned. If no
     * other focusable component can be found and this component is
     * focusable, this component will be returned. Otherwise, null will be
     * returned.
     */
    protected BComponent getNextFocus ()
    {
        if (_parent instanceof BContainer) {
            return ((BContainer)_parent).getNextFocus(this);
        } else if (acceptsFocus()) {
            return this;
        } else {
            return null;
        }
    }

    /**
     * Searches for the previous component that should receive the
     * keyboard focus. If such a component can be found, it will be
     * returned. If no other focusable component can be found and this
     * component is focusable, this component will be returned. Otherwise,
     * null will be returned.
     */
    protected BComponent getPreviousFocus ()
    {
        if (_parent instanceof BContainer) {
            return ((BContainer)_parent).getPreviousFocus(this);
        } else if (acceptsFocus()) {
            return this;
        } else {
            return null;
        }
    }

    protected BComponent _parent;
    protected BLookAndFeel _lnf;
    protected BBorder _border;
    protected BBackground _background;
    protected Dimension _preferredSize;
    protected int _x, _y, _width, _height;
    protected boolean _valid;
    protected ArrayList _listeners;
    protected HashMap _properties;

    protected static final Insets ZERO_INSETS = new Insets();
}
