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

import com.google.common.collect.ImmutableList;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static fr.duminy.components.swing.listpanel.StandardListPanelFeature.EDITING;
import static fr.duminy.components.swing.listpanel.StandardListPanelFeature.MANUAL_ORDER;

/**
 * @param <B> The class of items in the list.
 */
@SuppressWarnings("serial")
class ButtonsPanel<B> extends JPanel {
    private static final int PADDING = 2;

    private final List<ListAction> actions = new ArrayList<>();

    private Dimension buttonSize;

    ButtonsPanel(ListActions<B> listener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        MANUAL_ORDER.install(this, listener);
        EDITING.install(this, listener);
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

    ImmutableList<ListAction> getActions() {
        return ImmutableList.copyOf(actions);
    }
}
