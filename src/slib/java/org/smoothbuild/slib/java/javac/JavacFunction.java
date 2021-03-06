package org.smoothbuild.slib.java.javac;

import static java.nio.charset.Charset.defaultCharset;
import static org.smoothbuild.slib.java.javac.PackagedJavaFileObjects.classesFromJars;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.zip.ZipException;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.smoothbuild.db.record.base.Array;
import org.smoothbuild.db.record.base.Blob;
import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.db.record.base.Tuple;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

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
        return null;
      }
      return compile(srcs);
    }

    public Array compile(Array files) throws IOException {
      // prepare arguments for compilation

      StringWriter additionalCompilerOutput = new StringWriter();
      LoggingDiagnosticListener diagnostic = new LoggingDiagnosticListener(nativeApi);
      Iterable<String> options = options();
      StandardJavaFileManager fileManager1 =
          compiler.getStandardFileManager(diagnostic, null, defaultCharset());
      var libsClasses = classesFromJars(nativeApi, libs.asIterable(Blob.class));
      if (libsClasses == null) {
        return null;
      }
      try (SandboxedJavaFileManager fileManager = new SandboxedJavaFileManager(
          fileManager1, nativeApi, libsClasses)) {
        Iterable<InputSourceFile> inputSourceFiles = toJavaFiles(files.asIterable(Tuple.class));

        /*
         * Java compiler fails miserably when there's no java files.
         */
        if (!inputSourceFiles.iterator().hasNext()) {
          nativeApi.log().warning("Param 'srcs' is empty list.");
          return nativeApi.factory().arrayBuilder(nativeApi.factory().fileSpec()).build();
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
      } catch (ZipException e) {
        nativeApi.log().error("Cannot read archive. Corrupted data?");
        return null;
      }
    }

    private Iterable<String> options() {
      return StreamSupport.stream(options.asIterable(RString.class).spliterator(), false)
          .map(RString::jValue)
          .collect(Collectors.toList());
    }

    private static Iterable<InputSourceFile> toJavaFiles(Iterable<Tuple> sourceFiles) {
      ArrayList<InputSourceFile> result = new ArrayList<>();
      for (Tuple file : sourceFiles) {
        result.add(new InputSourceFile(file));
      }
      return result;
    }
  }
}
