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
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.JButtonFixture;
import org.junit.Before;
import org.junit.Test;
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
    private static final int SELECTED_INDEX = 1;
    private static final String LINE1 = "line1";
    private static final String LINE2 = "line2";

    private DefaultMutableListModel<String> listModel;
    private JList<String> list;
    private ListPanelFixture<JList<String>, String> fixture;
    private MockItemManager itemManager;
    private SimpleItemManager.FormDisplayer displayer;

    @SuppressWarnings("unchecked")
    @Before
    public void runBeforeTest() throws Exception {
        final ListPanel[] panel = new ListPanel[1];
        Supplier<JPanel> supplier = new Supplier<JPanel>() {
            @Override
            public JPanel get() {
                JPanel parentComponent = new JPanel(new BorderLayout());

                listModel = new DefaultMutableListModel<>();
                listModel.add(LINE1);
                listModel.add(LINE2);
                list = new JList<>(listModel);
                list.setSelectedIndex(SELECTED_INDEX);
                itemManager = Mockito.spy(new MockItemManager(false));
                panel[0] = new ListPanel<JList<String>, String>(list, itemManager);

                parentComponent.add(panel[0]);
                return parentComponent;
            }
        };
        buildAndShowWindow(supplier);
        fixture = new ListPanelFixture<JList<String>, String>(robot(), panel[0]);
    }

    @Test
    public void testAddButton() throws Exception {
        JButtonFixture buttonFixture = fixture.addButton();

        assertThat(buttonFixture).isNotNull();
        assertThat(buttonFixture.component().getName()).isEqualTo(ADD_BUTTON_NAME);
        buttonFixture.click();
        assertThat(listModel.size()).isEqualTo(3);
        String newLine = listModel.get(2);
        assertThat(newLine).isNotEqualTo(LINE1).isNotEqualTo(LINE2);
    }

    @Test
    public void testRemoveButton() throws Exception {
        JButtonFixture buttonFixture = fixture.removeButton();

        assertThat(buttonFixture).isNotNull();
        assertThat(buttonFixture.component().getName()).isEqualTo(REMOVE_BUTTON_NAME);
        buttonFixture.click();
        assertThat(listModel.size()).isEqualTo(1);
        assertThat(listModel.get(0)).isSameAs(LINE1);
    }

    @Test
    public void testUpButton() throws Exception {
        JButtonFixture buttonFixture = fixture.upButton();

        assertThat(buttonFixture).isNotNull();
        assertThat(buttonFixture.component().getName()).isEqualTo(UP_BUTTON_NAME);
        buttonFixture.click();
        assertThat(listModel.get(0)).isSameAs(LINE2);
        assertThat(listModel.get(1)).isSameAs(LINE1);
    }

    @Test
    public void testDownButton() throws Exception {
        GuiActionRunner.execute(new GuiTask() {
            protected void executeInEDT() {
                list.setSelectedIndex(0);
            }
        });

        JButtonFixture buttonFixture = fixture.downButton();

        assertThat(buttonFixture).isNotNull();
        assertThat(buttonFixture.component().getName()).isEqualTo(DOWN_BUTTON_NAME);
        buttonFixture.click();
        assertThat(listModel.get(0)).isSameAs(LINE2);
        assertThat(listModel.get(1)).isSameAs(LINE1);
    }

    @Test
    public void testUpdateButton() throws Exception {
        JButtonFixture buttonFixture = fixture.updateButton();

        assertThat(buttonFixture).isNotNull();
        assertThat(buttonFixture.component().getName()).isEqualTo(UPDATE_BUTTON_NAME);
        buttonFixture.click();
        verify(itemManager, times(1)).updateItem(eq(LINE2));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUserButton() throws Exception {
        final ListPanel<JList<String>, String> listPanel = (ListPanel<JList<String>, String>) fixture.component();
        final String buttonName = "userButton";
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

        JButtonFixture buttonFixture = fixture.userButton(buttonName);

        assertThat(buttonFixture).as("buttonFixture").isNotNull();
        assertThat(buttonFixture.component().getName()).as("buttonName").isEqualTo(buttonName);
        assertThat(buttonFixture.component().getAction()).as("buttonAction").isSameAs(buttonAction);
        buttonFixture.click();
        verify(buttonAction, times(1)).executeAction(eq(LINE2));
    }
}
