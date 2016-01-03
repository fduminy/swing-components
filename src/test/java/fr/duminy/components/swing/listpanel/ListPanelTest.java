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

import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import fr.duminy.components.swing.AbstractSwingTest;
import fr.duminy.components.swing.DesktopSwingComponentMessages_fr;
import fr.duminy.components.swing.SwingComponentMessages;
import fr.duminy.components.swing.i18n.I18nAble;
import fr.duminy.components.swing.list.DefaultMutableListModel;
import fr.duminy.components.swing.list.MutableListModel;
import org.assertj.core.api.AbstractIterableAssert;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.edt.GuiQuery;
import org.assertj.swing.edt.GuiTask;
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
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static fr.duminy.components.swing.DesktopSwingComponentMessages_fr.*;
import static fr.duminy.components.swing.TestUtilities.assertThatButtonIsAbsent;
import static fr.duminy.components.swing.TestUtilities.assertThatButtonIsPresent;
import static fr.duminy.components.swing.listpanel.EditingFeature.*;
import static fr.duminy.components.swing.listpanel.ManualOrderFeature.DOWN_BUTTON_NAME;
import static fr.duminy.components.swing.listpanel.ManualOrderFeature.UP_BUTTON_NAME;
import static fr.duminy.components.swing.listpanel.StandardListPanelFeature.EDITING;
import static fr.duminy.components.swing.listpanel.StandardListPanelFeature.MANUAL_ORDER;
import static org.assertj.swing.assertions.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(Theories.class)
public class ListPanelTest extends AbstractSwingTest {
    @DataPoint
    public static final Locale FRENCH = Locale.FRENCH;
    @DataPoint
    public static final Locale ENGLISH = AbstractItemActionTest.DEFAULT_LOCALE;

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

    public static class MyListPanel<TC extends JComponent, T> extends ListPanel<T, TC> {
        private Throwable updateItemThrown;
        private CountDownLatch countDownLatch = new CountDownLatch(1);

        public MyListPanel(JList<T> list, ItemManager<T> itemManager) {
            super(list, itemManager);
        }

        @Override
        void updateItem() {
            Handler h = new Handler() {
                @Override
                public void publish(LogRecord record) {
                    if (Level.SEVERE.equals(record.getLevel())) {
                        if (record.getThrown() != null) {
                            updateItemThrown = record.getThrown();
                        }
                    }
                }

                @Override
                public void flush() {
                }

                @Override
                public void close() throws SecurityException {
                }
            };

            // creates the logger if it doesn't (yet) exist
            java.util.logging.Logger l = java.util.logging.Logger.getLogger("com.google.common.util.concurrent.Futures$ImmediateFuture");

            Level oldLevel = l.getLevel();
            l.setLevel(Level.ALL);
            l.addHandler(h);

            try {
                super.updateItem();
            } catch (Exception e) {
                updateItemThrown = e;
            } finally {
                countDownLatch.countDown();
                l.removeHandler(h);
                l.setLevel(oldLevel);
            }
        }

        public void waitCallToUpdateItem() throws InterruptedException {
            countDownLatch.await();
        }

        public Throwable getUpdateItemThrown() {
            return updateItemThrown;
        }
    }

    @DataPoint
    public static final PanelFactory JLIST = new PanelFactory() {
        @Override
        public ListPanel<String, JList<String>> create(int nbItems, boolean itemFactoryReturnsNull) {
            return create(nbItems, itemFactoryReturnsNull, null);
        }

        @Override
        public ListPanel<String, JList<String>> create(int nbItems, final boolean itemManagerReturnsNull, ItemManager<String> itemManager) {
            if (itemManager == null) {
                itemManager = new MockItemManager(itemManagerReturnsNull);
            }
            return new MyListPanel<>(new JList<>(createItems(nbItems)), itemManager);
        }

        public String toString() {
            return "PanelFactory<JList>";
        }
    };

    public static class MockItemManager implements ItemManager<String> {
        private final boolean itemManagerReturnsNull;

        public MockItemManager(boolean itemManagerReturnsNull) {
            this.itemManagerReturnsNull = itemManagerReturnsNull;
        }

        @Override
        public ListenableFuture<String> createItem() {
            return createFuture(NEW_ITEM);
        }

        @Override
        public ListenableFuture<String> updateItem(String item) {
            return createFuture(UPDATED_ITEM);
        }

        private ListenableFuture<String> createFuture(String resultIfNotCancelled) {
            ListenableFuture<String> result;
            if (itemManagerReturnsNull) {
                result = Futures.immediateCancelledFuture();
            } else {
                result = Futures.immediateFuture(resultIfNotCancelled);
            }
            return result;
        }
    }

    @Before
    public void setUpBeforeTest() {
        AbstractItemActionTest.setDefaultLocale();
    }

