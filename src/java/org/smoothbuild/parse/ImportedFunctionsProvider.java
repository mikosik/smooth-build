package org.smoothbuild.parse;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.smoothbuild.message.message.MessageType.FATAL;

import javax.inject.Inject;

import org.smoothbuild.builtin.compress.UnzipFunction;
import org.smoothbuild.builtin.compress.ZipFunction;
import org.smoothbuild.builtin.file.FileFunction;
import org.smoothbuild.builtin.file.FilesFunction;
import org.smoothbuild.builtin.file.FilterFunction;
import org.smoothbuild.builtin.file.MergeFunction;
import org.smoothbuild.builtin.file.NewFileFunction;
import org.smoothbuild.builtin.file.SaveFunction;
import org.smoothbuild.builtin.java.JarFunction;
import org.smoothbuild.builtin.java.UnjarFunction;
import org.smoothbuild.builtin.java.javac.JavacFunction;
import org.smoothbuild.builtin.java.junit.JunitFunction;
import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.nativ.NativeFunctionFactory;
import org.smoothbuild.function.nativ.exc.NativeImplementationException;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.message.message.Message;

import com.google.inject.Provider;

public class ImportedFunctionsProvider implements Provider<ImportedFunctions> {
  private final NativeFunctionFactory nativeFunctionFactory;

  @Inject
  public ImportedFunctionsProvider(NativeFunctionFactory nativeFunctionFactory) {
    this.nativeFunctionFactory = nativeFunctionFactory;
  }

  @Override
  public ImportedFunctions get() {
    ImportedFunctions importedFunctions = new ImportedFunctions();

    // file related
    importedFunctions.add(createFunction(FileFunction.class));
    importedFunctions.add(createFunction(NewFileFunction.class));
    importedFunctions.add(createFunction(FilesFunction.class));
    importedFunctions.add(createFunction(FilterFunction.class));
    importedFunctions.add(createFunction(MergeFunction.class));
    importedFunctions.add(createFunction(SaveFunction.class));

    // java related
    importedFunctions.add(createFunction(JavacFunction.class));
    importedFunctions.add(createFunction(JarFunction.class));
    importedFunctions.add(createFunction(UnjarFunction.class));
    importedFunctions.add(createFunction(JunitFunction.class));

    // compression related
    importedFunctions.add(createFunction(ZipFunction.class));
    importedFunctions.add(createFunction(UnzipFunction.class));

    return importedFunctions;
  }

  private Function createFunction(Class<?> klass) {
    try {
      return nativeFunctionFactory.create(klass, true);
    } catch (NativeImplementationException e) {
      throw new ErrorMessageException(new Message(FATAL, "Bug in smooth binary: Builtin function "
          + klass.getCanonicalName() + " has implementation problem.\nJava stack trace is:\n"
          + getStackTraceAsString(e)));
    }
  }
}
