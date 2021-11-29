package org.smoothbuild.slib.java.javac;

import static java.nio.charset.Charset.defaultCharset;
import static org.smoothbuild.slib.java.javac.PackagedJavaFileObjects.classesFromJarFiles;

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

import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.plugin.NativeApi;

public class JavacFunc {
  public static ArrayH func(NativeApi nativeApi, ArrayH srcs, ArrayH libs, ArrayH options)
      throws IOException {
    return new Worker(nativeApi, srcs, libs, options).execute();
  }

  private static class Worker {
    private final JavaCompiler compiler;
    private final NativeApi nativeApi;
    private final ArrayH srcs;
    private final ArrayH libs;
    private final ArrayH options;

    public Worker(
        NativeApi nativeApi,
        ArrayH srcs,
        ArrayH libs,
        ArrayH options) {
      this.compiler = ToolProvider.getSystemJavaCompiler();
      this.nativeApi = nativeApi;
      this.srcs = srcs;
      this.libs = libs;
      this.options = options;
    }

    public ArrayH execute() throws IOException {
      if (compiler == null) {
        nativeApi.log().error("Couldn't find JavaCompiler implementation. "
            + "You have to run Smooth tool using JDK (not JVM). Only JDK contains java compiler.");
        return null;
      }
      return compile(srcs);
    }

    public ArrayH compile(ArrayH files) throws IOException {
      // prepare args for compilation

      StringWriter additionalCompilerOutput = new StringWriter();
      LoggingDiagnosticListener diagnostic = new LoggingDiagnosticListener(nativeApi);
      Iterable<String> options = options();
      StandardJavaFileManager fileManager1 =
          compiler.getStandardFileManager(diagnostic, null, defaultCharset());
      var libsClasses = classesFromJarFiles(nativeApi, libs.elems(TupleH.class));
      if (libsClasses == null) {
        return null;
      }
      try (SandboxedJavaFileManager fileManager = new SandboxedJavaFileManager(
          fileManager1, nativeApi, libsClasses)) {
        Iterable<InputSourceFile> inputSourceFiles = toJavaFiles(files.elems(TupleH.class));

        /*
         * Java compiler fails miserably when there's no java files.
         */
        if (!inputSourceFiles.iterator().hasNext()) {
          nativeApi.log().warning("Param 'srcs' is empty list.");
          return nativeApi.factory().arrayBuilder(nativeApi.factory().fileT()).build();
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
      return StreamSupport.stream(options.elems(StringH.class).spliterator(), false)
          .map(StringH::jValue)
          .collect(Collectors.toList());
    }

    private static Iterable<InputSourceFile> toJavaFiles(Iterable<TupleH> sourceFiles) {
      ArrayList<InputSourceFile> result = new ArrayList<>();
      for (TupleH file : sourceFiles) {
        result.add(new InputSourceFile(file));
      }
      return result;
    }
  }
}