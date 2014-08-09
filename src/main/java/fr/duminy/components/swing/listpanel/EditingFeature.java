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
 * This feature allows the user to add,remove and update an item in a {@link ListPanel}.
 *
 * @param <B> The class of items in the list.
 */
class EditingFeature<B> implements ListPanelFeature<B> {
    static final String ADD_BUTTON_NAME = "addButton";
    static final String REMOVE_BUTTON_NAME = "removeButton";
    static final String UPDATE_BUTTON_NAME = "updateButton";

    EditingFeature() {
    }

    @Override
    public void install(ButtonsPanel<B> buttonsPanel, ListActions<B> listener) {
        buttonsPanel.addButton(ADD_BUTTON_NAME, new AddItemAction<>(listener));
        buttonsPanel.addButton(REMOVE_BUTTON_NAME, new RemoveItemAction<>(listener));
        buttonsPanel.addButton(UPDATE_BUTTON_NAME, new UpdateItemAction<>(listener));
    }
}
