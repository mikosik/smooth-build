package org.smoothbuild.stdlib.java.javac;

import static java.nio.charset.Charset.defaultCharset;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.stdlib.file.FileHelper.fileArrayArrayToMap;

import java.io.IOException;
import java.io.StringWriter;
import java.util.zip.ZipException;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class JavacFunc {
  public static BValue func(NativeApi nativeApi, BTuple args) throws BytecodeException {
    BArray srcs = (BArray) args.get(0);
    BArray fileArrayArray = (BArray) args.get(1);
    BArray options = (BArray) args.get(2);

    return new Worker(nativeApi, srcs, fileArrayArray, options).execute();
  }

  private static class Worker {
    private final JavaCompiler compiler;
    private final NativeApi nativeApi;
    private final BArray srcs;
    private final BArray fileArrayArray;
    private final BArray options;

    public Worker(NativeApi nativeApi, BArray srcs, BArray fileArrayArray, BArray options) {
      this.compiler = ToolProvider.getSystemJavaCompiler();
      this.nativeApi = nativeApi;
      this.srcs = srcs;
      this.fileArrayArray = fileArrayArray;
      this.options = options;
    }

    public BArray execute() throws BytecodeException {
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

    public BArray compile(BArray files) throws BytecodeException {
      // prepare args for compilation

      var additionalCompilerOutput = new StringWriter();
      var diagnostic = new LoggingDiagnosticListener(nativeApi);
      var options = options();
      var standardJFM = compiler.getStandardFileManager(diagnostic, null, defaultCharset());
      var libsClasses = filesToInputClassFiles(nativeApi, fileArrayArray);
      if (libsClasses == null) {
        return null;
      }
      try (var sandboxedJFM = new SandboxedJavaFileManager(standardJFM, nativeApi, libsClasses)) {
        Iterable<InputSourceFile> inputSourceFiles =
            files.elements(BTuple.class).map(InputSourceFile::new);

        /*
         * Java compiler fails miserably when there's no java files.
         */
        if (!inputSourceFiles.iterator().hasNext()) {
          nativeApi.log().warning("Parameter 'srcs' is empty list.");
          return nativeApi
              .factory()
              .arrayBuilderWithElements(nativeApi.factory().fileType())
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
          return sandboxedJFM.resultClassFiles();
        } else {
          return null;
        }
      } catch (ZipException e) {
        var message = "Cannot read archive. Corrupted data? Internal message: " + e.getMessage();
        nativeApi.log().error(message);
        return null;
      } catch (IOException e) {
        nativeApi.log().fatal("IOException: " + e.getMessage());
        return null;
      }
    }

    private List<String> options() throws BytecodeException {
      return options.elements(BString.class).map(BString::toJavaString);
    }
  }

  public static Iterable<InputClassFile> filesToInputClassFiles(
      NativeApi nativeApi, BArray fileArrayArray) throws BytecodeException {
    var result = fileArrayArrayToMap(nativeApi, fileArrayArray);
    if (result == null) {
      return null;
    }
    return listOfAll(result.entrySet()).map(e -> new InputClassFile(e.getValue(), e.getKey()));
  }
}
