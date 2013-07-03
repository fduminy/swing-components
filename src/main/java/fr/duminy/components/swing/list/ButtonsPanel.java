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

import fr.duminy.components.swing.i18n.I18nAction;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
class ButtonsPanel extends JPanel {
    private static final int PADDING = 2;

    static final String ADD_BUTTON_NAME = "addButton";
    static final String REMOVE_BUTTON_NAME = "removeButton";
    static final String UP_BUTTON_NAME = "upButton";
    static final String DOWN_BUTTON_NAME = "downButton";

    private final JButton addButton;
    private final JButton removeButton;
    private final JButton upButton;
    private final JButton downButton;

    final I18nAction[] actions;

    ButtonsPanel(ListActions listener) {
        super(new GridLayout(4, 1));

        I18nAction addAction = new AddItemAction(listener);
        addButton = new JButton(addAction);
        Dimension d = new Dimension(addButton.getIcon().getIconWidth() + PADDING, addButton.getIcon().getIconHeight() + PADDING);
        addButton.setPreferredSize(d);
        addButton.setName(ADD_BUTTON_NAME);
        add(addButton);

        I18nAction removeAction = new RemoveItemAction(listener);
        removeButton = new JButton(removeAction);
        removeButton.setPreferredSize(d);
        removeButton.setName(REMOVE_BUTTON_NAME);
        add(removeButton);

        I18nAction moveUpAction = new MoveUpItemAction(listener);
        upButton = new JButton(moveUpAction);
        upButton.setPreferredSize(d);
        upButton.setName(UP_BUTTON_NAME);
        add(upButton);

        I18nAction moveDownAction = new MoveDownItemAction(listener);
        downButton = new JButton(moveDownAction);
        downButton.setPreferredSize(d);
        downButton.setName(DOWN_BUTTON_NAME);
        add(downButton);

        actions = new I18nAction[]{addAction, removeAction, moveUpAction, moveDownAction};
    }

    void setEnabled(boolean enabled, String... buttonNames) {
        for (String buttonName : buttonNames) {
            if (ADD_BUTTON_NAME.equals(buttonName)) {
                addButton.setEnabled(enabled);
            } else if (REMOVE_BUTTON_NAME.equals(buttonName)) {
                removeButton.setEnabled(enabled);
            } else if (UP_BUTTON_NAME.equals(buttonName)) {
                upButton.setEnabled(enabled);
            } else if (DOWN_BUTTON_NAME.equals(buttonName)) {
                downButton.setEnabled(enabled);
            }
        }
    }
}
