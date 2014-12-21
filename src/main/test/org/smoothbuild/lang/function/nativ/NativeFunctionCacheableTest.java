package org.smoothbuild.lang.function.nativ;

import static org.smoothbuild.lang.function.nativ.TestingUtils.function;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.NotCacheable;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class NativeFunctionCacheableTest {
  private NativeFunction function;

  @Test
  public void function_is_cacheable_by_default() throws Exception {
    given(function = function(WithoutAnnotation.class));
    when(function.isCacheable());
    thenReturned(true);
  }

  public static class WithoutAnnotation {
    @SmoothFunction
    public static SString myFunction(NativeApi nativeApi) {
      return null;
    }
  }

  @Test
  public void function_is_not_cacheable_when_annotated_with_not_cacheable() throws Exception {
    given(function = function(WithNotCacheableAnnotation.class));
    when(function.isCacheable());
    thenReturned(false);
  }

  public static class WithNotCacheableAnnotation {
    @SmoothFunction
    @NotCacheable
    public static SString myFunction(NativeApi nativeApi) {
      return null;
    }
  }
}