    @Theory
    public final void testExtendsI18nAble(PanelFactory factory) throws Exception {
        ListPanel<String, JList<String>> component = buildAndShowWindow(factory, 1);
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
        ListPanel<String, JList<String>> panel = GuiActionRunner.execute(new GuiQuery<ListPanel<String, JList<String>>>() {
            @Override
            protected ListPanel<String, JList<String>> executeInEDT() throws Throwable {
                return new ListPanel<>(list, null);
            }
        });

        assertThat(panel.getListComponent()).isEqualTo(list);
        assertThatHasNoFeatures(panel);
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
        ListPanel<String, JList<String>> panel = GuiActionRunner.execute(new GuiQuery<ListPanel<String, JList<String>>>() {
            @Override
            protected ListPanel<String, JList<String>> executeInEDT() throws Throwable {
                return new ListPanel<>(wrapper);
            }
        });


        assertThat(panel.getListComponent()).isEqualTo(list);
        assertThatHasNoFeatures(panel);
    }

    @Theory
    public final void testI18nMessages(PanelFactory factory, Locale locale) throws Exception {
        final ListPanel<String, JList<String>> component = buildAndShowWindow(factory, 1, EDITING, MANUAL_ORDER);
        Locale.setDefault(locale);

        GuiActionRunner.execute(new GuiQuery<Void>() {
            @Override
            protected Void executeInEDT() throws Throwable {
                component.updateMessages();
                return null;
            }
        });

        window.button(ADD_BUTTON_NAME).requireToolTip(DesktopSwingComponentMessages_fr.getExpectedMessage(ADD_KEY));
        window.button(REMOVE_BUTTON_NAME).requireToolTip(DesktopSwingComponentMessages_fr.getExpectedMessage(REMOVE_KEY));
        window.button(UP_BUTTON_NAME).requireToolTip(DesktopSwingComponentMessages_fr.getExpectedMessage(UP_KEY));
        window.button(DOWN_BUTTON_NAME).requireToolTip(DesktopSwingComponentMessages_fr.getExpectedMessage(DOWN_KEY));
        window.button(UPDATE_BUTTON_NAME).requireToolTip(DesktopSwingComponentMessages_fr.getExpectedMessage(UPDATE_KEY));
    }

    @Theory
    public void testAddItem(final PanelFactory factory, TestData data, boolean cancelAddItem) throws Exception {
        LOG.info("testAddItem: factory={} data={}", factory, data);
        List<String> expectedList = createItemList(data.nbItems);
        if (!cancelAddItem) {
            expectedList.add(NEW_ITEM);
        }

        buildAndShowWindow(factory, data.nbItems, cancelAddItem, null, EDITING);
        selectItem(data.selectedIndices);
        window.button(ADD_BUTTON_NAME).click();
        robot().waitForIdle();

        window.button(ADD_BUTTON_NAME).requireEnabled().requireToolTip(SwingComponentMessages.ADD_ITEM_TOOLTIP);
        window.list().requireItemCount(expectedList.size());
        assertThat(window.list().contents()).containsOnly(toStringArray(expectedList));
    }

    @SuppressWarnings("unchecked")
    @Theory
    public void testUpdateItem_wrongItemManager(final PanelFactory factory) throws Exception {
        ItemManager itemManager = Mockito.mock(ItemManager.class);
        when(itemManager.updateItem(any(String.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return Futures.immediateFuture(invocation.getArguments()[0]);
            }
        });

        MyListPanel<JList<String>, String> panel = (MyListPanel<JList<String>, String>) buildAndShowWindow(factory, 1, false, (ItemManager<String>) itemManager, EDITING);

        window.list().selectItems(0);

        window.button(UPDATE_BUTTON_NAME).click();

        panel.waitCallToUpdateItem();
        assertThat(panel.getUpdateItemThrown()).isExactlyInstanceOf(IllegalStateException.class).hasMessage("The element returned by " + itemManager.getClass().getName() +
                ".updateItem(oldItem) must not be the same instance as oldItem");
    }

