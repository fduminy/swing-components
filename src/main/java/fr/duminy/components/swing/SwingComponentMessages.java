/**
 * Swing-Components is a library of swing components.
 *
 * Copyright (C) 2013-2014 Fabien DUMINY (fabien [dot] duminy [at] webmails [dot] com)
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

import org.ez18n.Message;
import org.ez18n.MessageBundle;

/**
 * Interface for i18n messages.
 */
@MessageBundle
public interface SwingComponentMessages {
    static final String MOVE_UP_ITEM_TOOLTIP = "Move up item(s)";
    static final String REMOVE_ITEM_TOOLTIP = "Remove item(s)";
    static final String MOVE_DOWN_ITEM_TOOLTIP = "Move down item(s)";
    static final String ADD_ITEM_TOOLTIP = "Add a new item";
    static final String UPDATE_ITEM_TOOLTIP = "Update the item";


    static final String CREATE_TEXT = "Create";
    static final String CREATE_TOOLTIP = "Confirm creation";

    static final String UPDATE_TEXT = "Update";
    static final String UPDATE_TOOLTIP = "Confirm update";

    static final String CANCEL_TEXT = "Cancel";

    @Message(value = MOVE_UP_ITEM_TOOLTIP)
    String moveUpItemTooltip();

    @Message(value = REMOVE_ITEM_TOOLTIP)
    String removeItemTooltip();

    @Message(value = MOVE_DOWN_ITEM_TOOLTIP)
    String moveDownItemTooltip();

    @Message(value = ADD_ITEM_TOOLTIP)
    String addItemTooltip();

    @Message(value = UPDATE_ITEM_TOOLTIP)
    String updateItemTooltip();


    @Message(value = CREATE_TEXT)
    String createText();

    @Message(value = CREATE_TOOLTIP)
    String createTooltip();

    @Message(value = UPDATE_TEXT)
    String updateText();

    @Message(value = UPDATE_TOOLTIP)
    String updateTooltip();

    @Message(value = CANCEL_TEXT)
    String cancelText();
}
