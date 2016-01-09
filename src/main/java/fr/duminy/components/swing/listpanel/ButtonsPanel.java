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

import com.google.common.collect.ImmutableList;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * @param <B> The class of items in the list.
 */
@SuppressWarnings("serial")
class ButtonsPanel<B> extends JPanel {
    private static final int BUTTON_SIZE = 32;
    private static final int PADDING = 2;

    private final List<ListAction> actions = new ArrayList<>();
    private final ListActions<B> listener;
    private final EnumMap<StandardListPanelFeature, FeatureHandle<B>> features = new EnumMap<>(StandardListPanelFeature.class);
    private FeatureHandle<B> userFeatureHandle;

    private Dimension buttonSize;

    ButtonsPanel(ListActions<B> listener) {
        this.listener = listener;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    void addFeature(StandardListPanelFeature feature) {
        if (hasFeature(feature)) {
            return;
        }

        FeatureHandle<B> handle = new FeatureHandle<>(this);
        feature.install(handle);
        features.put(feature, handle);
        revalidate();
    }

    void removeFeature(StandardListPanelFeature feature) {
        FeatureHandle<B> handle = features.remove(feature);
        if (handle == null) {
            return;
        }

        for (ButtonHandle buttonHandle : handle.buttonHandles) {
            actions.remove(buttonHandle.action);
            remove(buttonHandle.button);
        }
        revalidate();
    }

    boolean hasFeature(StandardListPanelFeature feature) {
        return features.containsKey(feature);
    }

    void addButton(String buttonName, ListAction action) {
        if (userFeatureHandle == null) {
            userFeatureHandle = new FeatureHandle<>(this);
        }

        userFeatureHandle.addButton(buttonName, action);
    }

    static class FeatureHandle<B> {
        private final ButtonsPanel<B> buttonsPanel;
        private final List<ButtonHandle> buttonHandles = new ArrayList<>();

        private FeatureHandle(ButtonsPanel<B> buttonsPanel) {
            this.buttonsPanel = buttonsPanel;
        }

        void addButton(String buttonName, ListAction action) {
            buttonsPanel.actions.add(action);
            JButton button = new JButton(action);
            if (buttonsPanel.buttonSize == null) {
                buttonsPanel.buttonSize = new Dimension(BUTTON_SIZE + PADDING, BUTTON_SIZE + PADDING);
            }

            button.setPreferredSize(buttonsPanel.buttonSize);
            button.setName(buttonName);
            buttonsPanel.add(button);

            buttonHandles.add(new ButtonHandle<B>(action, button));
        }

        public ListActions<B> getListener() {
            return buttonsPanel.listener;
        }
    }

    private static class ButtonHandle<B> {
        private final ListAction action;
        private final JButton button;

        private ButtonHandle(ListAction action, JButton button) {
            this.action = action;
            this.button = button;
        }
    }

    ImmutableList<ListAction> getActions() {
        return ImmutableList.copyOf(actions);
    }
}