    @Theory
    public void testUpdateItem(final PanelFactory factory, TestData data, boolean cancelUpdateItem) throws Exception {
        LOG.info("testUpdateItem: factory={} data={}", factory, data);
        List<String> expectedList = createItemList(data.nbItems);

        if (expectedList.isEmpty() || (data.selectedIndices.length > 1) || (data.selectedIndices.length == 0)) {
            buildAndShowWindow(factory, data.nbItems, cancelUpdateItem, null, EDITING);
            selectItem(data.selectedIndices);

            window.button(UPDATE_BUTTON_NAME).requireDisabled().requireToolTip(SwingComponentMessages.UPDATE_ITEM_TOOLTIP);
        } else {
            int itemToUpdate = data.selectedIndices[0];
            String initialItem = expectedList.get(itemToUpdate);
            if (!cancelUpdateItem) {
                expectedList.set(itemToUpdate, UPDATED_ITEM);
            }

            buildAndShowWindow(factory, data.nbItems, cancelUpdateItem, null, EDITING);
            selectItem(data.selectedIndices);
            window.button(UPDATE_BUTTON_NAME).click();

            window.button(UPDATE_BUTTON_NAME).requireEnabled().requireToolTip(SwingComponentMessages.UPDATE_ITEM_TOOLTIP);
            String item = window.list().contents()[itemToUpdate];
            if (cancelUpdateItem) {
                assertThat(item).isSameAs(initialItem);
            } else {
                assertThat(item).isNotSameAs(initialItem);
            }
        }

        window.list().requireItemCount(expectedList.size());
        assertThat(window.list().contents()).containsOnly(toStringArray(expectedList));
    }

    public static String[] toStringArray(List<String> expectedList) {
        return expectedList.toArray(new String[expectedList.size()]);
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

        buildAndShowWindow(factory, data.nbItems, EDITING);
        selectItem(data.selectedIndices);
        window.button(REMOVE_BUTTON_NAME).requireToolTip(SwingComponentMessages.REMOVE_ITEM_TOOLTIP);
        if (valid) {
            window.button(REMOVE_BUTTON_NAME).requireEnabled();
            window.button(REMOVE_BUTTON_NAME).click();
        } else {
            window.button(REMOVE_BUTTON_NAME).requireDisabled();
        }

        window.list().requireItemCount(expectedList.size());
        assertThat(window.list().contents()).containsOnly(toStringArray(expectedList));
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

        buildAndShowWindow(factory, data.nbItems, MANUAL_ORDER);
        selectItem(data.selectedIndices);
        window.button(UP_BUTTON_NAME).requireToolTip(SwingComponentMessages.MOVE_UP_ITEM_TOOLTIP);
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
        assertThat(window.list().contents()).containsOnly(toStringArray(expectedList));
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

        buildAndShowWindow(factory, data.nbItems, MANUAL_ORDER);
        selectItem(data.selectedIndices);
        window.button(DOWN_BUTTON_NAME).requireToolTip(SwingComponentMessages.MOVE_DOWN_ITEM_TOOLTIP);
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
        assertThat(window.list().contents()).containsOnly(toStringArray(expectedList));
    }

    @SuppressWarnings("unchecked")
    @Theory
    public void testAddUserButton(PanelFactory factory) throws Exception {
        int nbItems = 2;
        final ListPanel<String, JList<String>> component = buildAndShowWindow(factory, nbItems);
        int selectedIndex = 1;
        String item = component.getListComponent().getModel().getElementAt(selectedIndex);
        final AbstractUserItemAction<String, ?> action = mock(AbstractUserItemAction.class);
        when(action.isEnabled()).thenReturn(true);
        doCallRealMethod().when(action).setListener(any(ListActions.class));
        action.setListener(component.getListActions());
        final String buttonName = "myButton";

        GuiActionRunner.execute(new GuiQuery<Void>() {
            @Override
            protected Void executeInEDT() throws Throwable {
                component.addUserButton(buttonName, action);
                window.target().pack();
                window.target().invalidate();
                return null;
            }
        });

        window.list().selectItem(selectedIndex);
        window.button(buttonName).requireVisible().requireEnabled().click();

        InOrder inOrder = Mockito.inOrder(action);
        inOrder.verify(action, times(1)).updateState(eq(new int[]{}), eq(nbItems));
        inOrder.verify(action, times(1)).updateState(eq(new int[]{selectedIndex}), eq(nbItems));
        inOrder.verify(action, times(1)).executeAction(eq(item));
        inOrder.verifyNoMoreInteractions();
    }

    @Theory
    public void testAddFeature(PanelFactory factory, StandardListPanelFeature feature) throws Exception {
        ListPanel<String, JList<String>> panel = buildAndShowWindow(factory, 2);

        addFeatureInEDT(panel, feature);

        switch (feature) {
            case EDITING:
                assertThatButtonIsAbsent(window, UP_BUTTON_NAME);
                assertThatButtonIsAbsent(window, DOWN_BUTTON_NAME);
                assertThatButtonIsPresent(window, ADD_BUTTON_NAME);
                assertThatButtonIsPresent(window, REMOVE_BUTTON_NAME);
                assertThatButtonIsPresent(window, UPDATE_BUTTON_NAME);
                break;

            case MANUAL_ORDER:
                assertThatButtonIsPresent(window, UP_BUTTON_NAME);
                assertThatButtonIsPresent(window, DOWN_BUTTON_NAME);
                assertThatButtonIsAbsent(window, ADD_BUTTON_NAME);
                assertThatButtonIsAbsent(window, REMOVE_BUTTON_NAME);
                assertThatButtonIsAbsent(window, UPDATE_BUTTON_NAME);
                break;
        }
    }

