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
import org.fest.swing.fixture.JTextComponentFixture;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import javax.swing.*;
import javax.swing.text.JTextComponent;
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
    public void testConstructor() {
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
    public void testSetPath_disabled(SelectionMode selectionMode) throws Exception {
        testSetPath(selectionMode, false);
    }

    @Theory
    public void testSetPath_enabled(SelectionMode selectionMode) throws Exception {
        testSetPath(selectionMode, true);
    }

    private void testSetPath(SelectionMode selectionMode, boolean enabled) throws Exception {
        final Parameters parameters = new Parameters(enabled, selectionMode);
        final JPath field = doBuildAndShowWindow(parameters);

        Exception exception = GuiActionRunner.execute(new GuiQuery<Exception>() {
            protected Exception executeInEDT() {
                Exception result = null;
                try {
                    field.setPath(parameters.getPath());
                } catch (Exception e) {
                    result = e;
                }
                return result;
            }
        });

        checkActualState(field, parameters, exception, true, true);
    }

    @Theory
    public void testSelectPath_disabled(SelectionMode selectionMode, boolean enterPath) throws Exception {
        testSelectPath(selectionMode, enterPath, false);
    }

    @Theory
    public void testSelectPath_enabled(SelectionMode selectionMode, boolean enterPath) throws Exception {
        testSelectPath(selectionMode, enterPath, true);
    }

    private void testSelectPath(SelectionMode selectionMode, boolean enterPath, boolean enabled) throws Exception {
        final Parameters parameters = new Parameters(enabled, selectionMode);

        JPath field = doBuildAndShowWindow(parameters);

        window.textBox().requireText(parameters.getInitialText());

        if (enterPath) {
            boolean expectError = !enabled;

            Exception exception = executeInEDT(new GuiQuery<Void>() {
                protected Void executeInEDT() throws Exception {
                    window.textBox().component().setText(parameters.getText());
                    return null;
                }
            }, expectError);

            checkActualState(field, parameters, exception, false, false);
        } else {
            boolean expectError = false;

            final JButtonFixture jbf = window.button(CHOOSE_BUTTON_NAME);
            if (enabled) {
                jbf.requireEnabled();

                executeInEDT(new GuiQuery<Void>() {
                    protected Void executeInEDT() {
                        jbf.targetCastedTo(JButton.class).doClick();
                        return null;
                    }
                }, expectError);

                JFileChooserFixture jfc = window.fileChooser();
                File fileToSelect = parameters.getPath().toFile();
                if (selectionMode.allowsDirectory()) {
                    jfc.setCurrentDirectory(fileToSelect.getParentFile());
                }
                jfc.selectFile(fileToSelect);
                jfc.approve();
            } else {
                jbf.requireDisabled();
            }

            checkActualState(field, parameters, null, false, false);
        }
    }

    private Exception executeInEDT(GuiQuery<Void> query, boolean expectError) {
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

    private void checkActualState(JPath field, Parameters parameters, Exception exception, boolean expectException, boolean expectIllegalStateException) {
        assertThat(field.isEnabled()).isEqualTo(parameters.enabled);

        //TODO add requireEnabled(boolean), requireEditable(boolean) ... etc to JTextComponentFixture, JButtonFixture...
        final JTextComponentFixture textBoxFixture = window.textBox(PATH_FIELD_NAME);
        JTextComponent textBox = textBoxFixture.component();
        assertThat(textBox.isEnabled()).as("textBox.enabled").isEqualTo(parameters.enabled);
        assertThat(textBox.isEnabled()).as("textBox.editable").isEqualTo(parameters.enabled);

        JButton button = window.button(CHOOSE_BUTTON_NAME).component();
        assertThat(button.isEnabled()).as("button.enabled").isEqualTo(parameters.enabled);

        if (parameters.enabled) {
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
        }

        Path expectedPath = parameters.getExpectedPath();

        Path actualPath = field.getPath();
        assertThat(actualPath).isNotNull();
        assertThat(actualPath).isEqualTo(expectedPath);

        textBoxFixture.requireText(expectedPath.toAbsolutePath().toString());
    }

    private JPath doBuildAndShowWindow(final Parameters parameters)
            throws Exception {
        return buildAndShowWindow(new Supplier<JPath>() {
            @Override
            public JPath get() {
                final JPath result = new JPath(parameters.selectionMode);
                result.setColumns(10);

                if (!parameters.enabled) {
                    result.setPath(parameters.getInitialPath());
                }
                GuiActionRunner.execute(new GuiQuery<Void>() {
                    protected Void executeInEDT() {
                        result.setEnabled(parameters.enabled);
                        return null;
                    }
                });

                return result;
            }

        });
    }

    private class Parameters {
        private final boolean enabled;
        private final SelectionMode selectionMode;

        private final Path initialPath;
        private final Path pathToSelect;

        public Parameters(final boolean enabled, SelectionMode selectionMode) {
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
