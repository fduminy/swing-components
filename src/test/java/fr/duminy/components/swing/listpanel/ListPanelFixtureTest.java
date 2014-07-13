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
import fr.duminy.components.swing.AbstractFormTest;
import fr.duminy.components.swing.list.DefaultMutableListModel;
import fr.duminy.components.swing.path.JPath;
import fr.duminy.components.swing.path.JPathFixture;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.exception.ComponentLookupException;
import org.fest.swing.fixture.JButtonFixture;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;

import static fr.duminy.components.swing.listpanel.ButtonsPanel.*;
import static fr.duminy.components.swing.listpanel.ListPanelTest.MockItemManager;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for class {@link fr.duminy.components.swing.listpanel.ListPanelFixture}.
 */
public class ListPanelFixtureTest extends AbstractFormTest {
    private static final String COMPONENT_NAME = "listPanelComponent";
    private static final int SELECTED_INDEX = 1;
    private static final String LINE1 = "line1";
    private static final String LINE2 = "line2";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testConstructor_nameArg_noMatch() {
        thrown.expect(ComponentLookupException.class);
        thrown.expectMessage("Unable to find a ListPanel with name '" + COMPONENT_NAME + "'");

        new ListPanelFixture(robot(), COMPONENT_NAME);
    }

    @Test
    public void testConstructor_nameArg_onlyOneMatch() throws Exception {
        buildAndShowList(false);

        new ListPanelFixture(robot(), COMPONENT_NAME);
    }

    @Test
    public void testConstructor_nameArg_multipleMatches() throws Exception {
        Supplier<JPanel> supplier = new Supplier<JPanel>() {
            @Override
            public JPanel get() {
                final JPath jPath1 = new JPath(JPath.SelectionMode.FILES_AND_DIRECTORIES);
                jPath1.setName(COMPONENT_NAME);
                final JPath jPath2 = new JPath(JPath.SelectionMode.FILES_AND_DIRECTORIES);
                jPath2.setName(COMPONENT_NAME);

                JPanel jPanel = new JPanel(new GridLayout(2, 1));
                jPanel.add(jPath1);
                jPanel.add(jPath2);
                return jPanel;
            }
        };
        buildAndShowWindow(supplier);
        thrown.expect(ComponentLookupException.class);
        thrown.expectMessage("There are duplicates JPath with name '" + COMPONENT_NAME + "'");

        new JPathFixture(robot(), COMPONENT_NAME);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testConstructor_listpanelArg_null() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Target component should not be null");

        new ListPanelFixture<String, JList<String>>(robot(), (ListPanel) null);
    }

    @Test
    public void testConstructor_listpanelArg_notNull() throws Exception {
        final ListPanel<String, JList<String>> listPanel = GuiActionRunner.execute(new GuiQuery<ListPanel<String, JList<String>>>() {
            @SuppressWarnings("unchecked")
            protected ListPanel<String, JList<String>> executeInEDT() {
                return new ListPanel<>(new JList<>(new DefaultMutableListModel<String>()), Mockito.mock(ItemManager.class));
            }
        });

        ListPanelFixture<String, JList<String>> fixture = new ListPanelFixture<>(robot(), listPanel);

        assertThat(fixture.component()).isSameAs(listPanel);
    }

    @Test
    public void testAddButton() throws Exception {
        ListData listData = buildAndShowList();
        JButtonFixture buttonFixture = listData.fixture.addButton();

        assertThat(buttonFixture).isNotNull();
        assertThat(buttonFixture.component().getName()).isEqualTo(ADD_BUTTON_NAME);
        buttonFixture.click();
        assertThat(listData.listModel.size()).isEqualTo(3);
        String newLine = listData.listModel.get(2);
        assertThat(newLine).isNotEqualTo(LINE1).isNotEqualTo(LINE2);
    }

    @Test
    public void testRemoveButton() throws Exception {
        ListData listData = buildAndShowList();
        JButtonFixture buttonFixture = listData.fixture.removeButton();

        assertThat(buttonFixture).isNotNull();
        assertThat(buttonFixture.component().getName()).isEqualTo(REMOVE_BUTTON_NAME);
        buttonFixture.click();
        assertThat(listData.listModel.size()).isEqualTo(1);
        assertThat(listData.listModel.get(0)).isSameAs(LINE1);
    }

    @Test
    public void testUpButton() throws Exception {
        ListData listData = buildAndShowList();
        JButtonFixture buttonFixture = listData.fixture.upButton();

        assertThat(buttonFixture).isNotNull();
        assertThat(buttonFixture.component().getName()).isEqualTo(UP_BUTTON_NAME);
        buttonFixture.click();
        assertThat(listData.listModel.get(0)).isSameAs(LINE2);
        assertThat(listData.listModel.get(1)).isSameAs(LINE1);
    }

