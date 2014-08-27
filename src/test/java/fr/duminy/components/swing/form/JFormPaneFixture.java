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
package fr.duminy.components.swing.form;

import fr.duminy.components.swing.FixtureUtilities;
import fr.duminy.components.swing.listpanel.ListPanel;
import fr.duminy.components.swing.listpanel.ListPanelFixture;
import fr.duminy.components.swing.path.JPath;
import fr.duminy.components.swing.path.JPathFixture;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.core.Robot;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JPanelFixture;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import static org.junit.Assert.fail;

/**
 * A fixture to help testing a {@link fr.duminy.components.swing.form.JFormPane}.
 */
public class JFormPaneFixture extends JPanelFixture {
    public JFormPaneFixture(Robot robot, String panelName) {
        super(robot, FixtureUtilities.find(robot, JFormPane.class, panelName));
    }

    public JFormPaneFixture(Robot robot, Class<?> beanClass) {
        super(robot, find(robot, beanClass));
    }

    public JButtonFixture okButton() {
        return button(JFormPane.OK_BUTTON_NAME);
    }

    public JButtonFixture cancelButton() {
        return button(JFormPane.CANCEL_BUTTON_NAME);
    }

    public JFormPaneFixture requireInDialog(boolean expectInADialog) {
        boolean actuallyInDialog = false;

        JFormPane formPane = form();
        Window window = SwingUtilities.getWindowAncestor(formPane);
        if (window instanceof JDialog) {
            if (((JDialog) window).getContentPane().equals(formPane)) {
                actuallyInDialog = true;
            }
        }

        if (actuallyInDialog && !expectInADialog) {
            fail("The form '" + component().getName() + "' must not be in a dialog");
        } else if (!actuallyInDialog && expectInADialog) {
            fail("The form '" + component().getName() + "' must be in a dialog");
        }

        return this;
    }

    public JFormPaneFixture requireModeCreate() {
        JFormPane formPane = form();
        if (!JFormPane.Mode.CREATE.equals(formPane.getMode())) {
            fail("The form '" + formPane.getName() + "' must be in CREATE mode");
        }
        return this;
    }

    public JFormPaneFixture requireModeUpdate() {
        JFormPane formPane = form();
        if (!JFormPane.Mode.UPDATE.equals(formPane.getMode())) {
            fail("The form '" + formPane.getName() + "' must be in UPDATE mode");
        }
        return this;
    }

    public JFormPaneFixture requireTitle(String expectedTitle) {
        JFormPane formPane = form();
        String actualTitle = formPane.getTitle();
        if (!Objects.equals(expectedTitle, actualTitle)) {
            fail(String.format("The form '%s' must have title %s but has title %s", formPane.getName(),
                    (expectedTitle == null) ? "null" : "'" + expectedTitle + "'",
                    (actualTitle == null) ? "null" : "'" + actualTitle + "'"));
        }
        return this;
    }

    /**
     * Creates a {@link fr.duminy.components.swing.path.JPathFixture} for a single {@link fr.duminy.components.swing.path.JPath}.
     */
    public JPathFixture path() {
        return new JPathFixture(robot, findByType(JPath.class));
    }

    /**
     * Creates a {@link fr.duminy.components.swing.path.JPathFixture} from a {@link fr.duminy.components.swing.path.JPath} matcher.
     */
    public JPathFixture path(GenericTypeMatcher<? extends JPath> matcher) {
        return new JPathFixture(robot, find(matcher));
    }

    /**
     * Creates a {@link fr.duminy.components.swing.path.JPathFixture} for a {@link fr.duminy.components.swing.path.JPath} named by <code>name</code>.
     */
    public JPathFixture path(String name) {
        return new JPathFixture(robot, findByName(name, JPath.class));
    }


    /**
     * Creates a {@link fr.duminy.components.swing.listpanel.ListPanelFixture} for a single {@link fr.duminy.components.swing.listpanel.ListPanel}.
     */
    @SuppressWarnings("unchecked")
    public <B, C extends JComponent> ListPanelFixture<B, C> listPanel() {
        return new ListPanelFixture<B, C>(robot, findByType(ListPanel.class));
    }

    /**
     * Creates a {@link fr.duminy.components.swing.listpanel.ListPanelFixture} from a {@link fr.duminy.components.swing.listpanel.ListPanel} matcher.
     */
    public <B, C extends JComponent> ListPanelFixture<B, C> listPanel(GenericTypeMatcher<? extends ListPanel<B, C>> matcher) {
        return new ListPanelFixture<>(robot, find(matcher));
    }

    /**
     * Creates a {@link fr.duminy.components.swing.listpanel.ListPanelFixture} for a {@link fr.duminy.components.swing.listpanel.ListPanel} named by <code>name</code>.
     */
    @SuppressWarnings("unchecked")
    public <B, C extends JComponent> ListPanelFixture<B, C> listPanel(String name) {
        return new ListPanelFixture<B, C>(robot, findByName(name, ListPanel.class));
    }

    private JFormPane form() {
        return (JFormPane) component();
    }

    private static JFormPane find(org.fest.swing.core.Robot robot, Class<?> beanClass) {
        String name = JFormPane.getDefaultPanelName(beanClass);
        return FixtureUtilities.find(robot, JFormPane.class, name);
    }
}
