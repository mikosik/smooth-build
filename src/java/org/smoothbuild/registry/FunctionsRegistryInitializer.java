package org.smoothbuild.registry;

import org.smoothbuild.builtin.file.FileFunction;
import org.smoothbuild.builtin.file.FilesFunction;
import org.smoothbuild.builtin.file.SaveToFunction;
import org.smoothbuild.builtin.file.UnzipFunction;
import org.smoothbuild.builtin.file.ZipFunction;
import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.registry.exc.FunctionAlreadyRegisteredException;
import org.smoothbuild.registry.exc.FunctionImplementationException;

public class FunctionsRegistryInitializer {
  private FunctionsRegistry registry;

  public void initialize() {
    register(FileFunction.class);
    register(FilesFunction.class);
    register(SaveToFunction.class);

    register(ZipFunction.class);
    register(UnzipFunction.class);
  }

  private void register(Class<? extends FunctionDefinition> klass) {
    try {
      registry.register(klass);
    } catch (FunctionImplementationException | FunctionAlreadyRegisteredException e) {
      throw new SmoothFatalException(e);
    }
  }
}
