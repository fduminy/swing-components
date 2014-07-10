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
package fr.duminy.components.swing.listpanel;

import fr.duminy.components.swing.i18n.AbstractI18nAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;

/**
 * Abstract class for an action on a {@link ListActions}.
 *
 * @param <T> The class of items in the list.
 * @param <M> The class of message bundle.
 */
abstract class AbstractItemAction<T, M> extends AbstractI18nAction<M> implements ListAction {
    private ListActions<T> listener;

    /**
     * @param listener       The interface for interactions with the associated listpanel component.
     * @param acceleratorKey The accelerator key for the action.
     * @param iconResource   The icon resource for the action.
     * @param messagesClass  The class of messages containing the action label.
     */
    AbstractItemAction(ListActions<T> listener, int acceleratorKey, String iconResource, Class<M> messagesClass) {
        super(messagesClass);
        this.listener = listener;

        putValue(ACCELERATOR_KEY, acceleratorKey);
        putValue(LARGE_ICON_KEY, loadIcon(iconResource));
        updateMessages();
    }

    private ImageIcon loadIcon(String iconResource) {
        if (iconResource == null) {
            throw new NullPointerException("Icon resource is null");
        }

        URL imageURL = getClass().getResource(iconResource);
        if (imageURL != null) {
            ImageIcon imageIcon = new ImageIcon(imageURL);
            if (imageIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                return imageIcon;
            }
        }

        throw new IllegalArgumentException("Icon resource not found : '" + iconResource + "'");
    }

    void setListener(ListActions<T> listener) {
        this.listener = listener;
    }

    @Override
    final public void actionPerformed(ActionEvent e) {
        doAction(listener);
    }

    /**
     * @param listener The interface for interactions with the associated listpanel component.
     */
    abstract protected void doAction(ListActions<T> listener);
}
