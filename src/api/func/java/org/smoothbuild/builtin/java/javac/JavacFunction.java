package org.smoothbuild.builtin.java.javac;

import static java.nio.charset.Charset.defaultCharset;
import static org.smoothbuild.builtin.java.javac.PackagedJavaFileObjects.classesFromJars;
import static org.smoothbuild.lang.message.MessageException.errorException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.smoothbuild.io.fs.base.FileSystemException;
import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.message.WarningMessage;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;

public class JavacFunction {
  @SmoothFunction
  public static Array javac_(NativeApi nativeApi, Array sources, Array libs, Array options) {
    return new Worker(nativeApi, sources, libs, options).execute();
  }

  private static class Worker {
    private final JavaCompiler compiler;
    private final NativeApi nativeApi;
    private final Array sources;
    private final Array libs;
    private final Array options;

    public Worker(
        NativeApi nativeApi,
        Array sources,
        Array libs,
        Array options) {
      this.compiler = ToolProvider.getSystemJavaCompiler();
      this.nativeApi = nativeApi;
      this.sources = sources;
      this.libs = libs;
      this.options = options;
    }

    public Array execute() {
      if (compiler == null) {
        throw errorException("Couldn't find JavaCompiler implementation. "
            + "You have to run Smooth tool using JDK (not JVM). Only JDK contains java compiler.");
      }
      return compile(sources);
    }

    public Array compile(Array files) {
      // prepare arguments for compilation

      StringWriter additionalCompilerOutput = new StringWriter();
      LoggingDiagnosticListener diagnostic = new LoggingDiagnosticListener(nativeApi);
      Iterable<String> options = options();
      SandboxedJavaFileManager fileManager = fileManager(diagnostic);

      try {
        Iterable<InputSourceFile> inputSourceFiles = toJavaFiles(files.asIterable(Struct.class));

        /*
         * Java compiler fails miserably when there's no java files.
         */
        if (!inputSourceFiles.iterator().hasNext()) {
          nativeApi.log(new WarningMessage("Param 'sources' is empty list."));
          return nativeApi.create().arrayBuilder(nativeApi.types().file()).build();
        }

        // run compilation task
        CompilationTask task =
            compiler.getTask(additionalCompilerOutput, fileManager, diagnostic, options, null,
                inputSourceFiles);
        boolean success = task.call();

        // tidy up
        if (!success && !diagnostic.errorReported()) {
          nativeApi.log(new ErrorMessage(
              "Internal error: Compilation failed but JavaCompiler reported no error message."));
        }
        String additionalInfo = additionalCompilerOutput.toString();
        if (!additionalInfo.isEmpty()) {
          nativeApi.log(new WarningMessage(additionalInfo));
        }
        return fileManager.resultClassfiles();
      } finally {
        try {
          fileManager.close();
        } catch (IOException e) {
          throw new FileSystemException(e);
        }
      }
    }

    private Iterable<String> options() {
      return StreamSupport.stream(options.asIterable(SString.class).spliterator(), false)
          .map(SString::data)
          .collect(Collectors.toList());
    }

    private SandboxedJavaFileManager fileManager(LoggingDiagnosticListener diagnostic) {
      StandardJavaFileManager fileManager =
          compiler.getStandardFileManager(diagnostic, null, defaultCharset());
      Iterable<InputClassFile> libsClasses = classesFromJars(nativeApi, libs.asIterable(
          Blob.class));
      return new SandboxedJavaFileManager(fileManager, nativeApi, libsClasses);
    }

    private static Iterable<InputSourceFile> toJavaFiles(Iterable<Struct> sourceFiles) {
      ArrayList<InputSourceFile> result = new ArrayList<>();
      for (Struct file : sourceFiles) {
        result.add(new InputSourceFile(file));
      }
      return result;
    }
  }

  public static <T> Set<T> unmodifiableSet(T... elements) {
    Set<T> set = new HashSet<>(elements.length);
    Collections.addAll(set, elements);
    return Collections.unmodifiableSet(set);
  }
}
