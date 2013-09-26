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
package fr.duminy.components.swing.listpanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @param <T> The class of items in the list.
 */
@SuppressWarnings("serial")
class ButtonsPanel<T> extends JPanel {
    private static final int PADDING = 2;

    static final String ADD_BUTTON_NAME = "addButton";
    static final String REMOVE_BUTTON_NAME = "removeButton";
    static final String UP_BUTTON_NAME = "upButton";
    static final String DOWN_BUTTON_NAME = "downButton";

    private final List<ListAction> actions = new ArrayList<>();

    private Dimension buttonSize;

    ButtonsPanel(ListActions<T> listener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        addButton(ADD_BUTTON_NAME, new AddItemAction<T>(listener));
        addButton(REMOVE_BUTTON_NAME, new RemoveItemAction<T>(listener));
        addButton(UP_BUTTON_NAME, new MoveUpItemAction<T>(listener));
        addButton(DOWN_BUTTON_NAME, new MoveDownItemAction<T>(listener));
    }

    void addButton(String buttonName, ListAction action) {
        actions.add(action);
        JButton button = new JButton(action);
        if (buttonSize == null) {
            buttonSize = new Dimension(button.getIcon().getIconWidth() + PADDING, button.getIcon().getIconHeight() + PADDING);
        }

        button.setPreferredSize(buttonSize);
        button.setName(buttonName);
        add(button);
    }

    public Collection<ListAction> getActions() {
        return actions;
    }
}
