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
package fr.duminy.components.swing.path;

import com.google.common.base.Supplier;
import fr.duminy.components.swing.AbstractFormTest;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.exception.ComponentLookupException;
import org.junit.ComparisonFailure;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Test the {@link fr.duminy.components.swing.path.JPathFixture} class.
 */
@RunWith(Theories.class)
public class JPathFixtureTest extends AbstractFormTest {
    private static final String COMPONENT_NAME = "jPathComponent";
    protected static final Path NULL_PATH = Paths.get("");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @DataPoints
    public static final JPath.SelectionMode[] MODES = JPath.SelectionMode.values();

    @Test
    public void testConstructor_noMatch() {
        thrown.expect(ComponentLookupException.class);
        thrown.expectMessage("Unable to find a JPath with name '" + COMPONENT_NAME + "'");

        new JPathFixture(robot(), COMPONENT_NAME);
    }

    @Test
    public void testConstructor_onlyOneMatch() throws Exception {
        buildAndShow(JPath.SelectionMode.FILES_AND_DIRECTORIES);

        new JPathFixture(robot(), COMPONENT_NAME);
    }

    @Test
    public void testConstructor_multipleMatches() throws Exception {
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

    @Theory
    public void testRequireSelectionMode(final JPath.SelectionMode mode) throws Exception {
        buildAndShow(mode);
        JPathFixture fixture = new JPathFixture(robot(), COMPONENT_NAME);

        fixture.requireSelectionMode(mode);

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

        GuiActionRunner.execute(new GuiQuery<Object>() {
            protected Object executeInEDT() {
                fixture.selectPath(expectedPath);
                return null;
            }
        });

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

        fixture.requireSelectedPath(expectedPath);
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
        thrown.handleAssertionErrors();
        if (NULL_PATH.equals(wrongPath)) {
            thrown.expectMessage("[selectedPath] expected:<[]> but was:<[" + path + "]>");
        } else {
            thrown.expectMessage("[selectedPath] expected:<[wrongP]ath> but was:<[p]ath>");
        }

        fixture.requireSelectedPath(wrongPath);
    }

    private JPath buildAndShow(final JPath.SelectionMode mode) throws Exception {
        Supplier<JPath> supplier = new Supplier<JPath>() {
            @Override
            public JPath get() {
                final JPath jPath = new JPath(mode);
                jPath.setName(COMPONENT_NAME);
                return jPath;
            }
        };
        return buildAndShowWindow(supplier);
    }

    private void setPathInEDT(final JPath jPath, final Path path) {
        GuiActionRunner.execute(new GuiQuery<Object>() {
            protected Object executeInEDT() {
                jPath.setPath(path);
                return null;
            }
        });
    }
}
