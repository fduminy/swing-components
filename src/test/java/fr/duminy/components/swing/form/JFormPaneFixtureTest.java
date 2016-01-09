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
package fr.duminy.components.swing.form;

import fr.duminy.components.swing.AbstractFormTest;
import fr.duminy.components.swing.AbstractSwingTest;
import fr.duminy.components.swing.list.DefaultMutableListModel;
import fr.duminy.components.swing.listpanel.ListPanel;
import fr.duminy.components.swing.listpanel.ListPanelFixture;
import fr.duminy.components.swing.listpanel.SimpleItemManager;
import fr.duminy.components.swing.path.JPath;
import fr.duminy.components.swing.path.JPathFixture;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.NameMatcher;
import org.assertj.swing.exception.ComponentLookupException;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.formbuilder.TypeMapper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.function.Supplier;

import static fr.duminy.components.swing.form.JFormPaneFixtureTest.ComponentLookupExceptionType.MULTIPLE_MATCHES;
import static fr.duminy.components.swing.form.JFormPaneFixtureTest.ComponentLookupExceptionType.NO_MATCH;
import static fr.duminy.components.swing.listpanel.SimpleItemManager.ContainerType.DIALOG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Test for class {@link fr.duminy.components.swing.form.JFormPaneFixture}.
 */
@RunWith(Theories.class)
public class JFormPaneFixtureTest extends AbstractFormTest {
    private static final String PANEL_NAME = Bean.class.getSimpleName();
    private static final String BUTTON_NAME = "openInDialog";

    @DataPoint
    public static final Action<JFormPane<Bean>> OPEN_IN_PANEL = new Action<>(false, new BaseSupplier<JFormPane<Bean>>() {
        @Override
        public JFormPane<Bean> get() {
            final FormBuilder<Bean> builder = createBuilder(null, Bean.class);
            JFormPane<Bean> form = new JFormPane<>(builder, title, mode);
            form.setName(PANEL_NAME);
            return form;
        }
    });

    @DataPoint
    public static final Action<JButton> OPEN_IN_DIALOG = new Action<JButton>(true, new CustomSupplier<JButton>() {
        @Override
        public JButton get() {
            JButton result = new JButton("Open Dialog");
            result.addActionListener(e -> {
                final FormBuilder<Bean> builder = createBuilder((Container) parentComponent, Bean.class);
                JFormPane.showFormDialog(parentComponent, builder, null, title, mode);
            });
            result.setName(BUTTON_NAME);
            return result;
        }
    }) {
        @Override
        public JComponent openForm(Component parentComponent, AbstractSwingTest test, JFormPane.Mode mode, String title) throws Exception {
            ((CustomSupplier) this.supplier).parentComponent = parentComponent;
            super.openForm(parentComponent, test, mode, title);
            test.window.button(BUTTON_NAME).click();
            test.getRobot().waitForIdle();
            return (JComponent) test.getRobot().finder().findByName(PANEL_NAME);
        }
    };

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Theory
    public void testConstructor_panelName(Action<JComponent> action) throws Exception {
        action.openForm(null, this, JFormPane.Mode.CREATE);

        JFormPaneFixture fixture = new JFormPaneFixture(robot(), PANEL_NAME);

        assertThat(fixture).isNotNull();
    }

    @Theory
    public void testConstructor_beanClass(Action<JComponent> action) throws Exception {
        action.openForm(null, this, JFormPane.Mode.CREATE);

        JFormPaneFixture fixture = new JFormPaneFixture(robot(), Bean.class);

        assertThat(fixture).isNotNull();
    }

    @Test
    public void testConstructor_panelName_componentNotFound() throws Exception {
        Supplier<JFormPane<Bean>> wrongName = () -> {
            OPEN_IN_PANEL.supplier.mode = JFormPane.Mode.CREATE;
            JFormPane<Bean> form = OPEN_IN_PANEL.supplier.get();
            form.setName("WrongName");
            return form;
        };
        buildAndShowWindow(wrongName);
        thrown.expect(ComponentLookupException.class);

        new JFormPaneFixture(robot(), PANEL_NAME);
    }

