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

import com.google.common.collect.ImmutableList;
import org.assertj.core.api.Assertions;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.edt.GuiQuery;
import org.junit.Test;

import java.util.Collection;

import static fr.duminy.components.swing.AbstractFormTest.Bean;
import static org.mockito.Mockito.mock;

public class ButtonsPanelTest {
    @SuppressWarnings("unchecked")
    @Test
    public void testGetActions_returnsImmutableList() {
        ButtonsPanel<Bean> buttonsPanel = GuiActionRunner.execute(new GuiQuery<ButtonsPanel<Bean>>() {
            @Override
            protected ButtonsPanel<Bean> executeInEDT() throws Throwable {
                return new ButtonsPanel<Bean>(mock(ListActions.class));
            }
        });

        Collection<ListAction> actions = buttonsPanel.getActions();

        Assertions.assertThat((Object) actions).isInstanceOf(ImmutableList.class);
    }
}