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

import org.ez18n.Message;
import org.ez18n.MessageBundle;

/**
 * Interface for i18n messages.
 */
@MessageBundle
public interface SwingComponentMessages {
    static final String MOVE_UP_MESSAGE = "Move up item(s)";
    static final String REMOVE_MESSAGE = "Remove item(s)";
    static final String MOVE_DOWN_MESSAGE = "Move down item(s)";
    static final String ADD_MESSAGE = "Add a new item";

    @Message(value = MOVE_UP_MESSAGE)
    String moveUpItem();

    @Message(value = REMOVE_MESSAGE)
    String removeItem();

    @Message(value = MOVE_DOWN_MESSAGE)
    String moveDownItem();

    @Message(value = ADD_MESSAGE)
    String addItem();
}
