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
package fr.duminy.components.swing.list;

import fr.duminy.components.swing.SwingComponentMessages;
import fr.duminy.components.swing.i18n.AbstractI18nAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Abstract class for an action on a {@link ListActions}.
 */
abstract class AbstractItemAction extends AbstractI18nAction<SwingComponentMessages> {
    private final ListActions listener;

    /**
     * @param listener       The interface for interactions with the associated list component.
     * @param acceleratorKey The accelerator key for the action.
     * @param iconResource   The icon resource for the action.
     */
    AbstractItemAction(ListActions listener, int acceleratorKey, String iconResource) {
        super(SwingComponentMessages.class);
        this.listener = listener;

        putValue(ACCELERATOR_KEY, acceleratorKey);
        putValue(LARGE_ICON_KEY, new ImageIcon(getClass().getResource(iconResource)));
        updateMessages();
    }

    @Override
    final public void actionPerformed(ActionEvent e) {
        doAction(listener);

    }

    /**
     * @param listener The interface for interactions with the associated list component.
     */
    abstract protected void doAction(ListActions listener);
}
