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

package com.jme.bui.layout;

import java.awt.Dimension;

import com.jme.bui.BComponent;
import com.jme.bui.BContainer;

/**
 * Layout managers implement a policy for laying out the children in a
 * container. They must provide routines for computing the preferred size
 * of a target container and for actually laying out its children.
 */
public abstract class BLayoutManager
{
    /**
     * Components added to a container will result in a call to this
     * method, informing the layout manager of said constraints. The
     * default implementation does nothing.
     */
    public void addLayoutComponent (BComponent comp, Object constraints)
    {
    }

    /**
     * Components removed to a container for which a layout manager has
     * been configured will result in a call to this method. The default
     * implementation does nothing.
     */
    public void removeLayoutComponent (BComponent comp)
    {
    }

    /**
     * Computes the preferred size for the supplied container, based on
     * the preferred sizes of its children and the layout policy
     * implemented by this manager.
     */
    public abstract Dimension computePreferredSize (BContainer target);

    /**
     * Effects the layout policy of this manager on the supplied target,
     * adjusting the size and position of its children based on the size
     * and position of the target at the time of this call.
     */
    public abstract void layoutContainer (BContainer target);
}
