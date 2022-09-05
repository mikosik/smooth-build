package org.smoothbuild.slib.java.javac;

import static java.nio.charset.Charset.defaultCharset;
import static org.smoothbuild.slib.compress.UnzipHelper.filesFromLibJars;
import static org.smoothbuild.slib.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.util.collect.Lists.map;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipException;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.StringB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class JavacFunc {
  public static ValB func(NativeApi nativeApi, TupleB args) throws IOException {
    ArrayB srcs = (ArrayB) args.get(0);
    ArrayB libs = (ArrayB) args.get(1);
    ArrayB options = (ArrayB) args.get(2);

    return new Worker(nativeApi, srcs, libs, options).execute();
  }

  private static class Worker {
    private final JavaCompiler compiler;
    private final NativeApi nativeApi;
    private final ArrayB srcs;
    private final ArrayB libs;
    private final ArrayB options;

    public Worker(
        NativeApi nativeApi,
        ArrayB srcs,
        ArrayB libs,
        ArrayB options) {
      this.compiler = ToolProvider.getSystemJavaCompiler();
      this.nativeApi = nativeApi;
      this.srcs = srcs;
      this.libs = libs;
      this.options = options;
    }

    public ArrayB execute() throws IOException {
      if (compiler == null) {
        nativeApi.log().error("Couldn't find JavaCompiler implementation. "
            + "You have to run Smooth tool using JDK (not JVM). Only JDK contains java compiler.");
        return null;
      }
      return compile(srcs);
    }

    public ArrayB compile(ArrayB files) throws IOException {
      // prepare args for compilation

      var additionalCompilerOutput = new StringWriter();
      var diagnostic = new LoggingDiagnosticListener(nativeApi);
      var options = options();
      var standardJFM = compiler.getStandardFileManager(diagnostic, null, defaultCharset());
      var libsClasses = classesFromJarFiles(nativeApi, libs);
      if (libsClasses == null) {
        return null;
      }
      try (var sandboxedJFM = new SandboxedJavaFileManager(standardJFM, nativeApi, libsClasses)) {
        Iterable<InputSourceFile> inputSourceFiles = toJavaFiles(files.elems(TupleB.class));

        /*
         * Java compiler fails miserably when there's no java files.
         */
        if (!inputSourceFiles.iterator().hasNext()) {
          nativeApi.log().warning("Param 'srcs' is empty list.");
          return nativeApi.factory().arrayBuilderWithElems(nativeApi.factory().fileT()).build();
        }

        // run compilation task
        var compilationTask = compiler.getTask(
            additionalCompilerOutput, sandboxedJFM, diagnostic, options, null, inputSourceFiles);
        boolean success = compilationTask.call();

        // tidy up
        if (!success && !diagnostic.errorReported()) {
          nativeApi.log().error(
              "Internal error: Compilation failed but JavaCompiler reported no error message.");
        }
        String additionalInfo = additionalCompilerOutput.toString();
        if (!additionalInfo.isEmpty()) {
          nativeApi.log().warning(additionalInfo);
        }
        if (success) {
          return sandboxedJFM.resultClassfiles();
        } else {
          return null;
        }
      } catch (ZipException e) {
        nativeApi.log().error(
            "Cannot read archive. Corrupted data? Internal message: " + e.getMessage());
        return null;
      }
    }

    private List<String> options() {
      return map(options.elems(StringB.class), StringB::toJ);
    }

    private static Iterable<InputSourceFile> toJavaFiles(Iterable<TupleB> sourceFiles) {
      ArrayList<InputSourceFile> result = new ArrayList<>();
      for (TupleB file : sourceFiles) {
        result.add(new InputSourceFile(file));
      }
      return result;
    }
  }

  public static Iterable<InputClassFile> classesFromJarFiles(
      NativeApi nativeApi, ArrayB libraryJars) throws IOException {
    var filesMap = filesFromLibJars(nativeApi, libraryJars, isClassFilePredicate());
    return filesMap == null
        ? null
        : map(filesMap.entrySet(), e -> new InputClassFile(e.getValue(), e.getKey()));
  }
}
