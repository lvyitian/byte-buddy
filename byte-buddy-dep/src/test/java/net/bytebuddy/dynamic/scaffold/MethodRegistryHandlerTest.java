package net.bytebuddy.dynamic.scaffold;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ModifierResolver;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.attribute.MethodAttributeAppender;
import net.bytebuddy.test.utility.MockitoRule;
import net.bytebuddy.test.utility.ObjectPropertyAssertion;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.objectweb.asm.ClassVisitor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MethodRegistryHandlerTest {

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private InstrumentedType instrumentedType, preparedInstrumentedType;

    @Mock
    private Implementation implementation;

    @Mock
    private Object annotationValue;

    @Mock
    private Implementation.Target implementationTarget;

    @Mock
    private MethodAttributeAppender attributeAppender;

    @Mock
    private ClassVisitor classVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Mock
    private MethodDescription methodDescription;

    @Mock
    private ModifierResolver modifierResolver;

    @Before
    public void setUp() throws Exception {
        when(implementation.prepare(instrumentedType)).thenReturn(preparedInstrumentedType);
    }

    @Test
    public void testHandlerForAbstractMethod() throws Exception {
        MethodRegistry.Handler handler = new MethodRegistry.Handler.ForAbstractMethod(modifierResolver);
        assertThat(handler.prepare(instrumentedType), is(instrumentedType));
        TypeWriter.MethodPool.Record record = handler.compile(implementationTarget).assemble(attributeAppender, methodDescription);
        assertThat(record.getSort(), is(TypeWriter.MethodPool.Record.Sort.DEFINED));
        assertThat(record.getModifierResolver(), is(modifierResolver));
    }

    @Test
    public void testHandlerForImplementation() throws Exception {
        MethodRegistry.Handler handler = new MethodRegistry.Handler.ForImplementation(implementation, modifierResolver);
        assertThat(handler.prepare(instrumentedType), is(preparedInstrumentedType));
        TypeWriter.MethodPool.Record record = handler.compile(implementationTarget).assemble(attributeAppender, methodDescription);
        assertThat(record.getSort(), is(TypeWriter.MethodPool.Record.Sort.IMPLEMENTED));
        assertThat(record.getModifierResolver(), is(modifierResolver));
    }

    @Test
    public void testHandlerForAnnotationValue() throws Exception {
        MethodRegistry.Handler handler = new MethodRegistry.Handler.ForAnnotationValue(annotationValue, modifierResolver);
        assertThat(handler.prepare(instrumentedType), is(instrumentedType));
        TypeWriter.MethodPool.Record record = handler.compile(implementationTarget).assemble(attributeAppender, methodDescription);
        assertThat(record.getSort(), is(TypeWriter.MethodPool.Record.Sort.DEFINED));
        assertThat(record.getModifierResolver(), is(modifierResolver));
    }

    @Test(expected = IllegalStateException.class)
    public void testVisibilityBridgeHandlerPreparationThrowsException() throws Exception {
        MethodRegistry.Handler.ForVisibilityBridge.INSTANCE.prepare(mock(InstrumentedType.class));
    }

    @Test
    public void testObjectProperties() throws Exception {
        ObjectPropertyAssertion.of(MethodRegistry.Handler.ForAbstractMethod.class).apply();
        ObjectPropertyAssertion.of(MethodRegistry.Handler.ForImplementation.class).apply();
        ObjectPropertyAssertion.of(MethodRegistry.Handler.ForImplementation.Compiled.class).apply();
        ObjectPropertyAssertion.of(MethodRegistry.Handler.ForAnnotationValue.class).apply();
        ObjectPropertyAssertion.of(MethodRegistry.Handler.ForVisibilityBridge.class).apply();
        ObjectPropertyAssertion.of(MethodRegistry.Handler.ForVisibilityBridge.Compiled.class).apply();
    }
}
