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

import fr.duminy.components.swing.i18n.I18nAble;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * This component encapsulates a list component (provided by an implementation of {@link ListComponent})
 * associated with buttons allowing the user to <br/>
 * <ul>
 * <li>add an item</li>
 * <li>remove an item</li>
 * <li>move an item up</li>
 * <li>move an item down</li>
 * </ul>.
 *
 * @param <B> The class of items in the list.
 * @param <C> The class of list component (example : a JList).
 */
@SuppressWarnings("serial")
public class ListPanel<B, C extends JComponent> extends JPanel implements I18nAble {
    private final transient ListComponent<B, C> list;
    private final transient ButtonsPanel<B> buttons;
    private final transient ListActions<B> listActions = new ListActionsImpl();

    /**
     * @param list        The list component to wrap.
     * @param itemManager The manager of items to use.
     */
    @SuppressWarnings("unchecked")
    public ListPanel(JList<B> list, ItemManager<B> itemManager) {
        this((ListComponent<B, C>) new JListComponentWrapper<>(list, itemManager));
    }

    /**
     * @param list The listpanel component to wrap.
     */
    public ListPanel(final ListComponent<B, C> list) {
        setLayout(new BorderLayout());

        this.list = list;
        add(new JScrollPane(list.getComponent(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

        buttons = new ButtonsPanel<>(listActions);
        add(buttons, BorderLayout.EAST);

        list.addSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtons();
            }
        });

        updateButtons();
    }

    /**
     * Add a user action.
     *
     * @param buttonName The name of the button (used for tests).
     * @param action     The action to add.
     */
    public void addUserButton(String buttonName, AbstractUserItemAction<B, ?> action) {
        action.setListener(listActions);
        buttons.addButton(buttonName, action);
        action.updateState(getSortedSelectedIndices(), list.getSize());
    }

    /**
     * Add a new {@link fr.duminy.components.swing.listpanel.StandardListPanelFeature} to this component.
     *
     * @param feature The feature to add.
     */
    public void addFeature(StandardListPanelFeature feature) {
        buttons.addFeature(feature);
        updateButtons();
    }

    /**
     * Remove a {@link fr.duminy.components.swing.listpanel.StandardListPanelFeature} from this component.
     *
     * @param feature The feature to remove.
     */
    public void removeFeature(StandardListPanelFeature feature) {
        buttons.removeFeature(feature);
    }

    /**
     * Does this component has the given feature ?
     *
     * @param feature
     * @return true if this component has the given feature.
     */
    public boolean hasFeature(StandardListPanelFeature feature) {
        return buttons.hasFeature(feature);
    }

    @Override
    public void updateMessages() {
        buttons.getActions().forEach(I18nAble::updateMessages);
    }

    ///////////////////////////////////
    ///// package private methods /////
    ///////////////////////////////////

    final C getListComponent() {
        return list.getComponent();
    }

    final ListActions<B> getListActions() {
        return listActions;
    }

    //We need this method here instead of inside ListActionsImpl class because of a test.
    //FIXME Find way to remove this method here.
    void updateItem() {
        int[] selectedItems = getSortedSelectedIndices();
        if (selectedItems.length > 0) {
            list.updateItem(selectedItems[0]);
        }
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

    ///////////////////////////////////
    ///////// private methods /////////
    ///////////////////////////////////

    private class ListActionsImpl implements ListActions<B> {
        @Override
        public void executeUserAction(UserListAction<B> action) {
            for (int selectedIndice : getSortedSelectedIndices()) {
                B item = list.getItem(selectedIndice);
                action.executeAction(item);
            }
        }

        @Override
        public void addItem() {
            list.addItem();
        }

        @Override
        public void updateItem() {
            ListPanel.this.updateItem();
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
    }

    private static int[] subArray(int[] array, int begin, int end) {
        int[] result = new int[end - begin];
        System.arraycopy(array, begin, result, 0, result.length);
        return result;
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
}
