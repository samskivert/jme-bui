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

/**
 * Provides additional information about a background that is used to
 * display the backgrounds of various components.
 */
public abstract class BBackground extends BComponent
{
    /**
     * Returns the left inset that should be used by components rendered
     * inside this background.
     */
    public int getLeftInset ()
    {
        return _left;
    }

    /**
     * Returns the top inset that should be used by components rendered
     * inside this background.
     */
    public int getTopInset ()
    {
        return _top;
    }

    /**
     * Returns the right inset that should be used by components rendered
     * inside this background.
     */
    public int getRightInset ()
    {
        return _right;
    }

    /**
     * Returns the bottom inset that should be used by components rendered
     * inside this background.
     */
    public int getBottomInset ()
    {
        return _bottom;
    }

    /** Configures this background with its insets. */
    protected BBackground (int left, int top, int right, int bottom)
    {
        _left = left;
        _top = top;
        _right = right;
        _bottom = bottom;
    }

    protected int _left, _top, _right, _bottom;
}
