package org.smoothbuild.lang.module;

import javax.inject.Singleton;

import org.smoothbuild.lang.function.nativ.err.NativeImplementationException;
import org.smoothbuild.parse.Builtin;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ModuleModule extends AbstractModule {

  @Override
  protected void configure() {}

  @Provides
  @Singleton
  @Builtin
  public Module provideBuiltinModule() throws NativeImplementationException {
    return NativeModuleFactory.createNativeModule(builtinModuleClass(), true);
  }

  private Class<?> builtinModuleClass() {
    try {
      return Class.forName("org.smoothbuild.builtin.BuiltinSmoothModule");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
