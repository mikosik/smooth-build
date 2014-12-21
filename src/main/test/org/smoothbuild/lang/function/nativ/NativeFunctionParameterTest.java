package org.smoothbuild.lang.function.nativ;

import static org.smoothbuild.lang.function.nativ.TestingUtils.$nativeFunctions;
import static org.smoothbuild.lang.function.nativ.TestingUtils.function;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.function.nativ.err.DuplicatedParameterException;
import org.smoothbuild.lang.function.nativ.err.IllegalParameterNameException;
import org.smoothbuild.lang.function.nativ.err.IllegalParameterTypeException;
import org.smoothbuild.lang.function.nativ.err.MissingNameAnnotationException;
import org.smoothbuild.lang.function.nativ.err.MissingNativeApiParameterException;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;

public class NativeFunctionParameterTest {
  private NativeFunction function;

  @Test
  public void parameter_name_is_taken_from_annotation() throws Exception {
    given(function = function(ParameterWithName.class));
    when(function.parameters().get(0).name());
    thenReturned("paramName");
  }

  public static class ParameterWithName {
    @SmoothFunction
    public static SString function(NativeApi nativeApi, @Name("paramName") SString parameter) {
      return null;
    }
  }

  @Test
  public void parameter_with_name_equal_empty_string_causes_exception() throws Exception {
    when($nativeFunctions(MethodWithParameterWithNameEqualEmptyString.class));
    thenThrown(IllegalParameterNameException.class);
  }

  public static class MethodWithParameterWithNameEqualEmptyString {
    @SmoothFunction
    public static SString function(NativeApi nativeApi, @Name("") SString parameter) {
      return null;
    }
  }

  @Test
  public void parameter_with_illegal_name_causes_exception() throws Exception {
    when($nativeFunctions(MethodWithParameterWithIllegalName.class));
    thenThrown(IllegalParameterNameException.class);
  }

  public static class MethodWithParameterWithIllegalName {
    @SmoothFunction
    public static SString function(NativeApi nativeApi, @Name("abc#def") SString parameter) {
      return null;
    }
  }

  @Test
  public void parameter_without_name_annotation_causes_exception() throws Exception {
    when($nativeFunctions(MethodWithParameterWithoutName.class));
    thenThrown(MissingNameAnnotationException.class);
  }

  public static class MethodWithParameterWithoutName {
    @SmoothFunction
    public static SString function(NativeApi nativeApi, SString parameter) {
      return null;
    }
  }

  @Test
  public void two_parameters_with_the_same_name_causes_exception() throws Exception {
    when($nativeFunctions(MethodWithTwoParametersWithSameName.class));
    thenThrown(DuplicatedParameterException.class);
  }

  public static class MethodWithTwoParametersWithSameName {
    @SmoothFunction
    public static SString function(NativeApi nativeApi, @Name("nameA") SString parameterA,
        @Name("nameA") SString parameterB) {
      return null;
    }
  }

  @Test
  public void method_without_native_api_parameter_causes_exception() throws Exception {
    when($nativeFunctions(MethodWithoutNativeApiParameter.class));
    thenThrown(MissingNativeApiParameterException.class);
  }

  public static class MethodWithoutNativeApiParameter {
    @SmoothFunction
    public static SString function() {
      return null;
    }
  }

  @Test
  public void method_with_first_parameter_that_is_not_native_api_causes_exception()
      throws Exception {
    when($nativeFunctions(MethodWithNonNativeApiAsFirstParameter.class));
    thenThrown(MissingNativeApiParameterException.class);
  }

  public static class MethodWithNonNativeApiAsFirstParameter {
    @SmoothFunction
    public static SString function(SString parameter) {
      return null;
    }
  }

  @Test
  public void method_with_illegal_parameter_type_causes_exception() throws Exception {
    when($nativeFunctions(MethodWithParameterWithIllegalType.class));
    thenThrown(IllegalParameterTypeException.class);
  }

  public static class MethodWithParameterWithIllegalType {
    @SmoothFunction
    public static SString function(NativeApi nativeApi, Object parameter) {
      return null;
    }
  }

  @Test
  public void method_with_parameter_with_value_type_causes_exception() throws Exception {
    when($nativeFunctions(MethodWithParameterWithValueType.class));
    thenThrown(IllegalParameterTypeException.class);
  }

  public static class MethodWithParameterWithValueType {
    @SmoothFunction
    public static SString function(NativeApi nativeApi, Value parameter) {
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
    public static SString function(NativeApi nativeApi, @Name("name") SString parameter) {
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
    public static SString function(NativeApi nativeApi, @Required @Name("name") SString parameter) {
      return null;
    }
  }
}
