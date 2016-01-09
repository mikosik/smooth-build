package org.smoothbuild.lang.module;

import static org.smoothbuild.SmoothConstants.SMOOTH_HOME_ENV_VARIABLE;
import static org.smoothbuild.SmoothConstants.SMOOTH_HOME_LIB_DIR;
import static org.smoothbuild.lang.module.NativeModuleFactory.loadNativeModulesFromDir;

import java.nio.file.Paths;
import java.util.Collection;

import javax.inject.Singleton;

import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.nativ.NativeFunctionImplementationException;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ModuleModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  public Functions provideBuiltinPackage() throws NativeFunctionImplementationException {
    Collection<Function> module = loadNativeModulesFromDir(Paths.get(smoothHomeDir(),
        SMOOTH_HOME_LIB_DIR));
    Functions functions = new Functions();
    for (Function function : module) {
      functions.add(function);
    }
    return functions;
  }

  private static String smoothHomeDir() {
    String smoothHomeDir = System.getenv(SMOOTH_HOME_ENV_VARIABLE);
    if (smoothHomeDir == null) {
      throw new RuntimeException("Environment variable '" + SMOOTH_HOME_ENV_VARIABLE
          + "' not set.");
    }
    return smoothHomeDir;
  }
}
