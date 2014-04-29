package org.smoothbuild.builtin;

import javax.inject.Singleton;

import org.smoothbuild.lang.function.base.Module;
import org.smoothbuild.lang.function.base.ModuleBuilder;
import org.smoothbuild.lang.function.nativ.NativeModuleFactory;
import org.smoothbuild.lang.function.nativ.err.NativeImplementationException;
import org.smoothbuild.parse.Builtin;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class BuiltinModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  @Builtin
  public Module provideBuiltinModule(ModuleBuilder builder) throws NativeImplementationException {
    return NativeModuleFactory.createNativeModule(BuiltinSmoothModule.class, true);
  }
}
