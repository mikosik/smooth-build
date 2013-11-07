package org.smoothbuild.builtin;

import javax.inject.Singleton;

import org.smoothbuild.function.base.Module;
import org.smoothbuild.function.base.ModuleBuilder;
import org.smoothbuild.function.nativ.NativeFunctionFactory;
import org.smoothbuild.function.nativ.exc.NativeImplementationException;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class BuiltinModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  @Builtin
  public Module provideBuiltinModule(ModuleBuilder builder) throws NativeImplementationException {
    for (Class<?> klass : BuiltinFunctions.BUILTIN_FUNCTION_CLASSES) {
      builder.addFunction(NativeFunctionFactory.create(klass, true));
    }
    return builder.build();
  }
}
