package org.smoothbuild.lang.function.nativ;

import static org.smoothbuild.lang.function.nativ.NativeFunctionFactory.createNativeFunction;

import java.lang.reflect.Method;

import org.smoothbuild.lang.function.base.Module;
import org.smoothbuild.lang.function.base.ModuleBuilder;
import org.smoothbuild.lang.function.nativ.err.NativeImplementationException;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class NativeModuleFactory {
  public static Module createNativeModule(Class<?> moduleClass, boolean isBuiltin)
      throws NativeImplementationException {
    ModuleBuilder builder = new ModuleBuilder();
    for (Method method : moduleClass.getDeclaredMethods()) {
      if (method.isAnnotationPresent(SmoothFunction.class)) {
        builder.addFunction(createNativeFunction(method, isBuiltin));
      }
    }
    return builder.build();
  }
}
