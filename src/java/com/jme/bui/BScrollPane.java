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

// import com.jmex.bui.event.InputDispatcher;
import com.jmex.bui.util.Dimension;
import com.jme.image.Texture;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

/**
 * Provides a scrollable clipped view on a sub-heirarchy of components.
 */
public class BScrollPane extends BComponent
{
    public BScrollPane (BComponent child)
    {
        // create the quad that will display our viewport
        _viewport = new Quad("viewport", 1, 1);
//         _node.attachChild(_viewport);
        _vtstate = DisplaySystem.getDisplaySystem().getRenderer().
            createTextureState();
        _vtstate.setEnabled(true);
        _viewport.setRenderState(_vtstate);
        _viewport.updateGeometricState(0, true);
        _viewport.updateRenderState();

        // set up our child
        _child = child;
        _child.setParent(this);
//         _child.getNode().setRenderQueueMode(Renderer.QUEUE_ORTHO);
        if (isAdded()) {
            _child.wasAdded();
        }
    }

    /**
     * Called every frame by the input dispatcher to allow us to re-render
     * our viewport.
     */
    public void update ()
    {
//         _trenderer.render(_child.getNode(), _texture);
    }

    // documentation inherited
    public Dimension getPreferredSize ()
    {
        return _child.getPreferredSize();
    }

    // documentation inherited
    public void validate ()
    {
        if (!_valid) {
            // lay ourselves out
            layout();
            // and validate our child
            _child.validate();
            // finally mark ourselves as valid
            _valid = true;
        }
    }

    // documentation inherited
    protected void wasAdded ()
    {
        super.wasAdded();
        _child.wasAdded();
//         _dispatcher = getWindow().getInputDispatcher();
//         _dispatcher.addScrollPane(this);
    }

    // documentation inherited
    protected void wasRemoved ()
    {
        super.wasRemoved();
        _child.wasRemoved();
//         if (_dispatcher != null) {
//             _dispatcher.removeScrollPane(this);
//             _dispatcher = null;
//         }
    }

    // documentation inherited
    protected void layout ()
    {
        // determine the desired size of our texture
        int vwidth = getWidth(), vheight = getHeight();
        int twidth = nextPOT(vwidth), theight = nextPOT(vheight);
        _viewport.resize(vwidth, vheight);
        _viewport.setLocalTranslation(new Vector3f(vwidth/2f, vheight/2f, 0f));
//         _viewport.setTextures(new Vector2f[] {
//             new Vector2f(0, 0), new Vector2f(0, vheight/(float)theight),
//             new Vector2f(vwidth/(float)twidth, vheight/(float)theight),
//             new Vector2f(vwidth/(float)twidth, 0), new Vector2f(0, 0) });

        // create or recreate our texture renderer as needed
        if (_texture == null || _texture.getImage().getWidth() != twidth ||
            _texture.getImage().getWidth() != twidth) {
            if (_trenderer != null) {
                _trenderer.cleanup();
            }
            Log.log.info("Recreating texture [size=" + twidth + "x" + theight +
                         ", osize=" + vwidth + "x" + vheight + "].");
            _trenderer = DisplaySystem.getDisplaySystem().createTextureRenderer(
                twidth, theight, false, true, false, false,
                TextureRenderer.RENDER_TEXTURE_2D, 0);
//             _trenderer.getCamera().setLocation(new Vector3f(0, 0, 65f));
//             _trenderer.updateCamera();
            _texture = _trenderer.setupTexture();
//             _texture.setWrap(Texture.WM_CLAMP_S_CLAMP_T);
            _vtstate.setTexture(_texture);
        }

        // also tell our child to size to its preferred size
        Dimension cpsize = _child.getPreferredSize();
        _child.setBounds(0, 0, cpsize.width, cpsize.height);
        Log.log.info("Sizing child " + cpsize);
    }

    /** Rounds the supplied value up to a power of two. */
    protected static int nextPOT (int value)
    {
        return (bitCount(value) > 1) ? (highestOneBit(value) << 1) : value;
    }

    protected static int bitCount (int value)
    {
        // some two's complement magic
	value = value - ((value >>> 1) & 0x55555555);
	value = (value & 0x33333333) + ((value >>> 2) & 0x33333333);
	value = (value + (value >>> 4)) & 0x0f0f0f0f;
	value = value + (value >>> 8);
	value = value + (value >>> 16);
	return value & 0x3f;
    }

    protected static int highestOneBit (int value)
    {
        // more two's complement magic
        value |= (value >>  1);
        value |= (value >>  2);
        value |= (value >>  4);
        value |= (value >>  8);
        value |= (value >> 16);
        return value - (value >>> 1);
    }

    protected BComponent _child;
//     protected InputDispatcher _dispatcher;

    protected Quad _viewport;
    protected TextureState _vtstate;

    protected TextureRenderer _trenderer;
    protected Texture _texture;
}
