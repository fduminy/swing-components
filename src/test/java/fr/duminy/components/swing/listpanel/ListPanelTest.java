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
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(Theories.class)
public class ListPanelTest extends AbstractSwingTest {
    @DataPoint
    public static final Locale FRENCH = Locale.FRENCH;
    @DataPoint
    public static final Locale ENGLISH = AbstractItemActionTest.DEFAULT_LOCALE;

    @DataPoint
    public static final boolean DO_OPERATION = false;
    @DataPoint
    public static final boolean CANCEL_OPERATION = true;

    private static final Logger LOG = LoggerFactory.getLogger(ListPanelTest.class);

    private static final String NEW_ITEM = "Z";
    private static final String UPDATED_ITEM = "Z_UPDATED";

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

    public static class MyListPanel<TC extends JComponent, T> extends ListPanel<TC, T> {
        private Exception updateItemException;
        private int nbCallsToUpdateItem;

        public MyListPanel(JList<T> list, ItemManager<T> itemManager) {
            super(list, itemManager);
        }

        @Override
        public void updateItem() {
            try {
                nbCallsToUpdateItem++;
                super.updateItem();
            } catch (Exception e) {
                updateItemException = e;
            }
        }

        public int getNbCallsToUpdateItem() {
            return nbCallsToUpdateItem;
        }

        public Exception getUpdateItemException() {
            return updateItemException;
        }
    }

    @DataPoint
    public static final PanelFactory JLIST = new PanelFactory() {
        @Override
        public ListPanel<JList<String>, String> create(int nbItems, boolean itemFactoryReturnsNull) {
            return create(nbItems, itemFactoryReturnsNull, null);
        }

        @Override
        public ListPanel<JList<String>, String> create(int nbItems, final boolean itemManagerReturnsNull, ItemManager<String> itemManager) {
            if (itemManager == null) {
                itemManager = new ItemManager<String>() {
                    @Override
                    public String createItem() {
                        return itemManagerReturnsNull ? null : NEW_ITEM;
                    }

                    @Override
                    public String updateItem(String item) {
                        return itemManagerReturnsNull ? null : UPDATED_ITEM;
                    }
                };
            }
            return new MyListPanel<>(new JList<>(createItems(nbItems)), itemManager);
        }

        public String toString() {
            return "PanelFactory<JList>";
        }
    };

