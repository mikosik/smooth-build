package org.smoothbuild.lang.function.nativ;

import static org.smoothbuild.lang.function.nativ.NativeFunctionFactory.nativeFunctions;
import static org.smoothbuild.lang.function.nativ.TestingUtils.function;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.hash.HashCode;

public class NativeFunctionParameterTest {
  private NativeFunction function;

  @Test
  public void parameter_name_is_taken_from_java_parameter_name() throws Exception {
    given(function = function(ParameterName.class));
    when(function.parameters().get(0).name());
    thenReturned(new Name("parameter"));
  }

  public static class ParameterName {
    @SmoothFunction
    public static SString function(Container container, SString parameter) {
      return null;
    }
  }

  @Test
  public void method_without_native_api_parameter_causes_exception() throws Exception {
    when(() -> nativeFunctions(MissingContainer.class, HashCode.fromInt(13)));
    thenThrown(exception(new NativeFunctionImplementationException(
        MissingContainer.class.getMethod("function"),
        "Its first parameter should have '" + Container.class.getCanonicalName() + "' type.")));
  }

  public static class MissingContainer {
    @SmoothFunction
    public static SString function() {
      return null;
    }
  }

  @Test
  public void method_with_first_parameter_that_is_not_native_api_causes_exception()
      throws Exception {
    when(() -> nativeFunctions(NotContainer.class, HashCode.fromInt(13)));
    thenThrown(exception(new NativeFunctionImplementationException(
        NotContainer.class.getMethod("function", SString.class),
        "Its first parameter should have '" + Container.class.getCanonicalName() + "' type.")));
  }

  public static class NotContainer {
    @SmoothFunction
    public static SString function(SString parameter) {
      return null;
    }
  }

  @Test
  public void method_with_first_parameter_that_is_native_api_impl_is_accepted() throws Exception {
    when(nativeFunctions(WithContainerImpl.class, HashCode.fromInt(13)));
    thenReturned();
  }

  public static class WithContainerImpl {
    @SmoothFunction
    public static SString function(ContainerImpl container) {
      return null;
    }
  }

  @Test
  public void method_with_illegal_parameter_type_causes_exception() throws Exception {
    when(() -> nativeFunctions(IllegalType.class, HashCode.fromInt(13)));
    thenThrown(exception(new NativeFunctionImplementationException(
        IllegalType.class.getMethod("function", Container.class, Object.class),
        "It has parameter with illegal java type 'java.lang.Object'.")));
  }

  public static class IllegalType {
    @SmoothFunction
    public static SString function(Container container, Object parameter) {
      return null;
    }
  }

  @Test
  public void method_with_parameter_with_value_type_causes_exception() throws Exception {
    when(() -> nativeFunctions(ValueType.class, HashCode.fromInt(13)));
    thenThrown(exception(new NativeFunctionImplementationException(
        ValueType.class.getMethod("function", Container.class, Value.class),
        "It has parameter with illegal java type 'org.smoothbuild.lang.value.Value'.")));
  }

  public static class ValueType {
    @SmoothFunction
    public static SString function(Container container, Value parameter) {
      return null;
    }
  }

  @Test
  public void parameter_is_not_required_by_default() throws Exception {
    given(function = function(NotRequiredParameter.class));
    when(function.parameters().get(0).isRequired());
    thenReturned(false);
  }

  public static class NotRequiredParameter {
    @SmoothFunction
    public static SString function(Container container, SString parameter) {
      return null;
    }
  }

  @Test
  public void parameter_annotated_with_required_annotation_is_required() throws Exception {
    given(function = function(RequiredParameter.class));
    when(function.parameters().get(0).isRequired());
    thenReturned(true);
  }

  public static class RequiredParameter {
    @SmoothFunction
    public static SString function(Container container, @Required SString parameter) {
      return null;
    }
  }
}
