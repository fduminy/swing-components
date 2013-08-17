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
import fr.duminy.components.swing.i18n.I18nAble;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Arrays;

import static fr.duminy.components.swing.list.ButtonsPanel.*;

@SuppressWarnings("serial")
public class ListPanel<TC extends JComponent, T> extends JPanel implements ListActions, I18nAble {
    private final ListComponent<TC, T> list;
    private final ButtonsPanel buttons;

    @SuppressWarnings("unchecked")
    public ListPanel(JList<T> list, Supplier<T> factory) {
        this((ListComponent<TC, T>) new JListComponentWrapper<T>(list, factory));
    }

    public ListPanel(final ListComponent<TC, T> list) {
        setLayout(new BorderLayout());

        this.list = list;
        add(new JScrollPane(list.getComponent(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

        buttons = new ButtonsPanel(this);
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
        buttons.setEnabled(true, ADD_BUTTON_NAME);
        buttons.setEnabled(false, REMOVE_BUTTON_NAME, UP_BUTTON_NAME, DOWN_BUTTON_NAME);

        int[] selectedItems = getSortedSelectedIndices();
        if (selectedItems.length > 0) {
            buttons.setEnabled(true, REMOVE_BUTTON_NAME);

            boolean upIsOk = false;
            boolean downIsOk = false;
            for (int selectedItem : selectedItems) {
                if (selectedItem > 0) {
                    upIsOk = true;
                }
                if (selectedItem < (list.getSize() - 1)) {
                    downIsOk = true;
                }
            }

            buttons.setEnabled(upIsOk, UP_BUTTON_NAME);
            buttons.setEnabled(downIsOk, DOWN_BUTTON_NAME);
        }
    }

    @Override
    public void updateMessages() {
        for (I18nAble action : buttons.actions) {
            action.updateMessages();
        }
    }
}
