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
import fr.duminy.components.swing.AbstractSwingTest;
import fr.duminy.components.swing.DesktopSwingComponentMessages_fr;
import fr.duminy.components.swing.SwingComponentMessages;
import fr.duminy.components.swing.i18n.I18nAble;
import fr.duminy.components.swing.list.DefaultMutableListModel;
import fr.duminy.components.swing.list.MutableListModel;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static fr.duminy.components.swing.DesktopSwingComponentMessages_fr.*;
import static fr.duminy.components.swing.listpanel.ButtonsPanel.*;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(Theories.class)
public class ListPanelTest extends AbstractSwingTest {
    @DataPoint
    public static final Locale FRENCH = Locale.FRENCH;
    @DataPoint
    public static final Locale ENGLISH = Locale.ENGLISH;

    @DataPoint
    public static final boolean ADD_ITEM = false;
    @DataPoint
    public static final boolean CANCEL_ADD_ITEM = true;

    private static final Logger LOG = LoggerFactory.getLogger(ListPanelTest.class);

    private static final String NEW_ITEM = "Z";

    private static int[] NB_ITEMS = new int[]{0, 1, 2, 3};

    private static int[][] SELECTED_INDICES = new int[][]{
            new int[]{},
            new int[]{0}, new int[]{1}, new int[]{2},
            new int[]{0, 1}, new int[]{0, 2}, new int[]{1, 2},
            new int[]{0, 1, 2}
    };

    @DataPoints
    public static final TestData[] DATA;

    static {
        List<TestData> result = new ArrayList<TestData>();
        for (int nbItems : NB_ITEMS) {
            for (int[] selectedIndices : SELECTED_INDICES) {
                boolean isValid = true;
                for (int indice : selectedIndices) {
                    if (indice >= nbItems) {
                        isValid = false;
                        break;
                    }
                }

                if (isValid) {
                    result.add(new TestData(nbItems, selectedIndices));
                }
            }
        }
        DATA = result.toArray(new TestData[result.size()]);
    }

    @DataPoint
    public static final PanelFactory JLIST = new PanelFactory() {
        @Override
        public ListPanel<JList<String>, String> create(int nbItems, final boolean itemFactoryReturnsNull) {
            return new ListPanel<JList<String>, String>(new JList<String>(createItems(nbItems)), new Supplier<String>() {
                public String get() {
                    return itemFactoryReturnsNull ? null : NEW_ITEM;
                }
            });
        }

        public String toString() {
            return "PanelFactory<JList>";
        }
    };

    @Theory
    public final void testExtendsI18nAble(PanelFactory factory) throws Exception {
        ListPanel<JList<String>, String> component = buildAndShowWindow(factory, 1);
        assertTrue("component extends I18nAble", I18nAble.class.isAssignableFrom(component.getClass()));
    }


    @Test
    public void testInit_JList() throws Exception {
        final JList<String> list = GuiActionRunner.execute(new GuiQuery<JList<String>>() {
            @Override
            protected JList<String> executeInEDT() throws Throwable {
                return new JList<>(new DefaultMutableListModel<String>());
            }
        });
        ListPanel<JList<String>, String> panel = GuiActionRunner.execute(new GuiQuery<ListPanel<JList<String>, String>>() {
            @Override
            protected ListPanel<JList<String>, String> executeInEDT() throws Throwable {
                return new ListPanel<>(list, null);
            }
        });

        assertThat(panel.getListComponent()).isEqualTo(list);
    }

    @Test
    public void testInit_ListComponent() throws Exception {
        JList<String> list = GuiActionRunner.execute(new GuiQuery<JList<String>>() {
            @Override
            protected JList<String> executeInEDT() throws Throwable {
                return new JList<>(new DefaultMutableListModel<String>());
            }
        });
        final JListComponentWrapper<String> wrapper = new JListComponentWrapper<>(list, null);
        ListPanel<JList<String>, String> panel = GuiActionRunner.execute(new GuiQuery<ListPanel<JList<String>, String>>() {
            @Override
            protected ListPanel<JList<String>, String> executeInEDT() throws Throwable {
                return new ListPanel<>(wrapper);
            }
        });


        assertThat(panel.getListComponent()).isEqualTo(list);
    }

    @Theory
    public final void testI18nMessages(PanelFactory factory, Locale locale) throws Exception {
        ListPanel<JList<String>, String> component = buildAndShowWindow(factory, 1);
        Locale.setDefault(locale);
        component.updateMessages();

        window.button(ADD_BUTTON_NAME).requireToolTip(DesktopSwingComponentMessages_fr.getMessage(ADD_KEY));
        window.button(REMOVE_BUTTON_NAME).requireToolTip(DesktopSwingComponentMessages_fr.getMessage(REMOVE_KEY));
        window.button(UP_BUTTON_NAME).requireToolTip(DesktopSwingComponentMessages_fr.getMessage(UP_KEY));
        window.button(DOWN_BUTTON_NAME).requireToolTip(DesktopSwingComponentMessages_fr.getMessage(DOWN_KEY));
    }

    @Theory
    public void testAddItem(final PanelFactory factory, TestData data, boolean cancelAddItem) throws Exception {
        LOG.info("testAddItem: factory={} data={}", factory, data);
        List<String> expectedList = createItemList(data.nbItems);
        if (!cancelAddItem) {
            expectedList.add(NEW_ITEM);
        }

        buildAndShowWindow(factory, data.nbItems, cancelAddItem);
        selectItem(data.selectedIndices);
        window.button(ADD_BUTTON_NAME).click();

        window.button(ADD_BUTTON_NAME).requireEnabled().requireToolTip(SwingComponentMessages.ADD_MESSAGE);
        window.list().requireItemCount(expectedList.size());
        assertThat(window.list().contents()).containsOnly(expectedList.toArray());
    }

