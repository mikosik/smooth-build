package org.smoothbuild.lang.function.nativ;

import static org.smoothbuild.lang.function.nativ.NativeFunctionFactory.nativeFunctions;
import static org.smoothbuild.lang.function.nativ.TestingUtils.function;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.function.nativ.err.IllegalParameterTypeException;
import org.smoothbuild.lang.function.nativ.err.MissingContainerParameterException;
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
  public void parameter_name_is_taken_from_annotation() throws Exception {
    given(function = function(ParameterWithName.class));
    when(function.parameters().get(0).name());
    thenReturned("parameter");
  }

  public static class ParameterWithName {
    @SmoothFunction
    public static SString function(Container container, SString parameter) {
      return null;
    }
  }

  @Test
  public void method_without_native_api_parameter_causes_exception() throws Exception {
    when(() -> nativeFunctions(MethodWithoutContainerParameter.class, HashCode.fromInt(13)));
    thenThrown(MissingContainerParameterException.class);
  }

  public static class MethodWithoutContainerParameter {
    @SmoothFunction
    public static SString function() {
      return null;
    }
  }

  @Test
  public void method_with_first_parameter_that_is_not_native_api_causes_exception()
      throws Exception {
    when(() -> nativeFunctions(MethodWithNonContainerAsFirstParameter.class, HashCode.fromInt(13)));
    thenThrown(MissingContainerParameterException.class);
  }

  public static class MethodWithNonContainerAsFirstParameter {
    @SmoothFunction
    public static SString function(SString parameter) {
      return null;
    }
  }

  @Test
  public void method_with_first_parameter_that_is_native_api_impl_is_accepted() throws Exception {
    when(nativeFunctions(MethodWithContainerImplAsFirstParameter.class, HashCode.fromInt(13)));
    thenReturned();
  }

  public static class MethodWithContainerImplAsFirstParameter {
    @SmoothFunction
    public static SString function(ContainerImpl container) {
      return null;
    }
  }

  @Test
  public void method_with_illegal_parameter_type_causes_exception() throws Exception {
    when(() -> nativeFunctions(MethodWithParameterWithIllegalType.class, HashCode.fromInt(13)));
    thenThrown(IllegalParameterTypeException.class);
  }

  public static class MethodWithParameterWithIllegalType {
    @SmoothFunction
    public static SString function(Container container, Object parameter) {
      return null;
    }
  }

  @Test
  public void method_with_parameter_with_value_type_causes_exception() throws Exception {
    when(() -> nativeFunctions(MethodWithParameterWithValueType.class, HashCode.fromInt(13)));
    thenThrown(IllegalParameterTypeException.class);
  }

  public static class MethodWithParameterWithValueType {
    @SmoothFunction
    public static SString function(Container container, Value parameter) {
      return null;
    }
  }

  @Test
  public void parameter_is_not_required_by_default() throws Exception {
    given(function = function(MethodWithNotRequiredParameter.class));
    when(function.parameters().get(0).isRequired());
    thenReturned(false);
  }

  public static class MethodWithNotRequiredParameter {
    @SmoothFunction
    public static SString function(Container container, SString parameter) {
      return null;
    }
  }

  @Test
  public void parameter_annotated_with_required_annotation_is_required() throws Exception {
    given(function = function(MethodWithRequiredParameter.class));
    when(function.parameters().get(0).isRequired());
    thenReturned(true);
  }

  public static class MethodWithRequiredParameter {
    @SmoothFunction
    public static SString function(Container container, @Required SString parameter) {
      return null;
    }
  }
}
