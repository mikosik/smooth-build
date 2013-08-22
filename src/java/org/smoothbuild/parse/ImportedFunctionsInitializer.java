package org.smoothbuild.parse;

import org.smoothbuild.builtin.compress.UnzipFunction;
import org.smoothbuild.builtin.compress.ZipFunction;
import org.smoothbuild.builtin.file.FileFunction;
import org.smoothbuild.builtin.file.FilesFunction;
import org.smoothbuild.builtin.file.SaveToFunction;
import org.smoothbuild.function.Function;
import org.smoothbuild.function.FunctionFactory;
import org.smoothbuild.function.exc.PluginImplementationException;

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

  private void register(Class<?> klass) {
    try {
      add(klass);
    } catch (PluginImplementationException e) {
      throw new RuntimeException("Builtin plugin " + klass.getCanonicalName()
          + " has implementation problem.", e);
    }
  }

  public void add(Class<?> klass) throws PluginImplementationException {
    Function function = functionFactory.create(klass);
    importedFunctions.add(function);
  }

}
