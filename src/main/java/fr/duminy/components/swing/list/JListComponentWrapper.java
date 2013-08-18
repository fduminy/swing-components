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

import com.google.common.base.Supplier;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;

/**
 * This class is an implementation of {@link ListComponent} that wraps a {@link JList} component.
 *
 * @param <T> The type of items contained in the list.
 */
class JListComponentWrapper<T> implements ListComponent<JList<T>, T> {
    private final JList<T> list;
    private final DefaultListModel<T> model;
    private final Supplier<T> itemFactory;

    /**
     * @param list        The list component to wrap.
     * @param itemFactory This itemFactory is used to add a new item to the list. When it returns null, the user has cancelled the operation.
     */
    JListComponentWrapper(JList<T> list, Supplier<T> itemFactory) {
        this.list = list;
        model = (DefaultListModel<T>) list.getModel();
        this.itemFactory = itemFactory;
    }

    @Override
    public JList<T> getComponent() {
        return list;
    }

    @Override
    public void addItem() {
        T item = itemFactory.get();
        // if item is null, then the user has cancelled the operation
        if (item != null) {
            model.addElement(item);
        }
    }

    @Override
    public void removeItem(int i) {
        if (isValidIndex(i, true, true)) {
            model.remove(i);
        }
    }

    @Override
    public void moveUpItem(int i) {
        if (isValidIndex(i, false, true)) {
            T item = model.remove(i);
            model.add(i - 1, item);
        }
    }

    @Override
    public void moveDownItem(int i) {
        if (isValidIndex(i, true, false)) {
            T item = model.remove(i);
            model.add(i + 1, item);
        }
    }

    private boolean isValidIndex(int i, boolean firstIsValid, boolean lastIsValid) {
        int min = firstIsValid ? 0 : 1;
        int max = lastIsValid ? model.getSize() : model.getSize() - 1;
        return (i >= min) && (i < max);
    }

    @Override
    public int getSize() {
        return model.getSize();
    }

    @Override
    public void addSelectionListener(ListSelectionListener listener) {
        list.addListSelectionListener(listener);
    }

    @Override
    public int[] getSelectedIndices() {
        return list.getSelectedIndices();
    }

    @Override
    public void setSelectedIndices(int... indices) {
        list.setSelectedIndices(indices);
    }
}