    @Before
    public void setUpBeforeTest() {
        AbstractItemActionTest.setDefaultLocale();
    }

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
        window.button(UPDATE_BUTTON_NAME).requireToolTip(DesktopSwingComponentMessages_fr.getMessage(UPDATE_KEY));
    }

    @Theory
    public void testAddItem(final PanelFactory factory, TestData data, boolean cancelAddItem) throws Exception {
        LOG.info("testAddItem: factory={} data={}", factory, data);
        List<String> expectedList = createItemList(data.nbItems);
        if (!cancelAddItem) {
            expectedList.add(NEW_ITEM);
        }

        buildAndShowWindow(factory, data.nbItems, cancelAddItem, null);
        selectItem(data.selectedIndices);
        window.button(ADD_BUTTON_NAME).click();

        window.button(ADD_BUTTON_NAME).requireEnabled().requireToolTip(SwingComponentMessages.ADD_MESSAGE);
        window.list().requireItemCount(expectedList.size());
        assertThat(window.list().contents()).containsOnly(expectedList.toArray());
    }

    @SuppressWarnings("unchecked")
    @Theory
    public void testUpdateItem_wrongItemManager(final PanelFactory factory) throws Exception {
        ItemManager itemManager = Mockito.mock(ItemManager.class);
        when(itemManager.updateItem(any(String.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0];
            }
        });

        MyListPanel<JList<String>, String> panel = (MyListPanel<JList<String>, String>) buildAndShowWindow(factory, 1, false, (ItemManager<String>) itemManager);

        window.list().selectItems(0);

        window.button(UPDATE_BUTTON_NAME).click();

        assertThat(panel.getNbCallsToUpdateItem()).as("nbCallsToUpdateItem").isEqualTo(1);
        assertThat(panel.getUpdateItemException()).isExactlyInstanceOf(IllegalStateException.class).hasMessage("The element returned by " + itemManager.getClass().getName() +
                ".updateItem(oldItem) must not be the same instance as oldItem");
    }

    @Theory
    public void testUpdateItem(final PanelFactory factory, TestData data, boolean cancelUpdateItem) throws Exception {
        LOG.info("testUpdateItem: factory={} data={}", factory, data);
        List<String> expectedList = createItemList(data.nbItems);

        if (expectedList.isEmpty() || (data.selectedIndices.length > 1) || (data.selectedIndices.length == 0)) {
            buildAndShowWindow(factory, data.nbItems, cancelUpdateItem, null);
            selectItem(data.selectedIndices);

            window.button(UPDATE_BUTTON_NAME).requireDisabled().requireToolTip(SwingComponentMessages.UPDATE_MESSAGE);
        } else {
            int itemToUpdate = data.selectedIndices[0];
            String initialItem = expectedList.get(itemToUpdate);
            if (!cancelUpdateItem) {
                expectedList.set(itemToUpdate, UPDATED_ITEM);
            }

            buildAndShowWindow(factory, data.nbItems, cancelUpdateItem, null);
            selectItem(data.selectedIndices);
            window.button(UPDATE_BUTTON_NAME).click();

            window.button(UPDATE_BUTTON_NAME).requireEnabled().requireToolTip(SwingComponentMessages.UPDATE_MESSAGE);
            String item = window.list().contents()[itemToUpdate];
            if (cancelUpdateItem) {
                assertThat(item).isSameAs(initialItem);
            } else {
                assertThat(item).isNotSameAs(initialItem);
            }
        }

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

    @SuppressWarnings("unchecked")
    @Theory
    public void testAddUserButton(PanelFactory factory) throws Exception {
        int nbItems = 2;
        final ListPanel<JList<String>, String> component = buildAndShowWindow(factory, nbItems);
        int selectedIndex = 1;
        String item = component.getListComponent().getModel().getElementAt(selectedIndex);
        final AbstractUserItemAction<String, ?> action = mock(AbstractUserItemAction.class);
        when(action.isEnabled()).thenReturn(true);
        doCallRealMethod().when(action).setListener(any(ListActions.class));
        action.setListener(component);
        final String buttonName = "myButton";

        GuiActionRunner.execute(new GuiQuery<Object>() {
            @Override
            protected Object executeInEDT() throws Throwable {
                component.addUserButton(buttonName, action);
                window.component().pack();
                window.component().invalidate();
                return null;
            }
        });

        window.list().selectItem(selectedIndex);
        window.button(buttonName).requireVisible().requireEnabled().click();

        InOrder inOrder = Mockito.inOrder(action);
        inOrder.verify(action, times(1)).updateState(eq(new int[]{selectedIndex}), eq(nbItems));
        inOrder.verify(action, times(1)).executeAction(eq(item));
        inOrder.verifyNoMoreInteractions();
    }

    private void selectItem(int[] selection) {
        if (selection.length == 0) {
            window.list().component().clearSelection();
        } else {
            window.list().selectItems(selection);
        }
    }

    private ListPanel<JList<String>, String> buildAndShowWindow(final PanelFactory factory, final int nbItems) throws Exception {
        return buildAndShowWindow(factory, nbItems, false, null);
    }

    private ListPanel<JList<String>, String> buildAndShowWindow(final PanelFactory factory, final int nbItems, final boolean itemFactoryReturnsNull, final ItemManager<String> itemManager)
            throws Exception {
        return buildAndShowWindow(new Supplier<ListPanel<JList<String>, String>>() {
            @Override
            public ListPanel<JList<String>, String> get() {
                ListPanel<JList<String>, String> result;
                if (itemManager != null) {
                    result = factory.create(nbItems, itemFactoryReturnsNull, itemManager);
                } else {
                    result = factory.create(nbItems, itemFactoryReturnsNull);
                }
                return result;
            }
        });
    }

    static interface PanelFactory {
        ListPanel<JList<String>, String> create(int nbItems, boolean itemFactoryReturnsNull);

        ListPanel<JList<String>, String> create(int nbItems, boolean itemFactoryReturnsNull, ItemManager<String> manager);
    }

    static List<String> createItemList(int nbItems) {
        List<String> items = new ArrayList<>();

        for (int i = 0; i < nbItems; i++) {
            // Attention : some tests depends on the call to String.intern() (comparison of references) 
            items.add(String.valueOf((char) ('A' + i)).intern());
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
