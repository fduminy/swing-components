/**
 * Swing-Components is a library of swing components.
 *
 * Copyright (C) 2013-2013 Fabien DUMINY (fabien [dot] duminy [at] webmails [dot] com)
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
package fr.duminy.components.swing;

import org.ez18n.runtime.BundleFactory;
import org.ez18n.runtime.Desktop;

/**
 * Simple utility class to get our message bundle.
 */
public class Bundle {
    private Bundle() {
    }

    /**
     * Get our message bundle.
     *
     * @return
     */
    public static SwingComponentMessages getBundle() {
        return getBundle(SwingComponentMessages.class);
    }

    /**
     * Get a message bundle implementing the given class.
     *
     * @param messagesClass The interface that the bundle have to implement.
     * @param <T>           The type of interface to implement.
     * @return
     */
    public static <T> T getBundle(Class<T> messagesClass) {
        return BundleFactory.get(messagesClass, Desktop.class);
    }
}