    @Test
    public void testDownButton() throws Exception {
        final ListData listData = buildAndShowList();
        GuiActionRunner.execute(new GuiTask() {
            protected void executeInEDT() {
                listData.list.setSelectedIndex(0);
            }
        });

        JButtonFixture buttonFixture = listData.fixture.downButton();

        assertThat(buttonFixture).isNotNull();
        assertThat(buttonFixture.component().getName()).isEqualTo(DOWN_BUTTON_NAME);
        buttonFixture.click();
        assertThat(listData.listModel.get(0)).isSameAs(LINE2);
        assertThat(listData.listModel.get(1)).isSameAs(LINE1);
    }

    @Test
    public void testUpdateButton() throws Exception {
        ListData listData = buildAndShowList();
        JButtonFixture buttonFixture = listData.fixture.updateButton();

        assertThat(buttonFixture).isNotNull();
        assertThat(buttonFixture.component().getName()).isEqualTo(UPDATE_BUTTON_NAME);
        buttonFixture.click();
        verify(listData.itemManager, times(1)).updateItem(eq(LINE2));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUserButton() throws Exception {
        ListData listData = buildAndShowList();
        final ListPanel<String, JList<String>> targetListPanel = listData.supplierWithNoise.getTargetComponent();
        final ListPanel<String, JList<String>> noiseListPanel = listData.supplierWithNoise.getNoiseComponent();
        final String buttonName = "userButton";
        final AbstractUserItemAction<String, ?> targetButtonAction = addUserButton(targetListPanel, buttonName);
        final AbstractUserItemAction<String, ?> noiseButtonAction = addUserButton(noiseListPanel, buttonName);

        JButtonFixture buttonFixture = listData.fixture.userButton(buttonName);

        assertThat(buttonFixture).as("buttonFixture").isNotNull();
        assertThat(buttonFixture.component().getName()).as("buttonName").isEqualTo(buttonName);
        assertThat(buttonFixture.component().getAction()).as("buttonAction").isSameAs(targetButtonAction);
        buttonFixture.click();
        verify(noiseButtonAction, never()).executeAction(any(String.class));
        verify(targetButtonAction, times(1)).executeAction(eq(LINE2));
    }

    @SuppressWarnings("unchecked")
    private AbstractUserItemAction<String, ?> addUserButton(final ListPanel<String, JList<String>> listPanel, final String buttonName) {
        final AbstractUserItemAction<String, ?> buttonAction = mock(AbstractUserItemAction.class);
        when(buttonAction.isEnabled()).thenReturn(true);
        doCallRealMethod().when(buttonAction).setListener(any(ListActions.class));
        buttonAction.setListener(listPanel);
        GuiActionRunner.execute(new GuiTask() {
            protected void executeInEDT() {
                listPanel.addUserButton(buttonName, buttonAction);
                window.component().pack();
                window.component().invalidate();
            }
        });
        return buttonAction;
    }

    public ListData buildAndShowList() throws Exception {
        return buildAndShowList(true);
    }

    @SuppressWarnings("unchecked")
    public ListData buildAndShowList(boolean withNoise) throws Exception {
        final DefaultMutableListModel[] listModel = new DefaultMutableListModel[1];
        final JList[] list = new JList[1];
        final MockItemManager[] itemManager = new MockItemManager[1];
        Supplier<ListPanel<String, JList<String>>> supplier = new Supplier<ListPanel<String, JList<String>>>() {
            @Override
            public ListPanel<String, JList<String>> get() {
                listModel[0] = new DefaultMutableListModel<String>();
                listModel[0].add(LINE1);
                listModel[0].add(LINE2);
                list[0] = new JList<String>(listModel[0]);
                list[0].setSelectedIndex(SELECTED_INDEX);
                itemManager[0] = Mockito.spy(new MockItemManager(false));

                final ListPanel<String, JList<String>> listPanel = new ListPanel<>(list[0], itemManager[0]);
                listPanel.setName(COMPONENT_NAME);
                return listPanel;
            }
        };

        if (withNoise) {
            SupplierWithNoise<ListPanel<String, JList<String>>> supplierWithNoise = buildAndShowComponentWithNoise(supplier);
            ListPanelFixture<String, JList<String>> fixture = new ListPanelFixture<>(robot(), supplierWithNoise.getTargetComponent());

            return new ListData(listModel[0], list[0], fixture, itemManager[0], supplierWithNoise);
        } else {
            ListPanel<String, JList<String>> listPanel = buildAndShowWindow(supplier);
            ListPanelFixture<String, JList<String>> fixture = new ListPanelFixture<>(robot(), listPanel);

            return new ListData(listModel[0], list[0], fixture, itemManager[0], null);
        }
    }

    private static class ListData {
        private final DefaultMutableListModel<String> listModel;
        private final JList<String> list;
        private final ListPanelFixture<String, JList<String>> fixture;
        private final MockItemManager itemManager;
        private final SupplierWithNoise<ListPanel<String, JList<String>>> supplierWithNoise;

        private ListData(DefaultMutableListModel<String> listModel, JList<String> list, ListPanelFixture<String, JList<String>> fixture, MockItemManager itemManager, SupplierWithNoise<ListPanel<String, JList<String>>> supplierWithNoise) {
            this.listModel = listModel;
            this.list = list;
            this.fixture = fixture;
            this.itemManager = itemManager;
            this.supplierWithNoise = supplierWithNoise;
        }
    }
}