    @Test
    public void testConstructor_beanClass_componentNotFound() throws Exception {
        Supplier<JLabel> wrongComponentClass = () -> {
            JLabel l = new JLabel("");
            l.setName(PANEL_NAME);
            return l;
        };
        buildAndShowWindow(wrongComponentClass);
        thrown.expect(ComponentLookupException.class);

        new JFormPaneFixture(robot(), Bean.class);
    }

    @Theory
    public void testComponent_beanClass(Action<JComponent> action) throws Exception {
        testComponent(action, true);
    }

    @Theory
    public void testComponent_panelName(Action<JComponent> action) throws Exception {
        testComponent(action, false);
    }

    private void testComponent(Action<JComponent> action, boolean useBeanClass) throws Exception {
        JComponent expectedForm = action.openForm(null, this, JFormPane.Mode.CREATE);
        JFormPaneFixture fixture = useBeanClass ? new JFormPaneFixture(robot(), Bean.class) : new JFormPaneFixture(robot(), PANEL_NAME);

        JPanel form = fixture.target();

        assertThat(form).isInstanceOf(JFormPane.class);
        assertThat(form).isEqualTo(expectedForm);
    }

    @Theory
    @SuppressWarnings("unchecked")
    public void testOkButton(Action<JComponent> action) throws Exception {
        action.openForm(null, this, JFormPane.Mode.CREATE);
        JFormPaneFixture fixture = new JFormPaneFixture(robot(), PANEL_NAME);
        JFormPane<Bean> formPane = (JFormPane<Bean>) fixture.target();
        FormListener<Bean> listener = Mockito.mock(FormListener.class);
        formPane.addFormListener(listener);

        JButtonFixture buttonFixture = fixture.okButton();
        assertThat(buttonFixture).isNotNull();
        buttonFixture.click();

        verify(listener).formValidated(any(Form.class));
        verifyNoMoreInteractions(listener);
    }

    @Theory
    @SuppressWarnings("unchecked")
    public void testCancelButton(Action<JComponent> action) throws Exception {
        action.openForm(null, this, JFormPane.Mode.CREATE);
        JFormPaneFixture fixture = new JFormPaneFixture(robot(), PANEL_NAME);
        JFormPane<Bean> formPane = (JFormPane<Bean>) fixture.target();
        FormListener<Bean> listener = Mockito.mock(FormListener.class);
        formPane.addFormListener(listener);

        JButtonFixture buttonFixture = fixture.cancelButton();
        assertThat(buttonFixture).isNotNull();
        buttonFixture.click();

        verify(listener).formCancelled(any(Form.class));
        verifyNoMoreInteractions(listener);
    }

    @Theory
    public void testRequireInDialog_asExpected(Action<JComponent> action) throws Exception {
        testRequireInDialog(action, action.inDialog);
    }

    @Theory
    public void testRequireInDialog_notAsExpected(Action<JComponent> action) throws Exception {
        boolean expectedInDialog = !action.inDialog;
        thrown.expect(AssertionError.class);
        thrown.expectMessage("The form '" + PANEL_NAME + "' must " + (expectedInDialog ? "" : "not ") + "be in a dialog");
        testRequireInDialog(action, expectedInDialog);
    }

    private void testRequireInDialog(Action<JComponent> action, boolean expectedInDialog) throws Exception {
        action.openForm(null, this, JFormPane.Mode.CREATE);
        JFormPaneFixture fixture = new JFormPaneFixture(robot(), PANEL_NAME);

        JFormPaneFixture actualFixture = fixture.requireInDialog(expectedInDialog);
        assertEquals("returned fixture", fixture, actualFixture);
    }

    @Theory
    public void testRequireModeCreate(Action<JComponent> action) throws Exception {
        testRequireModeCreate(action, JFormPane.Mode.CREATE);
    }

    @Theory
    public void testRequireModeCreate_wrongMode(Action<JComponent> action) throws Exception {
        thrown.expect(AssertionError.class);
        thrown.expectMessage("The form '" + PANEL_NAME + "' must be in CREATE mode");
        testRequireModeCreate(action, JFormPane.Mode.UPDATE);
    }

