package org.smoothbuild.lang.function.nativ;

import static org.junit.Assert.assertEquals;
import static org.smoothbuild.lang.function.nativ.NativeFunctionFactory.nativeFunctions;

import java.util.Set;

import org.smoothbuild.lang.function.nativ.err.NativeImplementationException;
import org.testory.Closure;

import com.google.common.hash.HashCode;

public class TestingUtils {

  public static Closure $nativeFunctions(final Class<?> clazz) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return nativeFunctions(clazz, HashCode.fromInt(13));
      }
    };
  }

  public static NativeFunction function(Class<?> clazz) throws NativeImplementationException {
    return function(clazz, HashCode.fromInt(13));
  }

  public static NativeFunction function(Class<?> clazz, HashCode hash)
      throws NativeImplementationException {
    Set<NativeFunction> functions = nativeFunctions(clazz, hash);
    assertEquals(1, functions.size());
    return functions.iterator().next();
  }
}
