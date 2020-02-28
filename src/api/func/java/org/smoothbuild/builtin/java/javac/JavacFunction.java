package org.smoothbuild.builtin.java.javac;

import static java.nio.charset.Charset.defaultCharset;
import static org.smoothbuild.builtin.java.javac.PackagedJavaFileObjects.classesFromJars;

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

import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.plugin.AbortException;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class JavacFunction {
  @SmoothFunction("javac_")
  public static Array javac_(NativeApi nativeApi, Array srcs, Array libs, Array options)
      throws IOException {
    return new Worker(nativeApi, srcs, libs, options).execute();
  }

  private static class Worker {
    private final JavaCompiler compiler;
    private final NativeApi nativeApi;
    private final Array srcs;
    private final Array libs;
    private final Array options;

    public Worker(
        NativeApi nativeApi,
        Array srcs,
        Array libs,
        Array options) {
      this.compiler = ToolProvider.getSystemJavaCompiler();
      this.nativeApi = nativeApi;
      this.srcs = srcs;
      this.libs = libs;
      this.options = options;
    }

    public Array execute() throws IOException {
      if (compiler == null) {
        nativeApi.log().error("Couldn't find JavaCompiler implementation. "
            + "You have to run Smooth tool using JDK (not JVM). Only JDK contains java compiler.");
        throw new AbortException();
      }
      return compile(srcs);
    }

    public Array compile(Array files) throws IOException {
      // prepare arguments for compilation

      StringWriter additionalCompilerOutput = new StringWriter();
      LoggingDiagnosticListener diagnostic = new LoggingDiagnosticListener(nativeApi);
      Iterable<String> options = options();
      try (SandboxedJavaFileManager fileManager = fileManager(diagnostic)) {
        Iterable<InputSourceFile> inputSourceFiles = toJavaFiles(files.asIterable(Struct.class));

        /*
         * Java compiler fails miserably when there's no java files.
         */
        if (!inputSourceFiles.iterator().hasNext()) {
          nativeApi.log().warning("Param 'srcs' is empty list.");
          return nativeApi.factory().arrayBuilder((nativeApi.factory()).fileType()).build();
        }

        // run compilation task
        CompilationTask task =
            compiler.getTask(additionalCompilerOutput, fileManager, diagnostic, options, null,
                inputSourceFiles);
        boolean success = task.call();

        // tidy up
        if (!success && !diagnostic.errorReported()) {
          nativeApi.log().error(
              "Internal error: Compilation failed but JavaCompiler reported no error message.");
        }
        String additionalInfo = additionalCompilerOutput.toString();
        if (!additionalInfo.isEmpty()) {
          nativeApi.log().warning(additionalInfo);
        }
        return fileManager.resultClassfiles();
      }
    }

    private Iterable<String> options() {
      return StreamSupport.stream(options.asIterable(SString.class).spliterator(), false)
          .map(SString::jValue)
          .collect(Collectors.toList());
    }

    private SandboxedJavaFileManager fileManager(LoggingDiagnosticListener diagnostic)
        throws IOException {
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
