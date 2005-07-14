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

package com.jme.bui.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.opengl.GL11;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.state.AlphaState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

import com.jme.bui.Log;
import com.jme.bui.util.Dimension;

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
    }

    // documentation inherited
    public BText createText (String text, ColorRGBA color)
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

        // determine the size of our rendered text
        final Dimension size = new Dimension();
        // TODO: do the Mac hack to get the real bounds
        Rectangle2D bounds = layout.getBounds();
        size.width = (int)(Math.max(bounds.getX(), 0) + bounds.getWidth());
        size.height = (int)(layout.getLeading() + layout.getAscent() +
                            layout.getDescent());

        // blank text results in a zero sized bounds, bump it up to 1x1 to
        // avoid freakout by the BufferedImage
        size.width = Math.max(size.width, 1);
        size.height = Math.max(size.height, 1);

        // render the text into the image
        BufferedImage image = new BufferedImage(
            size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR);
        gfx = image.createGraphics();
        try {
            gfx.setColor(BLANK);
            gfx.fillRect(0, 0, size.width, size.height);
            gfx.setColor(new Color(color.r, color.g, color.b, color.a));
            layout.draw(gfx, -(float)bounds.getX(), layout.getAscent());
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
            public void render (Renderer renderer, int x, int y) {
                _astate.apply();
                GL11.glRasterPos2i(x, y + size.height);
                GL11.glPixelZoom(1f, -1f);
                GL11.glDrawPixels(size.width, size.height,
                                  GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, idata);
                GL11.glPixelZoom(1f, 1f);
            }
        };
    }

    // documentation inherited
    public BText wrapText (
        String text, ColorRGBA color, int maxWidth, int[] remain)
    {
        // determine how many characters we can fit (note: JME currently
        // assumes all text is width 10 so we propagate that hack)
        int maxChars = maxWidth / 10;

        // deal with the easy case
        if (text.length() <= maxChars) {
            remain[0] = 0;
            return createText(text, color);
        }

        // scan backwards from maxChars looking for whitespace
        for (int ii = maxChars; ii >= 0; ii--) {
            if (Character.isWhitespace(text.charAt(ii))) {
                // subtract one to absorb the whitespace that we used to wrap
                remain[0] = (text.length() - ii - 1);
                return createText(text.substring(0, ii), color);
            }
        }

        // ugh, found no whitespace, just hard-wrap at maxChars
        remain[0] = (text.length() - maxChars);
        return createText(text.substring(0, maxChars), color);
    }

    protected Font _font;
    protected AlphaState _astate;
    protected BufferedImage _stub;

    protected static Color BLANK = new Color(1.0f, 1.0f, 1.0f, 0f);
}
