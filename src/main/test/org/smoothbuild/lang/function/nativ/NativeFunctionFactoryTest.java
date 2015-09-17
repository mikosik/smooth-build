package org.smoothbuild.lang.function.nativ;

import static org.hamcrest.Matchers.empty;
import static org.smoothbuild.lang.function.nativ.NativeFunctionFactory.nativeFunctions;
import static org.smoothbuild.lang.function.nativ.TestingUtils.$nativeFunctions;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.function.nativ.err.NonPublicSmoothFunctionException;
import org.smoothbuild.lang.function.nativ.err.NonStaticSmoothFunctionException;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class NativeFunctionFactoryTest {
  @Test
  public void methods_without_annotation_are_not_treated_as_smooth_functions() throws Exception {
    when(nativeFunctions(NotAnnotatedMethod.class, null));
    thenReturned(empty());
  }

  public static class NotAnnotatedMethod {
    public static SString function(Container container) {
      return null;
    }
  }

  @Test
  public void non_static_method_with_annotation_causes_exception() throws Exception {
    when($nativeFunctions(NonStaticMethod.class));
    thenThrown(NonStaticSmoothFunctionException.class);
  }

  public static class NonStaticMethod {
    @SmoothFunction
    public SString function(Container container) {
      return null;
    }
  }

  @Test
  public void non_public_method_with_annotation_causes_exception() throws Exception {
    when($nativeFunctions(NonPublicMethod.class));
    thenThrown(NonPublicSmoothFunctionException.class);
  }

  public static class NonPublicMethod {
    @SmoothFunction
    protected static SString function(Container container) {
      return null;
    }
  }
}