    @Theory
    public void testRemoveFeature(PanelFactory factory, StandardListPanelFeature feature) throws Exception {
        ListPanel<String, JList<String>> panel = buildAndShowWindow(factory, 2);

        addFeatureInEDT(panel, feature);

        removeFeatureInEDT(panel, feature);

        assertThatButtonIsAbsent(window, UP_BUTTON_NAME);
        assertThatButtonIsAbsent(window, DOWN_BUTTON_NAME);
        assertThatButtonIsAbsent(window, ADD_BUTTON_NAME);
        assertThatButtonIsAbsent(window, REMOVE_BUTTON_NAME);
        assertThatButtonIsAbsent(window, UPDATE_BUTTON_NAME);
    }

    @Theory
    public void testHasFeature(PanelFactory factory, StandardListPanelFeature feature) throws Exception {
        ListPanel<String, JList<String>> panel = buildAndShowWindow(factory, 2);

        assertThatHasNoFeatures(panel);

        addFeatureInEDT(panel, feature);
        assertThatHasOnlyFeatures(panel, feature);

        removeFeatureInEDT(panel, feature);
        assertThatHasNoFeatures(panel);
    }

    private void selectItem(int[] selection) {
        if (selection.length == 0) {
            window.list().target().clearSelection();
        } else {
            window.list().selectItems(selection);
        }
    }

    private ListPanel<String, JList<String>> buildAndShowWindow(final PanelFactory factory, final int nbItems, StandardListPanelFeature... features) throws Exception {
        return buildAndShowWindow(factory, nbItems, false, null, features);
    }

    private ListPanel<String, JList<String>> buildAndShowWindow(final PanelFactory factory, final int nbItems, final boolean itemFactoryReturnsNull, final ItemManager<String> itemManager, final StandardListPanelFeature... features)
            throws Exception {
        return buildAndShowWindow(new Supplier<ListPanel<String, JList<String>>>() {
            @Override
            public ListPanel<String, JList<String>> get() {
                ListPanel<String, JList<String>> result;
                if (itemManager != null) {
                    result = factory.create(nbItems, itemFactoryReturnsNull, itemManager);
                } else {
                    result = factory.create(nbItems, itemFactoryReturnsNull);
                }
                for (StandardListPanelFeature feature : features) {
                    result.addFeature(feature);
                }
                return result;
            }
        });
    }

    static interface PanelFactory {
        ListPanel<String, JList<String>> create(int nbItems, boolean itemFactoryReturnsNull);

        ListPanel<String, JList<String>> create(int nbItems, boolean itemFactoryReturnsNull, ItemManager<String> manager);
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

    private void assertThatHasNoFeatures(ListPanel<?, ?> panel) {
        assertThatHasOnlyFeatures(panel);
    }

    private void assertThatHasOnlyFeatures(ListPanel<?, ?> panel, StandardListPanelFeature... expectedFeatures) {
        EnumSet<StandardListPanelFeature> actualFeatures = getActualFeatures(panel);
        AbstractIterableAssert listAssert = assertThat(actualFeatures).as("actual features");
        if (expectedFeatures.length == 0) {
            listAssert.isEmpty();
        } else {
            listAssert.isEqualTo(EnumSet.copyOf(Arrays.asList(expectedFeatures)));
        }
    }

    public static EnumSet<StandardListPanelFeature> getActualFeatures(ListPanel<?, ?> panel) {
        List<StandardListPanelFeature> actualFeatures = new ArrayList<>();
        for (StandardListPanelFeature feature : StandardListPanelFeature.values()) {
            if (panel.hasFeature(feature)) {
                actualFeatures.add(feature);
            }
        }

        return copyOf(StandardListPanelFeature.class, actualFeatures.toArray(new StandardListPanelFeature[actualFeatures.size()]));
    }

    public static <T extends Enum<T>> EnumSet<T> copyOf(Class<T> enumClass, T[] values) {
        if (values == null) {
            return null;
        }
        if (values.length == 0) {
            return EnumSet.noneOf(enumClass);
        }
        if (values.length == 1) {
            return EnumSet.of(values[0]);
        }
        return EnumSet.of(values[0], Arrays.copyOfRange(values, 1, values.length));
    }

    private void addFeatureInEDT(final ListPanel<String, JList<String>> panel, final StandardListPanelFeature feature) {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                panel.addFeature(feature);
            }
        });
    }

    private void removeFeatureInEDT(final ListPanel<String, JList<String>> panel, final StandardListPanelFeature feature) {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                panel.removeFeature(feature);
            }
        });
    }
}
