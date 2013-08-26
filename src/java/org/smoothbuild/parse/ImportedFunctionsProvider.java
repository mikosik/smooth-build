package org.smoothbuild.parse;

import javax.inject.Inject;

import org.smoothbuild.builtin.compress.UnzipFunction;
import org.smoothbuild.builtin.compress.ZipFunction;
import org.smoothbuild.builtin.file.FileFunction;
import org.smoothbuild.builtin.file.FilesFunction;
import org.smoothbuild.builtin.file.SaveToFunction;
import org.smoothbuild.function.Function;
import org.smoothbuild.function.FunctionFactory;
import org.smoothbuild.function.exc.PluginImplementationException;

import com.google.inject.Provider;

public class ImportedFunctionsProvider implements Provider<ImportedFunctions> {
  private final FunctionFactory functionFactory;

  @Inject
  public ImportedFunctionsProvider(FunctionFactory functionFactory) {
    this.functionFactory = functionFactory;
  }

  @Override
  public ImportedFunctions get() {
    ImportedFunctions importedFunctions = new ImportedFunctions();

    importedFunctions.add(createFunction(FileFunction.class));
    importedFunctions.add(createFunction(FilesFunction.class));
    importedFunctions.add(createFunction(SaveToFunction.class));

    importedFunctions.add(createFunction(ZipFunction.class));
    importedFunctions.add(createFunction(UnzipFunction.class));

    return importedFunctions;
  }

  private Function createFunction(Class<?> klass) {
    try {
      return functionFactory.create(klass);
    } catch (PluginImplementationException e) {
      throw new RuntimeException("Builtin plugin " + klass.getCanonicalName()
          + " has implementation problem.", e);
    }
  }
}
