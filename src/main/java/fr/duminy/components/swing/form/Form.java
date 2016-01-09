/**
 * Swing-Components is a library of swing components.
 *
 * Copyright (C) 2013-2016 Fabien DUMINY (fabien [dot] duminy [at] webmails [dot] com)
 *
 * Swing-Components is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * Swing-Components is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 */
package fr.duminy.components.swing.form;


import javax.swing.*;

/**
 * Interface representing a form for a given bean.
 *
 * @param <B> The type of bean displayed by this form.
 */
public interface Form<B> {
    /**
     * Set the value of this form.
     *
     * @param value
     */
    void setValue(B value);

    /**
     * Get the value represented by this form.
     *
     * @return
     */
    B getValue();

    /**
     * Return a new swing component representing this form.
     *
     * @return
     */
    JComponent asComponent();
}
