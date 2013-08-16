package org.smoothbuild.parse;

import org.smoothbuild.builtin.file.FileFunction;
import org.smoothbuild.builtin.file.FilesFunction;
import org.smoothbuild.builtin.file.SaveToFunction;
import org.smoothbuild.builtin.file.UnzipFunction;
import org.smoothbuild.builtin.file.ZipFunction;
import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.registry.exc.FunctionImplementationException;
import org.smoothbuild.registry.instantiate.Function;
import org.smoothbuild.registry.instantiate.FunctionFactory;

public class ImportedFunctionsInitializer {
  private final ImportedFunctions importedFunctions;
  private final FunctionFactory functionFactory;

  public ImportedFunctionsInitializer(ImportedFunctions importedFunctions,
      FunctionFactory functionFactory) {
    this.importedFunctions = importedFunctions;
    this.functionFactory = functionFactory;
  }

  public void initialize() {
    register(FileFunction.class);
    register(FilesFunction.class);
    register(SaveToFunction.class);

    register(ZipFunction.class);
    register(UnzipFunction.class);
  }

  private void register(Class<? extends FunctionDefinition> klass) {
    try {
      add(klass);
    } catch (FunctionImplementationException e) {
      throw new SmoothFatalException(e);
    }
  }

  public void add(Class<? extends FunctionDefinition> klass) throws FunctionImplementationException {
    Function function = functionFactory.create(klass);
    importedFunctions.add(function);
  }

}
