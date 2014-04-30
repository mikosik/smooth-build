package org.smoothbuild;

import javax.inject.Singleton;

import org.smoothbuild.db.objects.ObjectsDbModule;
import org.smoothbuild.db.taskoutputs.TaskOutputsDbModule;
import org.smoothbuild.io.fs.FileSystemModule;
import org.smoothbuild.lang.function.base.Module;
import org.smoothbuild.lang.function.nativ.NativeModuleFactory;
import org.smoothbuild.lang.function.nativ.err.NativeImplementationException;
import org.smoothbuild.parse.Builtin;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class MainModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new TaskOutputsDbModule());
    install(new ObjectsDbModule());
    install(new FileSystemModule());
  }

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
