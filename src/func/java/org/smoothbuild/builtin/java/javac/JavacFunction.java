package org.smoothbuild.builtin.java.javac;

import static java.nio.charset.Charset.defaultCharset;
import static org.smoothbuild.util.Empty.nullToEmpty;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.smoothbuild.builtin.java.javac.err.AdditionalCompilerInfo;
import org.smoothbuild.builtin.java.javac.err.CompilerFailedWithoutDiagnosticsError;
import org.smoothbuild.builtin.java.javac.err.IllegalSourceParamError;
import org.smoothbuild.builtin.java.javac.err.IllegalTargetParamError;
import org.smoothbuild.builtin.java.javac.err.NoCompilerAvailableError;
import org.smoothbuild.builtin.java.javac.err.NoJavaSourceFilesFoundWarning;
import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class JavacFunction {

  public interface JavacParameters {
    @Required
    Array<SFile> sources();

    Array<Blob> libs();

    SString source();

    SString target();
  }

  @SmoothFunction
  public static Array<SFile> javac(NativeApi nativeApi, JavacParameters params) {
    return new Worker(nativeApi, params).execute();
  }

  private static class Worker {
    private static final ImmutableSet<String> SOURCE_VALUES = ImmutableSet.of("1.3", "1.4", "1.5",
        "5", "1.6", "6", "1.7", "7");
    private static final ImmutableSet<String> TARGET_VALUES = ImmutableSet.of("1.1", "1.2", "1.3",
        "1.4", "1.5", "5", "1.6", "6", "1.7", "7");

    private final JavaCompiler compiler;
    private final NativeApi nativeApi;
    private final JavacParameters params;

    public Worker(NativeApi nativeApi, JavacParameters params) {
      this.compiler = ToolProvider.getSystemJavaCompiler();
      this.nativeApi = nativeApi;
      this.params = params;
    }

    public Array<SFile> execute() {
      if (compiler == null) {
        throw new NoCompilerAvailableError();
      }
      return compile(params.sources());
    }

    public Array<SFile> compile(Array<SFile> files) {
      // prepare arguments for compilation

      StringWriter additionalCompilerOutput = new StringWriter();
      LoggingDiagnosticListener diagnostic = new LoggingDiagnosticListener(nativeApi);
      Iterable<String> options = options();
      SandboxedJavaFileManager fileManager = fileManager(diagnostic);

      try {
        Iterable<InputSourceFile> inputSourceFiles = toJavaFiles(files);

        /*
         * Java compiler fails miserably when there's no java files.
         */
        if (Iterables.isEmpty(inputSourceFiles)) {
          nativeApi.log(new NoJavaSourceFilesFoundWarning());
          return nativeApi.arrayBuilder(SFile.class).build();
        }

        // run compilation task
        CompilationTask task =
            compiler.getTask(additionalCompilerOutput, fileManager, diagnostic, options, null,
                inputSourceFiles);
        boolean success = task.call();

        // tidy up
        if (!success && !diagnostic.errorReported()) {
          nativeApi.log(new CompilerFailedWithoutDiagnosticsError());
        }
        String additionalInfo = additionalCompilerOutput.toString();
        if (!additionalInfo.isEmpty()) {
          nativeApi.log(new AdditionalCompilerInfo(additionalInfo));
        }
        return fileManager.resultClassfiles();
      } finally {
        try {
          fileManager.close();
        } catch (IOException e) {
          nativeApi.log(new FileSystemError(e));
        }
      }
    }

    private Iterable<String> options() {
      List<String> result = Lists.newArrayList();

      if (params.source() != null) {
        String sourceArg = params.source().value();
        if (!SOURCE_VALUES.contains(sourceArg)) {
          throw new IllegalSourceParamError(sourceArg, SOURCE_VALUES);
        }
        result.add("-source");
        result.add(sourceArg);
      }

      if (params.target() != null) {
        String targetArg = params.target().value();
        if (!TARGET_VALUES.contains(targetArg)) {
          throw new IllegalTargetParamError(targetArg, TARGET_VALUES);
        }
        result.add("-target");
        result.add(targetArg);
      }

      return result;
    }

    private SandboxedJavaFileManager fileManager(LoggingDiagnosticListener diagnostic) {
      StandardJavaFileManager fileManager =
          compiler.getStandardFileManager(diagnostic, null, defaultCharset());
      Multimap<String, JavaFileObject> libsClasses =
          PackagedJavaFileObjects.packagedJavaFileObjects(nativeApi, nullToEmpty(params.libs()));
      return new SandboxedJavaFileManager(fileManager, nativeApi, libsClasses);
    }

    private static Iterable<InputSourceFile> toJavaFiles(Iterable<SFile> sourceFiles) {
      return FluentIterable.from(sourceFiles).transform(new Function<SFile, InputSourceFile>() {
        @Override
        public InputSourceFile apply(SFile file) {
          return new InputSourceFile(file);
        }
      });
    }
  }
}
