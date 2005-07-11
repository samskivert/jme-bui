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

/**
 * Creates instances of {@link BText} using a particular technology and a
 * particular font configuration.
 */
public abstract class BTextFactory
{
    /**
     * Creates a text instance using our font configuration.
     */
    public abstract BText createText (String text);

    /**
     * Creates a text that is no wider than the specified maximum width
     * but contains as much of the supplied text (terminating on a word
     * boundary) as is possible within that limit.
     *
     * @param remain if non-null, will have the number of unrendered
     * characters filled into the zeroth element.
     */
    public abstract BText wrapText (String text, int maxWidth, int[] remain);
}
