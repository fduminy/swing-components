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
package fr.duminy.components.swing;

import fr.duminy.components.swing.form.JFormPane;
import fr.duminy.components.swing.listpanel.AbstractItemActionTest;

import java.util.ListResourceBundle;
import java.util.Locale;

import static fr.duminy.components.swing.SwingComponentMessages.*;

/**
 * French bundle for {link SwingComponentMessages}.
 */
public class DesktopSwingComponentMessages_fr extends ListResourceBundle {
    public static final String ADD_KEY = "addItemTooltip";
    public static final String REMOVE_KEY = "removeItemTooltip";
    public static final String UP_KEY = "moveUpItemTooltip";
    public static final String DOWN_KEY = "moveDownItemTooltip";
    public static final String UPDATE_KEY = "updateItemTooltip";

    public static final String CREATE_TEXT_KEY = "createText";
    public static final String CREATE_TOOLTIP_KEY = "createTooltip";
    public static final String UPDATE_TEXT_KEY = "updateText";
    public static final String UPDATE_TOOLTIP_KEY = "updateTooltip";
    public static final String CANCEL_TEXT_KEY = "cancelText";

    private static final Object[][] CONTENTS = new Object[][]{
            {UP_KEY, "Monter article", MOVE_UP_ITEM_TOOLTIP},
            {REMOVE_KEY, "Supprimer article", REMOVE_ITEM_TOOLTIP},
            {DOWN_KEY, "Descendre article", MOVE_DOWN_ITEM_TOOLTIP},
            {ADD_KEY, "Ajouter un nouvel article", ADD_ITEM_TOOLTIP},
            {UPDATE_KEY, "Modifier article", UPDATE_ITEM_TOOLTIP},

            {CREATE_TEXT_KEY, "Créer", CREATE_TEXT},
            {CREATE_TOOLTIP_KEY, "Créer", CREATE_TEXT},
            {UPDATE_TEXT_KEY, "Mettre à jour", UPDATE_TEXT},
            {UPDATE_TOOLTIP_KEY, "Mettre à jour", UPDATE_TEXT},
            {CANCEL_TEXT_KEY, "Annuler", CANCEL_TEXT}
    };

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    public static String getExpectedMessage(JFormPane.Mode mode) {
        switch (mode) {
            case CREATE:
                return getExpectedMessage(CREATE_TEXT_KEY);
            case UPDATE:
                return getExpectedMessage(UPDATE_TEXT_KEY);
            default:
                throw new IllegalArgumentException("unknown mode: " + mode);
        }
    }

    public static String getExpectedMessage(String key) {
        int localeIndex = AbstractItemActionTest.DEFAULT_LOCALE.equals(Locale.getDefault()) ? 2 : 1;
        for (Object[] message : CONTENTS) {
            if (message[0].equals(key)) {
                return String.valueOf(message[localeIndex]);
            }
        }
        throw new IllegalArgumentException("unknown key : '" + key + "'");
    }
}
