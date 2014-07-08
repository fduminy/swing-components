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
import fr.duminy.components.swing.AbstractSwingTest;
import org.fest.assertions.Condition;
import org.fest.assertions.ThrowableAssert;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.exception.WaitTimedOutError;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static fr.duminy.components.swing.path.JPath.*;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Tests for class {@link JPath}.
 */
@RunWith(Theories.class)
public class JPathTest extends AbstractSwingTest {
    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    @DataPoint
    public static final SelectionMode FILES_ONLY = SelectionMode.FILES_ONLY;
    @DataPoint
    public static final SelectionMode DIRECTORIES_ONLY = SelectionMode.DIRECTORIES_ONLY;
    @DataPoint
    public static final SelectionMode FILES_AND_DIRECTORIES = SelectionMode.FILES_AND_DIRECTORIES;

    @DataPoint
    public static final boolean ENTER_FILE_PATH = true;
    @DataPoint
    public static final boolean SELECT_FILE = false;

    @DataPoint
    public static final int ONE_COLUMN = 1;
    @DataPoint
    public static final int TWENTY_COLUMNS = 20;

    private Path path;
    private Path pathForDisabledField;

    @Override
    public void onSetUp() {
        super.onSetUp();
        try {
            InputStream data = new ByteArrayInputStream("data".getBytes());

            path = createFile(1, data);
            pathForDisabledField = createFile(2, data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Path createFile(int fileNum, InputStream data) throws IOException {
        Path grandParent = tempFolder.newFolder("grandParent" + fileNum).toPath();
        Path parent = grandParent.resolve("parent" + fileNum);
        Files.createDirectories(parent);

        Path result = parent.resolve("childFile" + fileNum);
        Files.copy(data, result);

        return result;
    }

    @Test
    public void testCreate_withoutBaseDirectoryGetter() {
        Exception e = create();
        assertThat(e).isNull();
    }

    private Exception create() {
        return GuiActionRunner.execute(new GuiQuery<Exception>() {
            protected Exception executeInEDT() {
                try {
                    new JPath(SelectionMode.FILES_AND_DIRECTORIES);
                } catch (Exception e) {
                    return e;
                }
                return null;
            }
        });
    }

    @Theory
    public void testSetColumns(final int columns) throws Exception {
        Supplier<JPath> pathSupplier = new Supplier<JPath>() {
            @Override
            public JPath get() {
                JPath field = new JPath(SelectionMode.FILES_AND_DIRECTORIES);
                field.setColumns(columns);
                return field;
            }
        };

        buildAndShowWindow(pathSupplier);

        JTextField tf = (JTextField) window.textBox(PATH_FIELD_NAME).component();
        assertThat(tf.getColumns()).isEqualTo(columns);
    }

    @Theory
    public void testSetPath_readOnly_withoutBasePathGetter(SelectionMode selectionMode) throws Exception {
        testSetPath(selectionMode, false);
    }

    @Theory
    public void testSetPath_withoutBasePathGetter(SelectionMode selectionMode) throws Exception {
        testSetPath(selectionMode, true);
    }

    private void testSetPath(SelectionMode selectionMode, boolean enabled) throws Exception {
        final PathToSelect pathToSelect = new PathToSelect(enabled, selectionMode);
        final JPath field = doBuildAndShowWindow(pathToSelect, selectionMode, enabled);

        Exception exception = GuiActionRunner.execute(new GuiQuery<Exception>() {
            protected Exception executeInEDT() {
                Exception result = null;
                try {
                    field.setPath(pathToSelect.getPath());
                } catch (Exception e) {
                    result = e;
                }
                return result;
            }
        });

        checkActualState(field, pathToSelect, exception, true, true);
    }

    @Theory
    public void testSelectPath_readOnly_withoutBasePathGetter(SelectionMode selectionMode, boolean enterPath) throws Exception {
        testSelectPath(selectionMode, enterPath, false);
    }

    @Theory
    public void testSelectPath_withoutBasePathGetter(SelectionMode selectionMode, boolean enterPath) throws Exception {
        testSelectPath(selectionMode, enterPath, true);
    }

    private void testSelectPath(SelectionMode selectionMode, boolean enterPath, boolean enabled) throws Exception {
        final PathToSelect pathToSelect = new PathToSelect(enabled, selectionMode);

        JPath field = doBuildAndShowWindow(pathToSelect,
                selectionMode, enabled);

        window.textBox().requireText(pathToSelect.getInitialText());

        if (enterPath) {
            boolean expectError = !enabled;

            Exception exception = executeInEDT(new GuiQuery<Object>() {
                protected Object executeInEDT() throws Exception {
                    window.textBox().component().setText(pathToSelect.getText());
                    return null;
                }
            }, expectError);

            checkActualState(field, pathToSelect, exception, false, false);
        } else {
            boolean expectError = false;

            final JButtonFixture jbf = window.button(CHOOSE_BUTTON_NAME);
            if (enabled) {
                jbf.requireEnabled();

                executeInEDT(new GuiQuery<Object>() {
                    protected Object executeInEDT() {
                        jbf.targetCastedTo(JButton.class).doClick();
                        return null;
                    }
                }, expectError);

                JFileChooserFixture jfc = window.fileChooser();
                File fileToSelect = pathToSelect.getPath().toFile();
                if (selectionMode.allowsDirectory()) {
                    jfc.setCurrentDirectory(fileToSelect.getParentFile());
                }
                jfc.selectFile(fileToSelect);
                jfc.approve();
            } else {
                jbf.requireDisabled();
            }

            checkActualState(field, pathToSelect, null, false, false);
        }
    }

    private Exception executeInEDT(GuiQuery query, boolean expectError) {
        Exception result = null;
        try {
            GuiActionRunner.execute(query);
        } catch (Exception e) {
            if (expectError) {
                result = e;
            } else {
                throw e;
            }
        }
        return result;
    }

    private void checkActualState(JPath field, PathToSelect pathToSelect, Exception exception, boolean expectException, boolean expectIllegalStateException) {
        if (pathToSelect.enabled) {
            window.textBox(PATH_FIELD_NAME).requireEnabled().requireEditable();
            window.button(CHOOSE_BUTTON_NAME).requireEnabled();
            if (exception != null) {
                Assert.fail("expected exception to be null but is " + exception);
            }
        } else {
            if (expectException) {
                ThrowableAssert ta = assertThat(exception).as("exception thrown when disabled")
                        .isNotNull();

                if (expectIllegalStateException) {
                    ta.isExactlyInstanceOf(IllegalStateException.class).as("expected exception").hasMessage("component is disabled");
                } else {
                    ta.isExactlyInstanceOf(WaitTimedOutError.class).as("expected exception").satisfies(new Condition<Throwable>() {
                        @Override
                        public boolean matches(Throwable value) {
                            return value.getMessage().startsWith("Timed out waiting for path chooser to be found using matcher");
                        }
                    });
                }
            }

            window.textBox(PATH_FIELD_NAME).requireDisabled().requireNotEditable();
            window.button(CHOOSE_BUTTON_NAME).requireDisabled();
        }
        assertThat(field.isEnabled()).isEqualTo(pathToSelect.enabled);

        String expectedText;

        Path expectedPath = pathToSelect.getExpectedPath();

        Path actualPath = field.getPath();
        assertThat(actualPath).isNotNull();

        assertThat(actualPath).isEqualTo(expectedPath);
        expectedText = expectedPath.toAbsolutePath().toString();

        window.textBox(PATH_FIELD_NAME).requireText(expectedText);
    }

    private JPath doBuildAndShowWindow(
            final PathToSelect pathToSelect,
            final SelectionMode selectionMode, final boolean enabled)
            throws Exception {
        return buildAndShowWindow(new Supplier<JPath>() {
            @Override
            public JPath get() {
                final JPath result = new JPath(selectionMode);
                result.setColumns(10);

                if (!enabled) {
                    result.setPath(pathToSelect.getInitialPath());
                    GuiActionRunner.execute(new GuiQuery<Object>() {
                        protected Object executeInEDT() {
                            result.setEnabled(false);
                            return null;
                        }
                    });
                }

                return result;
            }

        });
    }

    private class PathToSelect {
        private final boolean enabled;
        private final SelectionMode selectionMode;

        private final Path initialPath;
        private final Path pathToSelect;

        public PathToSelect(final boolean enabled, SelectionMode selectionMode) {
            this.enabled = enabled;
            this.selectionMode = selectionMode;

            final boolean testPathIsADirectory = selectionMode.allowsDirectory();

            pathToSelect = testPathIsADirectory ? path.getParent() : path;

            if (enabled) {
                initialPath = null;
            } else {
                initialPath = selectionMode.allowsDirectory() ? pathForDisabledField.getParent() : pathForDisabledField;
            }
        }

        public Path getExpectedPath() {
            return enabled ? getPath() : getInitialPath();
        }

        public Path getInitialPath() {
            return initialPath;
        }

        public Path getPath() {
            return pathToSelect;
        }

        public String getInitialText() {
            return getText(initialPath);
        }

        public String getText() {
            return getText(pathToSelect);
        }

        private String getText(Path file) {
            String text = "";
            if (file != null) {
                text = file.toString();
            }
            return text;
        }

        @Override
        public String toString() {
            return "[enabled=" + enabled
                    + ", selectionMode=" + selectionMode + ", pathToSelect="
                    + pathToSelect + "]";
        }
    }
}