    private void testRequireModeCreate(Action<JComponent> action, JFormPane.Mode actualMode) throws Exception {
        action.openForm(null, this, actualMode);
        JFormPaneFixture fixture = new JFormPaneFixture(robot(), PANEL_NAME);

        JFormPaneFixture actualFixture = fixture.requireModeCreate();
        assertEquals("returned fixture", fixture, actualFixture);
    }

    @Theory
    public void testRequireModeUpdate(Action<JComponent> action) throws Exception {
        testRequireModeUpdate(action, JFormPane.Mode.UPDATE);
    }

    @Theory
    public void testRequireModeUpdate_wrongMode(Action<JComponent> action) throws Exception {
        thrown.expect(AssertionError.class);
        thrown.expectMessage("The form '" + PANEL_NAME + "' must be in UPDATE mode");
        testRequireModeUpdate(action, JFormPane.Mode.CREATE);
    }

    private void testRequireModeUpdate(Action<JComponent> action, JFormPane.Mode actualMode) throws Exception {
        action.openForm(null, this, actualMode);
        JFormPaneFixture fixture = new JFormPaneFixture(robot(), PANEL_NAME);

        JFormPaneFixture actualFixture = fixture.requireModeUpdate();
        assertEquals("returned fixture", fixture, actualFixture);
    }

    @SuppressWarnings("unchecked")
    @Theory
    public void testRequireTitle(Action<JComponent> action) throws Exception {
        action.openForm(null, this, JFormPane.Mode.CREATE);
        JFormPaneFixture fixture = new JFormPaneFixture(robot(), PANEL_NAME);

        JFormPaneFixture actualFixture = fixture.requireTitle(TITLE);
        assertEquals("returned fixture", fixture, actualFixture);
    }

    @Theory
    public void testRequireTitle_wrongTitle(Action<JComponent> action) throws Exception {
        final String wrongTitle = "wrongTitle";
        action.openForm(null, this, JFormPane.Mode.CREATE, wrongTitle);

        thrown.expect(AssertionError.class);
        thrown.expectMessage(String.format("The form '%s' must have title '%s' but has title '%s'", PANEL_NAME, TITLE, wrongTitle));

        new JFormPaneFixture(robot(), PANEL_NAME).requireTitle(TITLE);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////// Tests for method path(String) ///////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testPath_nameArg_noMatch_noCustomField() throws Exception {
        testField_nameArg_noMatch_noCustomField(new JPathFixtureFactory<>(), JPath.class, "path");
    }

    @Test
    public void testPath_nameArg_noMatch_wrongCustomFieldName() throws Exception {
        testField_nameArg_noMatch_wrongCustomFieldName(new JPathFixtureFactory<>(), JPath.class);
    }

    @Test
    public void testPath_nameArg_onlyOneMatch() throws Exception {
        testField_nameArg_onlyOneMatch(new JPathFixtureFactory<>(), "path", "path2");
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// Tests for method path() //////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testPath_noArgs_noMatch_noCustomField() throws Exception {
        testField_noArgs_noMatch_noCustomField(new JPathFixtureFactory<>(), JPath.class);
    }

    @Test
    public void testPath_noArgs_multipleMatches() throws Exception {
        testField_noArgs_multipleMatches(new JPathFixtureFactory<>(), JPath.class, "path", "path2");
    }

    @Test
    public void testPath_noArgs_onlyOneMatch() throws Exception {
        testField_noArgs_onlyOneMatch(new JPathFixtureFactory<>(), "path");
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////// Tests for method path(GenericTypeMatcher) /////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testPath_matcherArg_noMatch_noCustomField() throws Exception {
        testField_matcherArg_noMatch_noCustomField(new JPathFixtureFactory<>(), JPath.class, "path");
    }

    @Test
    public void testPath_matcherArg_noMatch_wrongCustomFieldName() throws Exception {
        testField_matcherArg_noMatch_wrongCustomFieldName(new JPathFixtureFactory<>(), JPath.class);
    }

    @Test
    public void testPath_matcherArg_onlyOneMatch() throws Exception {
        testField_matcherArg_onlyOneMatch(new JPathFixtureFactory<>(), JPath.class, "path", "path2");
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////// Tests for method listPanel(String) ////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testListPanel_nameArg_noMatch_noCustomField() throws Exception {
        testField_nameArg_noMatch_noCustomField(new ListPanelFixtureFactory<>(), ListPanel.class, "list");
    }

