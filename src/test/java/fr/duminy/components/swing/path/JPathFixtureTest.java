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
package fr.duminy.components.swing.path;

import fr.duminy.components.swing.AbstractFormTest;
import fr.duminy.components.swing.listpanel.ListPanelFixtureTest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.edt.GuiQuery;
import org.assertj.swing.exception.ComponentLookupException;
import org.junit.ComparisonFailure;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test the {@link fr.duminy.components.swing.path.JPathFixture} class.
 */
@RunWith(Theories.class)
public class JPathFixtureTest extends AbstractFormTest {
    private static final String COMPONENT_NAME = "jPathComponent";
    protected static final Path NULL_PATH = Paths.get("");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testConstructor_nameArg_noMatch() {
        thrown.expect(ComponentLookupException.class);
        thrown.expectMessage("Unable to find a JPath with name '" + COMPONENT_NAME + "'");

        new JPathFixture(robot(), COMPONENT_NAME);
    }

    @Test
    public void testConstructor_nameArg_onlyOneMatch() throws Exception {
        buildAndShow(JPath.SelectionMode.FILES_AND_DIRECTORIES);

        new JPathFixture(robot(), COMPONENT_NAME);
    }

    @Test
    public void testConstructor_nameArg_multipleMatches() throws Exception {
        Supplier<JPanel> supplier = () -> {
            final JPath jPath1 = new JPath();
            jPath1.setName(COMPONENT_NAME);
            final JPath jPath2 = new JPath();
            jPath2.setName(COMPONENT_NAME);

            JPanel jPanel = new JPanel(new GridLayout(2, 1));
            jPanel.add(jPath1);
            jPanel.add(jPath2);
            return jPanel;
        };
        buildAndShowWindow(supplier);
        thrown.expect(ComponentLookupException.class);
        thrown.expectMessage("There are duplicates JPath with name '" + COMPONENT_NAME + "'");

        new JPathFixture(robot(), COMPONENT_NAME);
    }

    @Test
    public void testConstructor_jpathArg_null() throws Exception {
        thrown.expect(NullPointerException.class);
//        thrown.expectMessage("Target component should not be null");
        thrown.expectMessage(ListPanelFixtureTest.nullString());

        new JPathFixture(robot(), (JPath) null);
    }

    @Test
    public void testConstructor_jpathArg_notNull() throws Exception {
        final JPath jpath = GuiActionRunner.execute(new GuiQuery<JPath>() {
            protected JPath executeInEDT() {
                return new JPath();
            }
        });

        JPathFixture fixture = new JPathFixture(robot(), jpath);

        assertThat(fixture.target()).isSameAs(jpath);
    }

    @Test
    public void testRequireFileHidingEnabled_true() throws Exception {
        testRequireFileHidingEnabled(true);
    }

    @Test
    public void testRequireFileHidingEnabled_false() throws Exception {
        testRequireFileHidingEnabled(false);
    }

    private void testRequireFileHidingEnabled(boolean fileHidingEnabled) throws Exception {
        buildAndShow(JPath.SelectionMode.FILES_AND_DIRECTORIES, fileHidingEnabled);
        JPathFixture fixture = new JPathFixture(robot(), COMPONENT_NAME);

        JPathFixture actualFixture = fixture.requireFileHidingEnabled(fileHidingEnabled);
        assertThat(actualFixture).as("returned fixture").isSameAs(fixture);

        AssertionError error = null;
        boolean wrongValue = !fileHidingEnabled;
        try {
            fixture.requireFileHidingEnabled(wrongValue);
        } catch (AssertionError e) {
            error = e;
        }
        assertThat(error).as("requireFileHidingEnabled must fail for " + wrongValue).isExactlyInstanceOf(AssertionError.class).
                hasMessage("JPath component named '" + COMPONENT_NAME + "' must have fileHidingEnabled=" + wrongValue);
    }

