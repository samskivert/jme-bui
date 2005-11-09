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

package com.jmex.bui.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.AttributedString;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.logging.Level;

import org.lwjgl.opengl.GL11;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Spatial;
import com.jme.scene.state.AlphaState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

import com.jmex.bui.Log;
import com.jmex.bui.util.Dimension;

/**
 * Formats text by using the AWT to render runs of text into a bitmap and
 * then texturing a quad with the result.
 */
public class AWTTextFactory extends BTextFactory
{
    /**
     * Creates an AWT text factory with the supplied font.
     */
    public AWTTextFactory (Font font)
    {
        _font = font;
        _attrs.put(TextAttribute.FONT, _font);

        // create an alpha state that we'll use to draw our text over the
        // background
        _astate = DisplaySystem.getDisplaySystem().getRenderer().
            createAlphaState();
        _astate.setBlendEnabled(true);
        _astate.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        _astate.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        _astate.setEnabled(true);

        // we need a graphics context to figure out how big our text is
        // going to be, but we need an image to get the graphics context,
        // but we don't want to create our image until we know how big our
        // text needs to be. dooh!
        _stub = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);

        // compute the height of our font by creating a sample text and
        // storing its height
        _height = createText("J", ColorRGBA.black).getSize().height;
    }

    // documentation inherited
    public int getHeight ()
    {
        return _height;
    }

    // documentation inherited
    public BText createText (String text, ColorRGBA color)
    {
        return createText(text, color, false);
    }

    // documentation inherited
    public BText wrapText (
        String text, ColorRGBA color, int maxWidth, int[] remain)
    {
        // the empty string will break things; so use a single space instead
        if (text.length() == 0) {
            text = " ";
        }

        Graphics2D gfx = _stub.createGraphics();
        TextLayout layout;
        try {
            gfx.setFont(_font);

            // stop at the next newline or the end of the line if there
            // are no newlines in the text
            int nextret = text.indexOf('\n', 1);
            if (nextret == -1) {
                nextret = text.length();
            }

            // measure out as much text as we can render in one line
            LineBreakMeasurer measurer = new LineBreakMeasurer(
                new AttributedString(text, _attrs).getIterator(),
                gfx.getFontRenderContext());
            layout = measurer.nextLayout(maxWidth, nextret, false);

            // skip past any newline that we used to terminate our wrap
            int pos = measurer.getPosition();
            if (pos < text.length() && text.charAt(pos) == '\n') {
                pos++;
            }

            // note the characters that we were unable to include
            remain[0] = text.length() - pos;

        } finally {
            gfx.dispose();
        }

        return createText(layout, color, true);
    }

    /** Helper function. */
    protected BText createText (
        String text, ColorRGBA color, boolean useAdvance)
    {
        if (text.equals("")) {
            text = " ";
        }

        Graphics2D gfx = _stub.createGraphics();
        TextLayout layout;
        try {
            gfx.setFont(_font);
            layout = new TextLayout(text, _font, gfx.getFontRenderContext());
        } finally {
            gfx.dispose();
        }

        return createText(layout, color, useAdvance);
    }

    /** Helper function. */
    protected BText createText (
        final TextLayout layout, ColorRGBA color, boolean useAdvance)
    {
        // determine the size of our rendered text
        final Dimension size = new Dimension();
        // TODO: do the Mac hack to get the real bounds
        Rectangle2D bounds = layout.getBounds();
        if (useAdvance) {
            size.width = (int)Math.round(
                Math.max(bounds.getX(), 0) + layout.getAdvance());
        } else {
            size.width = (int)Math.round(
                Math.max(bounds.getX(), 0) + bounds.getWidth());
        }
        size.height = (int)(layout.getLeading() + layout.getAscent() +
                            layout.getDescent());

        // blank text results in a zero sized bounds, bump it up to 1x1 to
        // avoid freakout by the BufferedImage
        size.width = Math.max(size.width, 1);
        size.height = Math.max(size.height, 1);

        // render the text into the image
        BufferedImage image = new BufferedImage(
            size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D gfx = image.createGraphics();
        try {
//             gfx.setColor(Color.blue);
//             gfx.drawRect(0, 0, size.width-1, size.height-1);
            gfx.setColor(new Color(color.r, color.g, color.b, color.a));
            layout.draw(gfx, 0, layout.getAscent());
        } finally {
            gfx.dispose();
        }

        final ByteBuffer idata =
            ByteBuffer.allocateDirect(4 * image.getWidth() * image.getHeight());
        idata.order(ByteOrder.nativeOrder());
        byte[] data = (byte[])image.getRaster().getDataElements(
            0, 0, image.getWidth(), image.getHeight(), null);
        idata.clear();
        idata.put(data);
        idata.flip();

        // wrap it all up in the right object
        return new BText() {
            public Dimension getSize () {
                return size;
            }
            public int getCursorPos (int index) {
                Shape[] carets = layout.getCaretShapes(index);
                Rectangle2D bounds = carets[0].getBounds2D();
                return (int)Math.round(bounds.getX() + bounds.getWidth()/2);
            }
            public void render (Renderer renderer, int x, int y) {
                Spatial.applyDefaultStates();
                _astate.apply();

                GL11.glRasterPos2i(x, y + size.height);
                GL11.glPixelZoom(1f, -1f);
                GL11.glDrawPixels(size.width, size.height,
                                  GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, idata);
                GL11.glPixelZoom(1f, 1f);
            }
        };
    }

    protected Font _font;
    protected HashMap _attrs = new HashMap();
    protected int _height;
    protected AlphaState _astate;
    protected BufferedImage _stub;
}