    @Theory
    public void testRemoveItem(PanelFactory factory, TestData data) throws Exception {
        LOG.info("testRemoveItem: factory={} data={}", factory, data);
        List<String> expectedList = createItemList(data.nbItems);
        boolean valid = (data.selectedIndices.length > 0);
        if (valid) {
            for (int i = data.selectedIndices.length - 1; i >= 0; i--) {
                expectedList.remove(data.selectedIndices[i]);
            }
        }

        buildAndShowWindow(factory, data.nbItems);
        selectItem(data.selectedIndices);
        window.button(REMOVE_BUTTON_NAME).requireToolTip(SwingComponentMessages.REMOVE_MESSAGE);
        if (valid) {
            window.button(REMOVE_BUTTON_NAME).requireEnabled();
            window.button(REMOVE_BUTTON_NAME).click();
        } else {
            window.button(REMOVE_BUTTON_NAME).requireDisabled();
        }

        window.list().requireItemCount(expectedList.size());
        assertThat(window.list().contents()).containsOnly(expectedList.toArray());
    }

    @Theory
    public void testMoveUpItem(PanelFactory factory, TestData data) throws Exception {
        LOG.info("testMoveUpItem: factory={} data={}", factory, data);
        List<String> expectedList = createItemList(data.nbItems);
        boolean valid = false;
        for (int selection : data.selectedIndices) {
            if (selection > 0) {
                String item = expectedList.remove(selection);
                expectedList.add(selection - 1, item);
                valid = true;
            }
        }

        buildAndShowWindow(factory, data.nbItems);
        selectItem(data.selectedIndices);
        window.button(UP_BUTTON_NAME).requireToolTip(SwingComponentMessages.MOVE_UP_MESSAGE);
        if (valid) {
            window.button(UP_BUTTON_NAME).requireEnabled();
            window.button(UP_BUTTON_NAME).click();
            int[] selections = ListPanel.moveIndicesUp(data.selectedIndices);
            if (selections.length > 0) {
                window.list().requireSelectedItems(selections);
            } else {
                window.list().requireNoSelection();
            }
        } else {
            window.button(UP_BUTTON_NAME).requireDisabled();
        }

        window.list().requireItemCount(expectedList.size());
        assertThat(window.list().contents()).containsOnly(expectedList.toArray());
    }

    @Theory
    public void testMoveDownItem(PanelFactory factory, TestData data) throws Exception {
        LOG.info("testMoveDownItem: factory={} data={}", factory, data);
        List<String> expectedList = createItemList(data.nbItems);
        boolean valid = false;
        for (int i = data.selectedIndices.length - 1; i >= 0; i--) {
            int selection = data.selectedIndices[i];
            if (selection < (data.nbItems - 1)) {
                String item = expectedList.remove(selection);
                expectedList.add(selection + 1, item);
                valid = true;
            }
        }

        buildAndShowWindow(factory, data.nbItems);
        selectItem(data.selectedIndices);
        window.button(DOWN_BUTTON_NAME).requireToolTip(SwingComponentMessages.MOVE_DOWN_MESSAGE);
        if (valid) {
            window.button(DOWN_BUTTON_NAME).requireEnabled();
            window.button(DOWN_BUTTON_NAME).click();
            int[] selections = ListPanel.moveIndicesDown(data.selectedIndices, data.nbItems);
            if (selections.length > 0) {
                window.list().requireSelectedItems(selections);
            } else {
                window.list().requireNoSelection();
            }
        } else {
            window.button(DOWN_BUTTON_NAME).requireDisabled();
        }

        window.list().requireItemCount(expectedList.size());
        assertThat(window.list().contents()).containsOnly(expectedList.toArray());
    }

    private void selectItem(int[] selection) {
        if (selection.length == 0) {
            window.list().component().clearSelection();
        } else {
            window.list().selectItems(selection);
        }
    }

    private ListPanel<JList<String>, String> buildAndShowWindow(final PanelFactory factory, final int nbItems) throws Exception {
        return buildAndShowWindow(factory, nbItems, false);
    }

    private ListPanel<JList<String>, String> buildAndShowWindow(final PanelFactory factory, final int nbItems, final boolean itemFactoryReturnsNull)
            throws Exception {
        return buildAndShowWindow(new Supplier<ListPanel<JList<String>, String>>() {
            @Override
            public ListPanel<JList<String>, String> get() {
                return factory.create(nbItems, itemFactoryReturnsNull);
            }
        });
    }

    static interface PanelFactory {
        ListPanel<JList<String>, String> create(int nbItems, boolean itemFactoryReturnsNull);
    }

    static List<String> createItemList(int nbItems) {
        List<String> items = new ArrayList<>();

        for (int i = 0; i < nbItems; i++) {
            items.add(String.valueOf((char) ('A' + i)));
        }

        return items;
    }

    static MutableListModel<String> createItems(int nbItems) {
        MutableListModel<String> items = new DefaultMutableListModel<>();

        for (String item : createItemList(nbItems)) {
            items.add(item);
        }

        return items;
    }

    static class TestData {
        final int nbItems;
        final int[] selectedIndices;

        public TestData(int nbItems, int[] selectedIndices) {
            this.nbItems = nbItems;
            this.selectedIndices = selectedIndices;
        }

        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder("TestData(nbItems=");
            buffer.append(nbItems).append(", selectedIndices=[");
            boolean first = true;
            for (int indice : selectedIndices) {
                if (!first) {
                    buffer.append(", ");
                }
                first = false;
                buffer.append(indice);
            }
            buffer.append("])");

            return buffer.toString();
        }
    }
}
