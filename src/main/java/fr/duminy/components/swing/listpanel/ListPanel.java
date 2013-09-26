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

import com.google.common.base.Supplier;
import fr.duminy.components.swing.i18n.I18nAble;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Arrays;

/**
 * This component encapsulates a listpanel component (provided by an implementation of {@link ListComponent})
 * associated with buttons allowing the user to <br/>
 * <ul>
 * <li>add an item</li>
 * <li>remove an item</li>
 * <li>move an item up</li>
 * <li>move an item down</li>
 * </ul>.
 *
 * @param <TC> The class of list component (example : a JList).
 * @param <T>  The class of items in the list.
 */
@SuppressWarnings("serial")
public class ListPanel<TC extends JComponent, T> extends JPanel implements ListActions<T>, I18nAble {
    private static Logger LOG = LoggerFactory.getLogger(ButtonsPanel.class);

    private final ListComponent<TC, T> list;
    private final ButtonsPanel<T> buttons;

    /**
     * @param list        The list component to wrap.
     * @param itemFactory This factory is used to add a new item to the list. When it returns null, the user has cancelled the operation.
     */
    @SuppressWarnings("unchecked")
    public ListPanel(JList<T> list, Supplier<T> itemFactory) {
        this((ListComponent<TC, T>) new JListComponentWrapper<T>(list, itemFactory));
    }

    /**
     * @param list The listpanel component to wrap.
     */
    public ListPanel(final ListComponent<TC, T> list) {
        setLayout(new BorderLayout());

        this.list = list;
        add(new JScrollPane(list.getComponent(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

        buttons = new ButtonsPanel<>(this);
        add(buttons, BorderLayout.EAST);

        list.addSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    updateButtons();
                }
            }
        });

        updateButtons();
    }

    public TC getListComponent() {
        return list.getComponent();
    }

    /**
     * Add a user action.
     *
     * @param buttonName The name of the button (used for tests).
     * @param action     The action to add.
     */
    public void addUserButton(String buttonName, AbstractUserItemAction<T, ?> action) {
        action.setListener(this);
        buttons.addButton(buttonName, action);
    }

    @Override
    public void executeUserAction(UserListAction<T> action) {
        for (int selectedIndice : getSortedSelectedIndices()) {
            T item = list.getItem(selectedIndice);
            action.executeAction(item);
        }
    }

    @Override
    public void addItem() {
        list.addItem();
    }

    @Override
    public void removeItem() {
        int[] selectedItems = getSortedSelectedIndices();
        for (int i = selectedItems.length - 1; i >= 0; i--) {
            list.removeItem(selectedItems[i]);
        }
    }

    @Override
    public void moveUpItem() {
        int[] selectedIndices = getSortedSelectedIndices();
        for (int selectedIndice : selectedIndices) {
            list.moveUpItem(selectedIndice);
        }

        list.setSelectedIndices(moveIndicesUp(selectedIndices));
    }

    @Override
    public void moveDownItem() {
        int[] selectedIndices = getSortedSelectedIndices();
        for (int i = selectedIndices.length - 1; i >= 0; i--) {
            list.moveDownItem(selectedIndices[i]);
        }

        list.setSelectedIndices(moveIndicesDown(selectedIndices, list.getSize()));
    }


    private static int[] subArray(int[] array, int begin, int end) {
        int[] result = new int[end - begin];
        System.arraycopy(array, begin, result, 0, result.length);
        return result;
    }

    static int[] moveIndicesUp(int[] indices) {
        if (indices.length > 0) {
            int begin = 0;
            if (indices[0] == 0) {
                begin++;
            }
            indices = subArray(indices, begin, indices.length);
            for (int i = 0; i < indices.length; i++) {
                indices[i]--;
            }
        }

        return indices;
    }

    static int[] moveIndicesDown(int[] indices, int listSize) {
        if (indices.length > 0) {
            int end = indices.length;
            if (indices[end - 1] >= (listSize - 1)) {
                end--;
            }
            indices = subArray(indices, 0, end);
            for (int i = 0; i < indices.length; i++) {
                indices[i]++;
            }
        }

        return indices;
    }

    private int[] getSortedSelectedIndices() {
        int[] selectedItems = list.getSelectedIndices();
        Arrays.sort(selectedItems);
        return selectedItems;
    }

    private void updateButtons() {
        int[] selectedItems = getSortedSelectedIndices();
        for (ListAction action : buttons.getActions()) {
            action.updateState(selectedItems, list.getSize());
        }
    }

    @Override
    public void updateMessages() {
        for (I18nAble action : buttons.getActions()) {
            action.updateMessages();
        }
    }
}
