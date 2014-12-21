package org.smoothbuild.lang.function.nativ;

import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.nativ.TestingUtils.$nativeFunctions;
import static org.smoothbuild.lang.function.nativ.TestingUtils.function;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.function.nativ.err.IllegalFunctionNameException;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class NativeFunctionNameTest {
  @Test
  public void method_with_name_that_is_illegal_smooth_name_causes_exception() throws Exception {
    when($nativeFunctions(IllegalFunctionName.class));
    thenThrown(IllegalFunctionNameException.class);
  }

  public static class IllegalFunctionName {
    @SmoothFunction
    public static SString illegal_$_name(NativeApi nativeApi) {
      return null;
    }
  }

  @Test
  public void function_name_is_equal_to_method_name() throws Exception {
    when(function(SimpleFunction.class).name());
    thenReturned(name("myFunction"));
  }

  public static class SimpleFunction {
    @SmoothFunction
    public static SString myFunction(NativeApi nativeApi) {
      return null;
    }
  }
}