    @Theory
    public void testRequireSelectionMode(final JPath.SelectionMode mode) throws Exception {
        buildAndShow(mode);
        JPathFixture fixture = new JPathFixture(robot(), COMPONENT_NAME);

        JPathFixture actualFixture = fixture.requireSelectionMode(mode);
        assertThat(actualFixture).as("returned fixture").isSameAs(fixture);

        for (JPath.SelectionMode m : JPath.SelectionMode.values()) {
            if (!m.equals(mode)) {
                JPath.SelectionMode wrongMode = m;
                AssertionError error = null;
                try {
                    fixture.requireSelectionMode(wrongMode);
                } catch (AssertionError e) {
                    error = e;
                }
                assertThat(error).as("requireSelectionMode must fail for " + wrongMode).isExactlyInstanceOf(AssertionError.class).
                        hasMessage("JPath component named '" + COMPONENT_NAME + "' must have selection mode " + wrongMode);
            }
        }
    }

    @Test
    public void testSelectPath_notNull() throws Exception {
        testSelectPath(Paths.get("finalPath"));
    }

    @Test
    public void testSelectPath_null() throws Exception {
        testSelectPath(NULL_PATH);
    }

    private void testSelectPath(final Path expectedPath) throws Exception {
        final JPath jPath = buildAndShow(JPath.SelectionMode.FILES_AND_DIRECTORIES);
        setPathInEDT(jPath, Paths.get("initialPath"));
        final JPathFixture fixture = new JPathFixture(robot(), COMPONENT_NAME);

        JPathFixture actualFixture = GuiActionRunner.execute(new GuiQuery<JPathFixture>() {
            protected JPathFixture executeInEDT() {
                return fixture.selectPath(expectedPath);
            }
        });
        assertThat(actualFixture).as("returned fixture").isSameAs(fixture);

        assertThat(jPath.getPath()).as("selectedPath").isEqualTo(expectedPath);
    }

    @Test
    public void testRequireSelectedPath_goodPath_null() throws Exception {
        testRequireSelectedPath_goodPath(NULL_PATH);
    }

    @Test
    public void testRequireSelectedPath_goodPath_notNull() throws Exception {
        testRequireSelectedPath_goodPath(Paths.get("path"));
    }

    private void testRequireSelectedPath_goodPath(final Path expectedPath) throws Exception {
        final JPath jPath = buildAndShow(JPath.SelectionMode.FILES_AND_DIRECTORIES);
        setPathInEDT(jPath, expectedPath);
        final JPathFixture fixture = new JPathFixture(robot(), COMPONENT_NAME);

        JPathFixture actualFixture = fixture.requireSelectedPath(expectedPath);
        assertThat(actualFixture).as("returned fixture").isSameAs(fixture);
    }

    @Test
    public void testRequireSelectedPath_wrongPath_null() throws Exception {
        testRequireSelectedPath_wrongPath(NULL_PATH);
    }

    @Test
    public void testRequireSelectedPath_wrongPath_notNull() throws Exception {
        testRequireSelectedPath_wrongPath(Paths.get("wrongPath"));
    }

    private void testRequireSelectedPath_wrongPath(Path wrongPath) throws Exception {
        final JPath jPath = buildAndShow(JPath.SelectionMode.FILES_AND_DIRECTORIES);
        String path = "path";
        setPathInEDT(jPath, Paths.get(path));
        final JPathFixture fixture = new JPathFixture(robot(), COMPONENT_NAME);
        thrown.expect(ComparisonFailure.class);
        if (NULL_PATH.equals(wrongPath)) {
            thrown.expectMessage("[selectedPath] expected:<[]> but was:<[" + path + "]>");
        } else {
            thrown.expectMessage("[selectedPath] expected:<[wrongP]ath> but was:<[p]ath>");
        }

        fixture.requireSelectedPath(wrongPath);
    }

    private JPath buildAndShow(final JPath.SelectionMode mode) throws Exception {
        return buildAndShow(mode, true);
    }

    private JPath buildAndShow(final JPath.SelectionMode mode, final boolean fileHidingEnabled) throws Exception {
        Supplier<JPath> supplier = () -> {
            final JPath jPath = new JPath();
            jPath.setSelectionMode(mode);
            jPath.setName(COMPONENT_NAME);
            jPath.setFileHidingEnabled(fileHidingEnabled);
            return jPath;
        };
        return buildAndShowWindow(supplier);
    }

    private void setPathInEDT(final JPath jPath, final Path path) {
        GuiActionRunner.execute(new GuiQuery<Void>() {
            protected Void executeInEDT() {
                jPath.setPath(path);
                return null;
            }
        });
    }
}
