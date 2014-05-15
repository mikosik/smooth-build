package org.smoothbuild.lang.module;

import static org.smoothbuild.lang.function.nativ.NativeFunctionFactory.createNativeFunctions;

import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.function.nativ.err.NativeImplementationException;

public class NativeModuleFactory {
  public static Module createNativeModule(Class<?> moduleClass)
      throws NativeImplementationException {
    ModuleBuilder builder = new ModuleBuilder();
    for (NativeFunction<?> function : createNativeFunctions(moduleClass)) {
      builder.addFunction(function);
    }
    return builder.build();
  }
}
