package org.smoothbuild.lang.function.nativ;

import static org.smoothbuild.lang.function.nativ.TestingUtils.$nativeFunctions;
import static org.smoothbuild.lang.function.nativ.TestingUtils.function;
import static org.smoothbuild.lang.type.Types.NIL;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.lang.type.Types.STRING_ARRAY;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.function.nativ.err.IllegalResultTypeException;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;

public class NativeFunctionTypeTest {
  private NativeFunction function;

  @Test
  public void function_return_type_is_equal_to_method_return_type() throws Exception {
    given(function = function(FunctionReturningString.class));
    when(function.type());
    thenReturned(STRING);
  }

  public static class FunctionReturningString {
    @SmoothFunction
    public static SString myFunction(Container container) {
      return null;
    }
  }

  @Test
  public void function_return_type_is_equal_to_method_return_type_for_string_array()
      throws Exception {
    given(function = function(FunctionReturningStringArray.class));
    when(function.type());
    thenReturned(STRING_ARRAY);
  }

  public static class FunctionReturningStringArray {
    @SmoothFunction
    public static Array<SString> myFunction(Container container) {
      return null;
    }
  }

  @Test
  public void method_with_illegal_return_type_causes_exception() throws Exception {
    when($nativeFunctions(IllegalReturnType.class));
    thenThrown(IllegalResultTypeException.class);
  }

  public static class IllegalReturnType {
    @SmoothFunction
    public static Object function(Container container) {
      return null;
    }
  }

  @Test
  public void method_with_value_as_return_type_causes_exception() throws Exception {
    when($nativeFunctions(ValueAsReturnType.class));
    thenThrown(IllegalResultTypeException.class);
  }

  public static class ValueAsReturnType {
    @SmoothFunction
    public static Value function(Container container) {
      return null;
    }
  }

  @Test
  public void method_with_void_return_type_causes_exception() throws Exception {
    when($nativeFunctions(VoidReturnType.class));
    thenThrown(IllegalResultTypeException.class);
  }

  public static class VoidReturnType {
    @SmoothFunction
    public static void function(Container container) {
      return;
    }
  }

  @Test
  public void method_with_nil_return_type_is_allowed() throws Exception {
    given(function = function(NilReturnType.class));
    when(function.type());
    thenReturned(NIL);
  }

  public static class NilReturnType {
    @SmoothFunction
    public static Array<Nothing> function(Container container) {
      return container.create().arrayBuilder(Nothing.class).build();
    }
  }
}
