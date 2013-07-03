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
package fr.duminy.components.swing.i18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

/**
 * Manager for a set of {@link I18nAble} objects.
 */
public class I18nManager implements I18nAble {
    private final Collection<I18nAble> objects = new ArrayList<>();

    public final void add(I18nAble object) {
        if (object == this) {
            throw new IllegalArgumentException("can't add this");
        }

        objects.add(object);
    }

    public final void remove(I18nAble object) {
        objects.remove(object);
    }

    /**
     * Set the current locale and update all associated messages.
     *
     * @param locale The new current locale.
     */
    public final void setLocale(Locale locale) {
        Locale.setDefault(locale);
        updateMessages();
    }

    /**
     * Updates messages of all associated objects.
     */
    @Override
    public final void updateMessages() {
        for (I18nAble object : objects) {
            object.updateMessages();
        }
    }
}
