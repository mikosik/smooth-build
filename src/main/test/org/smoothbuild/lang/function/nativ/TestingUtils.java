package org.smoothbuild.lang.function.nativ;

import static org.junit.Assert.assertEquals;
import static org.smoothbuild.lang.function.nativ.NativeFunctionFactory.nativeFunctions;

import java.util.Set;

import com.google.common.hash.HashCode;

public class TestingUtils {
  public static NativeFunction function(Class<?> clazz)
      throws NativeFunctionImplementationException {
    return function(clazz, HashCode.fromInt(13));
  }

  public static NativeFunction function(Class<?> clazz, HashCode hash)
      throws NativeFunctionImplementationException {
    Set<NativeFunction> functions = nativeFunctions(clazz, hash);
    assertEquals(1, functions.size());
    return functions.iterator().next();
  }
}
