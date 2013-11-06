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
import org.smoothbuild.db.task.TaskDb;
import org.smoothbuild.function.nativ.NativeFunction;
import org.smoothbuild.function.nativ.NativeFunctionFactory;
import org.smoothbuild.function.nativ.exc.NativeImplementationException;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.message.message.Message;

import com.google.inject.Provider;

public class ImportedFunctionsProvider implements Provider<ImportedFunctions> {
  private final TaskDb taskDb;

  @Inject
  public ImportedFunctionsProvider(TaskDb taskDb) {
    this.taskDb = taskDb;
  }

  @Override
  public ImportedFunctions get() {
    ImportedFunctions importedFunctions = new ImportedFunctions(taskDb);

    // file related
    importedFunctions.add(function(FileFunction.class));
    importedFunctions.add(function(FilesFunction.class));
    importedFunctions.add(function(NewFileFunction.class));
    importedFunctions.add(function(FilterFunction.class));
    importedFunctions.add(function(MergeFunction.class));
    importedFunctions.add(function(SaveFunction.class));

    // java related
    importedFunctions.add(function(JavacFunction.class));
    importedFunctions.add(function(JarFunction.class));
    importedFunctions.add(function(UnjarFunction.class));
    importedFunctions.add(function(JunitFunction.class));

    // compression related
    importedFunctions.add(function(ZipFunction.class));
    importedFunctions.add(function(UnzipFunction.class));

    return importedFunctions;
  }

  private NativeFunction function(Class<?> klass) {
    try {
      return NativeFunctionFactory.create(klass, true);
    } catch (NativeImplementationException e) {
      throw new ErrorMessageException(new Message(FATAL, "Bug in smooth binary: Builtin function "
          + klass.getCanonicalName() + " has implementation problem.\nJava stack trace is:\n"
          + getStackTraceAsString(e)));
    }
  }
}
