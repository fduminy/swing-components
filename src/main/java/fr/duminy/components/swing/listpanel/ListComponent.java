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
package fr.duminy.components.swing.listpanel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;

/**
 * @param <B> The class of items in the list.
 * @param <C> The class of list component.
 */
interface ListComponent<B, C extends JComponent> {
    // This is only needed by tests.
    // FIXME Find a way to remove this method from here.
    C getComponent();

    void addItem();

    void updateItem(int i);

    void removeItem(int i);

    void moveUpItem(int i);

    void moveDownItem(int i);

    int getSize();

    void addSelectionListener(ListSelectionListener listener);

    int[] getSelectedIndices();

    void setSelectedIndices(int... indices);

    B getItem(int i);
}