    @Test
    public void testListPanel_nameArg_noMatch_wrongCustomFieldName() throws Exception {
        testField_nameArg_noMatch_wrongCustomFieldName(new ListPanelFixtureFactory<>(), ListPanel.class);
    }

    @Test
    public void testListPanel_nameArg_onlyOneMatch() throws Exception {
        testField_nameArg_onlyOneMatch(new ListPanelFixtureFactory<>(), "list", "list2");
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// Tests for method listPanel() ///////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testListPanel_noArgs_noMatch_noCustomField() throws Exception {
        testField_noArgs_noMatch_noCustomField(new ListPanelFixtureFactory<>(), ListPanel.class);
    }

    @Test
    public void testListPanel_noArgs_multipleMatches() throws Exception {
        testField_noArgs_multipleMatches(new ListPanelFixtureFactory<>(), ListPanel.class, "list", "list2");
    }

    @Test
    public void testListPanel_noArgs_onlyOneMatch() throws Exception {
        testField_noArgs_onlyOneMatch(new ListPanelFixtureFactory<>(), "list");
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////// Tests for method listPanel(GenericTypeMatcher) //////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unchecked")
    @Test
    public void testListPanel_matcherArg_noMatch_noCustomField() throws Exception {
        Class<?> componentClass = ListPanel.class;
        testField_matcherArg_noMatch_noCustomField(new ListPanelFixtureFactory<>(), (Class<ListPanel<BeanWithoutCustomField, ListPanel<BeanWithoutCustomField, JList>>>) componentClass, "list");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testListPanel_matcherArg_noMatch_wrongCustomFieldName() throws Exception {
        Class<?> componentClass = ListPanel.class;
        testField_matcherArg_noMatch_wrongCustomFieldName(new ListPanelFixtureFactory<>(), (Class<ListPanel<BeanWithOneCustomField, ListPanel>>) componentClass);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testListPanel_matcherArg_onlyOneMatch() throws Exception {
        Class<?> componentClass = ListPanel.class;
        testField_matcherArg_onlyOneMatch(new ListPanelFixtureFactory<>(), (Class<ListPanel<BeanWithTwoCustomFields, ListPanel>>) componentClass, "list", "list2");
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////// Generic methods for testing custom field (JPath, ListPanel ...) fixtures //////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private <C extends JPanel, CF extends JPanelFixture> void testField_noArgs_noMatch_noCustomField(
            FixtureFactory<BeanWithoutCustomField, C, CF> factory, Class<? super C> componentClass) throws Exception {
        expectComponentLookupException(componentClass, NO_MATCH);
        testField_noArgs(factory, BeanWithoutCustomField.class);
    }

    private <C extends JPanel, CF extends JPanelFixture> void testField_noArgs_onlyOneMatch(
            FixtureFactory<BeanWithOneCustomField, C, CF> factory, String fieldName) throws Exception {
        FormsSupplier<BeanWithOneCustomField, CF> supplier = testField_noArgs(factory, BeanWithOneCustomField.class);

        assertThat(supplier.fieldFixture).isNotNull();
        Component component = supplier.fieldFixture.target();
        assertThat(component.getName()).isEqualTo(fieldName);
        assertThat(SwingUtilities.getAncestorOfClass(JFormPane.class, component)).isEqualTo(supplier.targetForm);
    }

    private <C extends JPanel, CF extends JPanelFixture> void testField_noArgs_multipleMatches(
            FixtureFactory<BeanWithTwoCustomFields, C, CF> factory, Class<? super C> componentClass, String fieldName, String field2Name) throws Exception {
        expectComponentLookupException(componentClass, MULTIPLE_MATCHES, fieldName, field2Name);
        testField_noArgs(factory, BeanWithTwoCustomFields.class);
    }

    private <B, C extends JPanel, CF extends JPanelFixture> FormsSupplier<B, CF> testField_noArgs(FixtureFactory<B, C, CF> factory, Class<B> beanClass) throws Exception {
        FormsSupplier<B, CF> supplier = buildAndShowWindowWithCustomField(beanClass);
        supplier.fieldFixture = factory.fixture(supplier);
        return supplier;
    }

    private <C extends JPanel, CF extends JPanelFixture> void testField_matcherArg_noMatch_noCustomField(
            FixtureFactory<BeanWithoutCustomField, C, CF> factory, Class<C> componentClass, String fieldName) throws Exception {
        expectComponentLookupException(NO_MATCH);
        FormsSupplier<BeanWithoutCustomField, CF> supplier = buildAndShowWindowWithCustomField(BeanWithoutCustomField.class);

        factory.fixture(supplier, new FieldNameMatcher<>(componentClass, fieldName));
    }

    private <C extends JPanel, CF extends JPanelFixture> void testField_matcherArg_noMatch_wrongCustomFieldName(
            FixtureFactory<BeanWithOneCustomField, C, CF> factory, Class<C> componentClass) throws Exception {
        expectComponentLookupException(componentClass, NO_MATCH);
        FormsSupplier<BeanWithOneCustomField, CF> supplier = buildAndShowWindowWithCustomField(BeanWithOneCustomField.class);

        factory.fixture(supplier, new FieldNameMatcher<>(componentClass, "wrongName"));
    }

    private <C extends JPanel, CF extends JPanelFixture> void testField_matcherArg_onlyOneMatch(
            FixtureFactory<BeanWithTwoCustomFields, C, CF> factory, Class<C> componentClass, String fieldName, String field2Name) throws Exception {
        FormsSupplier<BeanWithTwoCustomFields, CF> supplier = buildAndShowWindowWithCustomField(BeanWithTwoCustomFields.class);

        testField_matcherArg(factory, supplier, componentClass, fieldName);
        testField_matcherArg(factory, supplier, componentClass, field2Name);
    }

    private <C extends JPanel, CF extends JPanelFixture> void testField_matcherArg(
            FixtureFactory<BeanWithTwoCustomFields, C, CF> factory, FormsSupplier<BeanWithTwoCustomFields, CF> supplier,
            Class<C> componentClass, String fieldName) {
        CF fixture = factory.fixture(supplier, new FieldNameMatcher<>(componentClass, fieldName));

        assertThat(fixture).isNotNull();
        Component component = fixture.target();
        assertThat(component.getName()).isEqualTo(fieldName);
        assertThat(SwingUtilities.getAncestorOfClass(JFormPane.class, component)).isEqualTo(supplier.targetForm);
    }

    private <C extends JPanel, CF extends JPanelFixture> void testField_nameArg_noMatch_noCustomField(
            FixtureFactory<BeanWithoutCustomField, C, CF> factory, Class<? super C> componentClass, String fieldName) throws Exception {
        expectComponentLookupException(componentClass, NO_MATCH);
        FormsSupplier<BeanWithoutCustomField, CF> supplier = buildAndShowWindowWithCustomField(BeanWithoutCustomField.class);

        factory.fixture(supplier, fieldName);
    }

    private <C extends JPanel, CF extends JPanelFixture> void testField_nameArg_noMatch_wrongCustomFieldName(
            FixtureFactory<BeanWithOneCustomField, C, CF> factory, Class<? super C> componentClass) throws Exception {
        expectComponentLookupException(componentClass, NO_MATCH);
        FormsSupplier<BeanWithOneCustomField, CF> supplier = buildAndShowWindowWithCustomField(BeanWithOneCustomField.class);

        factory.fixture(supplier, "wrongName");
    }

    private <C extends JPanel, CF extends JPanelFixture> void testField_nameArg_onlyOneMatch(
            FixtureFactory<BeanWithTwoCustomFields, C, CF> factory, String fieldName, String field2Name) throws Exception {
        FormsSupplier<BeanWithTwoCustomFields, CF> supplier = buildAndShowWindowWithCustomField(BeanWithTwoCustomFields.class);

        testField_nameArg(factory, supplier, fieldName);
        testField_nameArg(factory, supplier, field2Name);
    }

    @SuppressWarnings("unchecked")
    private <C extends JPanel, CF extends JPanelFixture> void testField_nameArg(
            FixtureFactory<BeanWithTwoCustomFields, C, CF> factory, FormsSupplier<BeanWithTwoCustomFields, CF> supplier, String fieldName) {
        CF fieldFixture = factory.fixture(supplier, fieldName);

        assertThat(fieldFixture).isNotNull();
        C field = (C) fieldFixture.target();
        assertThat(field.getName()).isEqualTo(fieldName);
        assertThat(SwingUtilities.getAncestorOfClass(JFormPane.class, field)).isEqualTo(supplier.targetForm);
    }

    private <C extends JPanel> ExpectedException expectComponentLookupException(Class<? super C> componentClass, ComponentLookupExceptionType type, String... otherMessages) {
        expectComponentLookupException(type).expectMessage(componentClass.getName());
        for (String otherMessage : otherMessages) {
            thrown.expectMessage(otherMessage);
        }
        return thrown;
    }

    private ExpectedException expectComponentLookupException(ComponentLookupExceptionType type) {
        thrown.expect(ComponentLookupException.class);
        thrown.expectMessage(type.message);
        return thrown;
    }

    public static enum ComponentLookupExceptionType {
        NO_MATCH("Unable to find component"),
        MULTIPLE_MATCHES("Found more than one component");

        private final String message;

        private ComponentLookupExceptionType(String message) {
            this.message = message;
        }
    }

    private static interface FixtureFactory<B, C extends JPanel, CF extends JPanelFixture> {
        CF fixture(FormsSupplier<B, CF> supplier);

        CF fixture(FormsSupplier<B, CF> supplier, GenericTypeMatcher<C> matcher);

        CF fixture(FormsSupplier<B, CF> supplier, String name);
    }

    private static class JPathFixtureFactory<B> implements FixtureFactory<B, JPath, JPathFixture> {
        @Override
        public JPathFixture fixture(FormsSupplier<B, JPathFixture> supplier) {
            return supplier.formFixture.path();
        }

        @Override
        public JPathFixture fixture(FormsSupplier<B, JPathFixture> supplier, GenericTypeMatcher<JPath> matcher) {
            return supplier.formFixture.path(matcher);
        }

        @Override
        public JPathFixture fixture(FormsSupplier<B, JPathFixture> supplier, String name) {
            return supplier.formFixture.path(name);
        }
    }

    private static class ListPanelFixtureFactory<B, C extends JComponent> implements FixtureFactory<B, ListPanel<B, C>, ListPanelFixture<B, C>> {
        @Override
        public ListPanelFixture<B, C> fixture(FormsSupplier<B, ListPanelFixture<B, C>> supplier) {
            return supplier.formFixture.listPanel();
        }

        @Override
        public ListPanelFixture<B, C> fixture(FormsSupplier<B, ListPanelFixture<B, C>> supplier, GenericTypeMatcher<ListPanel<B, C>> matcher) {
            return supplier.formFixture.listPanel(matcher);
        }

        @Override
        public ListPanelFixture<B, C> fixture(FormsSupplier<B, ListPanelFixture<B, C>> supplier, String name) {
            return supplier.formFixture.listPanel(name);
        }
    }

    private static class FieldNameMatcher<C extends JComponent> extends GenericTypeMatcher<C> {
        private final String fieldName;

        public FieldNameMatcher(Class<C> componentClass, String fieldName) {
            super(componentClass, true);
            this.fieldName = fieldName;
        }

        @Override
        protected boolean isMatching(C component) {
            return new NameMatcher(fieldName, true).matches(component);
        }
    }

    private <B, C extends JPanel, CF extends JPanelFixture> FormsSupplier<B, CF> buildAndShowWindowWithCustomField(final Class<B> beanClass) throws Exception {
        final String formName = JFormPane.getDefaultPanelName(beanClass);
        Supplier<JFormPane<B>> formSupplier = () -> {
            final FormBuilder<B> builder = createBuilder(null, beanClass);
            final JFormPane<B> formPane = new JFormPane<>(builder, "title", JFormPane.Mode.CREATE);
            formPane.setName(formName);
            return formPane;
        };
        FormsSupplier<B, CF> allFormsSupplier = new FormsSupplier<>(formSupplier);
        buildAndShowWindow(allFormsSupplier);
        allFormsSupplier.noiseForm.setName("noiseForm"); // avoid name clash for the 2 JFormPanes
        allFormsSupplier.formFixture = new JFormPaneFixture(robot(), formName);
        assertThat(allFormsSupplier.targetForm).isSameAs(allFormsSupplier.formFixture.target());

        return allFormsSupplier;
    }

    private static class FormsSupplier<B, CF extends JPanelFixture> implements Supplier<JPanel> {
        private final Supplier<JFormPane<B>> formSupplier;
        private JFormPane<B> targetForm;
        private JFormPane<B> noiseForm;

        private JFormPaneFixture formFixture;
        private CF fieldFixture;

        private FormsSupplier(Supplier<JFormPane<B>> formSupplier) {
            this.formSupplier = formSupplier;
        }

        @Override
        public JPanel get() {
            JPanel allFormsPanel = new JPanel(new GridLayout(2, 1));

            noiseForm = formSupplier.get();
            allFormsPanel.add(noiseForm);

            targetForm = formSupplier.get();
            allFormsPanel.add(targetForm);

            return allFormsPanel;
        }
    }

    /**
     * A bean that won't need our custom fields (like JPath, ListPanel ...).
     */
    public static class BeanWithoutCustomField {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * A bean that will need one instance of our custom fields (like JPath, ListPanel ...).
     */
    public static class BeanWithOneCustomField extends BeanWithoutCustomField {
        private File path;
        private java.util.List<String> list;

        public File getPath() {
            return path;
        }

        public void setPath(File path) {
            this.path = path;
        }

        public java.util.List<String> getList() {
            return list;
        }

        public void setList(java.util.List<String> list) {
            this.list = list;
        }
    }

    /**
     * A bean that will need two instances of our custom fields (like JPath, ListPanel ...).
     */
    public static class BeanWithTwoCustomFields extends BeanWithOneCustomField {
        private File path2;
        private java.util.List<String> list2;

        public File getPath2() {
            return path2;
        }

        public void setPath2(File path2) {
            this.path2 = path2;
        }

        public java.util.List<String> getList2() {
            return list2;
        }

        public void setList2(java.util.List<String> list2) {
            this.list2 = list2;
        }
    }

    private static abstract class BaseSupplier<C extends JComponent> implements Supplier<C> {
        protected JFormPane.Mode mode;
        protected String title = TITLE;
    }

    public static class Action<C extends JComponent> {
        protected final boolean inDialog;
        protected final BaseSupplier<C> supplier;

        private Action(boolean inDialog, BaseSupplier<C> supplier) {
            this.inDialog = inDialog;
            this.supplier = supplier;
        }

        @SuppressWarnings("unchecked")
        public final JComponent openForm(Component parentComponent, AbstractSwingTest test, JFormPane.Mode mode) throws Exception {
            return openForm(parentComponent, test, mode, null);
        }

        public JComponent openForm(Component parentComponent, AbstractSwingTest test, JFormPane.Mode mode, String title) throws Exception {
            supplier.mode = mode;
            supplier.title = (title == null) ? TITLE : title;
            return test.buildAndShowWindow(supplier);
        }
    }

    private static abstract class CustomSupplier<C extends JComponent> extends BaseSupplier<C> {
        protected Component parentComponent;
    }

    private static class MockFormBuilder<B> extends DefaultFormBuilder<B> {
        private final Container parentComponent;

        public MockFormBuilder(Container parentComponent, Class<B> beanClass) {
            super(beanClass);
            this.parentComponent = parentComponent;
        }

        @Override
        protected void configureBuilder(org.formbuilder.FormBuilder<B> builder) {
            super.configureBuilder(builder);
            TypeMapper mapper = Mockito.mock(TypeMapper.class);
            when(mapper.createEditorComponent()).thenAnswer(invocation -> {
                JList<String> list = new JList<>(new DefaultMutableListModel<>());
                list.setName("strings");

                SimpleItemManager<String> sourceProvider = new SimpleItemManager<>(String.class, parentComponent, "Strings", DIALOG);
                return new ListPanel<>(list, sourceProvider);
            });
            when(mapper.getValueClass()).thenReturn(java.util.List.class);
            builder.useForProperty("list", mapper);
            builder.useForProperty("list2", mapper);
        }
    }

    private static <B> FormBuilder<B> createBuilder(Container parentComponent, Class<B> beanClass) {
        return new MockFormBuilder<>(parentComponent, beanClass);
    }
}
