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

package com.jme.bui.util;

import com.jme.scene.Spatial;
import com.jme.scene.state.AlphaState;
import com.jme.system.DisplaySystem;

/**
 * Useful rendering functions.
 */
public class RenderUtil
{
    /** An alpha state that blends the source plus one minus destination. */
    public static AlphaState blendState;

    /**
     * Configures the supplied spatial with transparency in the standard
     * user interface sense which is that transparent pixels show through
     * to the background but non-transparent pixels are not blended with
     * what is behind them.
     */
    public static void makeTransparent (Spatial target)
    {
        target.setRenderState(blendState);
    }

    static {
        blendState = DisplaySystem.getDisplaySystem().getRenderer().
            createAlphaState();
        blendState.setBlendEnabled(true);
        blendState.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        blendState.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        blendState.setEnabled(true);
    }
}
