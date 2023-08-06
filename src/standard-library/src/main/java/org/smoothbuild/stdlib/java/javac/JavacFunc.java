package org.smoothbuild.stdlib.java.javac;

import static java.nio.charset.Charset.defaultCharset;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.stdlib.compress.UnzipHelper.filesFromLibJars;
import static org.smoothbuild.stdlib.java.util.JavaNaming.isClassFilePredicate;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipException;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.StringB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class JavacFunc {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws BytecodeException {
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

    public Worker(NativeApi nativeApi, ArrayB srcs, ArrayB libs, ArrayB options) {
      this.compiler = ToolProvider.getSystemJavaCompiler();
      this.nativeApi = nativeApi;
      this.srcs = srcs;
      this.libs = libs;
      this.options = options;
    }

    public ArrayB execute() throws BytecodeException {
      if (compiler == null) {
        nativeApi
            .log()
            .error(
                "Couldn't find JavaCompiler implementation. "
                    + "You have to run Smooth tool using JDK (not JVM). Only JDK contains java compiler.");
        return null;
      }
      return compile(srcs);
    }

    public ArrayB compile(ArrayB files) throws BytecodeException {
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
          return nativeApi
              .factory()
              .arrayBuilderWithElems(nativeApi.factory().fileT())
              .build();
        }

        // run compilation task
        var compilationTask = compiler.getTask(
            additionalCompilerOutput, sandboxedJFM, diagnostic, options, null, inputSourceFiles);
        boolean success = compilationTask.call();

        // tidy up
        if (!success && !diagnostic.errorReported()) {
          nativeApi
              .log()
              .error(
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
        var message = "Cannot read archive. Corrupted data? Internal message: " + e.getMessage();
        nativeApi.log().error(message);
        return null;
      } catch (IOException e) {
        nativeApi.log().error("IOException: " + e.getMessage());
        return null;
      }
    }

    private List<String> options() throws BytecodeException {
      return options.elems(StringB.class).map(StringB::toJ);
    }

    private static Iterable<InputSourceFile> toJavaFiles(Iterable<TupleB> sourceFiles)
        throws BytecodeException {
      ArrayList<InputSourceFile> result = new ArrayList<>();
      for (TupleB file : sourceFiles) {
        result.add(new InputSourceFile(file));
      }
      return result;
    }
  }

  public static Iterable<InputClassFile> classesFromJarFiles(
      NativeApi nativeApi, ArrayB libraryJars) throws BytecodeException {
    var filesMap = filesFromLibJars(nativeApi, libraryJars, isClassFilePredicate());
    return filesMap == null
        ? null
        : listOfAll(filesMap.entrySet()).map(e -> new InputClassFile(e.getValue(), e.getKey()));
  }
}
