package org.smoothbuild.lang.function.nativ;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.lang.function.nativ.TestingUtils.function;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

import com.google.common.hash.HashCode;

public class NativeFunctionHashTest {
  private NativeFunction function1;
  private NativeFunction function2;

  @Test
  public void functions_with_different_names_in_the_same_jar_have_different_hashes()
      throws Exception {
    given(function1 = function(FirstFunction.class, HashCode.fromInt(1)));
    given(function2 = function(SecondFunction.class, HashCode.fromInt(1)));
    when(function1.hash());
    thenReturned(not(function2.hash()));
  }

  @Test
  public void functions_with_same_names_in_different_jar_have_different_hashes() throws Exception {
    given(function1 = function(FirstFunction.class, HashCode.fromInt(1)));
    given(function2 = function(FirstFunction.class, HashCode.fromInt(2)));
    when(function1.hash());
    thenReturned(not(function2.hash()));
  }

  @Test
  public void functions_with_same_names_in_same_jar_have_same_hashes() throws Exception {
    given(function1 = function(FirstFunction.class, HashCode.fromInt(1)));
    given(function2 = function(FirstFunction.class, HashCode.fromInt(1)));
    when(function1.hash());
    thenReturned(function2.hash());
  }

  public static class FirstFunction {
    @SmoothFunction
    public static SString firstFunction(NativeApi nativeApi) {
      return null;
    }
  }

  public static class SecondFunction {
    @SmoothFunction
    public static SString secondFunction(NativeApi nativeApi) {
      return null;
    }
  }
}
