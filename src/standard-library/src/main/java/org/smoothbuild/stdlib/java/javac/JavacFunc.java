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
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class JavacFunc {
  public static BValue func(NativeApi nativeApi, BTuple args) throws BytecodeException {
    BArray srcs = (BArray) args.get(0);
    BArray libs = (BArray) args.get(1);
    BArray options = (BArray) args.get(2);

    return new Worker(nativeApi, srcs, libs, options).execute();
  }

  private static class Worker {
    private final JavaCompiler compiler;
    private final NativeApi nativeApi;
    private final BArray srcs;
    private final BArray libs;
    private final BArray options;

    public Worker(NativeApi nativeApi, BArray srcs, BArray libs, BArray options) {
      this.compiler = ToolProvider.getSystemJavaCompiler();
      this.nativeApi = nativeApi;
      this.srcs = srcs;
      this.libs = libs;
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
      var libsClasses = classesFromJarFiles(nativeApi, libs);
      if (libsClasses == null) {
        return null;
      }
      try (var sandboxedJFM = new SandboxedJavaFileManager(standardJFM, nativeApi, libsClasses)) {
        Iterable<InputSourceFile> inputSourceFiles = toJavaFiles(files.elements(BTuple.class));

        /*
         * Java compiler fails miserably when there's no java files.
         */
        if (!inputSourceFiles.iterator().hasNext()) {
          nativeApi.log().warning("Param 'srcs' is empty list.");
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
      return options.elements(BString.class).map(BString::toJavaString);
    }

    private static Iterable<InputSourceFile> toJavaFiles(Iterable<BTuple> sourceFiles)
        throws BytecodeException {
      ArrayList<InputSourceFile> result = new ArrayList<>();
      for (BTuple file : sourceFiles) {
        result.add(new InputSourceFile(file));
      }
      return result;
    }
  }

  public static Iterable<InputClassFile> classesFromJarFiles(
      NativeApi nativeApi, BArray libraryJars) throws BytecodeException {
    var filesMap = filesFromLibJars(nativeApi, libraryJars, isClassFilePredicate());
    return filesMap == null
        ? null
        : listOfAll(filesMap.entrySet()).map(e -> new InputClassFile(e.getValue(), e.getKey()));
  }
}
