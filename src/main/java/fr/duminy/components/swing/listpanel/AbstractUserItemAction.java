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

/**
 * Abstract implementation of {@link UserListAction}.
 *
 * @param <T> The class of items in the list.
 * @param <M> The class of message bundle.
 */
abstract public class AbstractUserItemAction<T, M> extends AbstractItemAction<T, M> implements UserListAction<T> {
    /**
     * @param acceleratorKey The accelerator key for the action.
     * @param iconResource   The icon resource for the action.
     * @param messagesClass  The class of messages for this action.
     */
    public AbstractUserItemAction(int acceleratorKey, String iconResource, Class<M> messagesClass) {
        super(null, acceleratorKey, iconResource, messagesClass);
    }

    /**
     * @param listener The interface for interactions with the associated listpanel component.
     */    @Override
    protected final void doAction(ListActions<T> listener) {
        listener.executeUserAction(this);
    }
}
