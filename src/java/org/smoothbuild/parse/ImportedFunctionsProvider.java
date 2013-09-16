package org.smoothbuild.parse;

import javax.inject.Inject;

import org.smoothbuild.builtin.compress.UnzipFunction;
import org.smoothbuild.builtin.compress.ZipFunction;
import org.smoothbuild.builtin.file.FileFunction;
import org.smoothbuild.builtin.file.FilesFunction;
import org.smoothbuild.builtin.file.NewFileFunction;
import org.smoothbuild.builtin.file.SaveFunction;
import org.smoothbuild.builtin.java.JarFunction;
import org.smoothbuild.builtin.java.UnjarFunction;
import org.smoothbuild.builtin.java.javac.JavacFunction;
import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.plugin.PluginFactory;
import org.smoothbuild.function.plugin.exc.PluginImplementationException;

import com.google.inject.Provider;

public class ImportedFunctionsProvider implements Provider<ImportedFunctions> {
  private final PluginFactory pluginFactory;

  @Inject
  public ImportedFunctionsProvider(PluginFactory pluginFactory) {
    this.pluginFactory = pluginFactory;
  }

  @Override
  public ImportedFunctions get() {
    ImportedFunctions importedFunctions = new ImportedFunctions();

    // file related
    importedFunctions.add(createFunction(FileFunction.class));
    importedFunctions.add(createFunction(NewFileFunction.class));
    importedFunctions.add(createFunction(FilesFunction.class));
    importedFunctions.add(createFunction(SaveFunction.class));

    // java related
    importedFunctions.add(createFunction(JavacFunction.class));
    importedFunctions.add(createFunction(JarFunction.class));
    importedFunctions.add(createFunction(UnjarFunction.class));

    // compression related
    importedFunctions.add(createFunction(ZipFunction.class));
    importedFunctions.add(createFunction(UnzipFunction.class));

    return importedFunctions;
  }

  private Function createFunction(Class<?> klass) {
    try {
      return pluginFactory.create(klass, true);
    } catch (PluginImplementationException e) {
      throw new RuntimeException("Builtin plugin " + klass.getCanonicalName()
          + " has implementation problem.", e);
    }
  }
}
