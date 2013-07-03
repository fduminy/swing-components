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

import java.util.ListResourceBundle;
import java.util.Locale;

import static fr.duminy.components.swing.SwingComponentMessages.*;

/**
 * French bundle for {link SwingComponentMessages}.
 */
public class DesktopSwingComponentMessages_fr extends ListResourceBundle {
    public static final String ADD_KEY = "addItem";
    public static final String REMOVE_KEY = "removeItem";
    public static final String UP_KEY = "moveUpItem";
    public static final String DOWN_KEY = "moveDownItem";

    private static final Object[][] CONTENTS = new Object[][]{
            {UP_KEY, "Monter article", MOVE_UP_MESSAGE},
            {REMOVE_KEY, "Supprimer article", REMOVE_MESSAGE},
            {DOWN_KEY, "Descendre article", MOVE_DOWN_MESSAGE},
            {ADD_KEY, "Ajouter un nouvel article", ADD_MESSAGE}
    };

    public Object[][] getContents() {
        return CONTENTS;
    }

    public static String getMessage(String key) {
        int localeIndex = Locale.ENGLISH.equals(Locale.getDefault()) ? 2 : 1;
        for (Object[] message : CONTENTS) {
            if (message[0].equals(key)) {
                return String.valueOf(message[localeIndex]);
            }
        }
        throw new IllegalArgumentException("unknown key : '" + key + "'");
    }
}
