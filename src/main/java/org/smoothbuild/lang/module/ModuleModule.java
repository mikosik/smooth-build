package org.smoothbuild.lang.module;

import static org.smoothbuild.SmoothConstants.SMOOTH_HOME_ENV_VARIABLE;
import static org.smoothbuild.SmoothConstants.SMOOTH_HOME_LIB_DIR;
import static org.smoothbuild.util.ClassLoaders.loadClassFromJar;

import java.nio.file.Path;
import java.nio.file.Paths;

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
    return NativeModuleFactory.createNativeModule(builtinModuleClass());
  }

  private Class<?> builtinModuleClass() {
    Path funcsJarPath = Paths.get(smoothHomeDir(), SMOOTH_HOME_LIB_DIR, "funcs.jar");
    try {
      return loadClassFromJar(funcsJarPath, "org.smoothbuild.builtin.BuiltinSmoothModule");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private static String smoothHomeDir() {
    String smoothHomeDir = System.getenv(SMOOTH_HOME_ENV_VARIABLE);
    if (smoothHomeDir == null) {
      throw new RuntimeException("Environment variable '" + SMOOTH_HOME_ENV_VARIABLE + "' not set.");
    }
    return smoothHomeDir;
  }
}
