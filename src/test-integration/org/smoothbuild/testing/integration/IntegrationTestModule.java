package org.smoothbuild.testing.integration;

import javax.inject.Singleton;

import org.smoothbuild.builtin.BuiltinSmoothModule;
import org.smoothbuild.db.objects.ObjectsDbModule;
import org.smoothbuild.db.taskoutputs.TaskOutputsDbModule;
import org.smoothbuild.lang.function.base.Module;
import org.smoothbuild.lang.function.base.ModuleBuilder;
import org.smoothbuild.lang.function.nativ.NativeModuleFactory;
import org.smoothbuild.lang.function.nativ.err.NativeImplementationException;
import org.smoothbuild.parse.Builtin;
import org.smoothbuild.testing.io.fs.base.FakeFileSystemModule;
import org.smoothbuild.testing.message.FakeUserConsoleModule;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class IntegrationTestModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new TaskOutputsDbModule());
    install(new ObjectsDbModule());
    install(new FakeFileSystemModule());
    install(new FakeUserConsoleModule());
  }

  @Provides
  @Singleton
  @Builtin
  public Module provideBuiltinModule(ModuleBuilder builder) throws NativeImplementationException {
    return NativeModuleFactory.createNativeModule(BuiltinSmoothModule.class, true);
  }
}
