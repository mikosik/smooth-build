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
import org.smoothbuild.fs.base.exc.FileSystemError;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.plugin.api.Required;
import org.smoothbuild.plugin.api.SmoothFunction;
import org.smoothbuild.task.exec.SandboxImpl;
import org.smoothbuild.type.api.File;
import org.smoothbuild.type.api.FileSet;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class JavacFunction {

  public interface Parameters {
    @Required
    FileSet sources();

    FileSet libs();

    String source();

    String target();
  }

  @SmoothFunction("javac")
  public static FileSet execute(SandboxImpl sandbox, Parameters params) {
    return new Worker(sandbox, params).execute();
  }

  private static class Worker {
    private static final ImmutableSet<String> SOURCE_VALUES = ImmutableSet.of("1.3", "1.4", "1.5",
        "5", "1.6", "6", "1.7", "7");
    private static final ImmutableSet<String> TARGET_VALUES = ImmutableSet.of("1.1", "1.2", "1.3",
        "1.4", "1.5", "5", "1.6", "6", "1.7", "7");

    private final JavaCompiler compiler;
    private final SandboxImpl sandbox;
    private final Parameters params;

    public Worker(SandboxImpl sandbox, Parameters params) {
      this.compiler = ToolProvider.getSystemJavaCompiler();
      this.sandbox = sandbox;
      this.params = params;
    }

    public FileSet execute() {
      if (compiler == null) {
        throw new ErrorMessageException(new NoCompilerAvailableError());
      }
      return compile(params.sources());
    }

    public FileSet compile(Iterable<File> files) {
      // prepare arguments for compilation

      StringWriter additionalCompilerOutput = new StringWriter();
      ReportingDiagnosticListener diagnostic = new ReportingDiagnosticListener(sandbox);
      Iterable<String> options = options();
      SandboxedJavaFileManager fileManager = fileManager(diagnostic);

      try {
        Iterable<InputSourceFile> inputSourceFiles = toJavaFiles(files);

        // run compilation task
        CompilationTask task = compiler.getTask(additionalCompilerOutput, fileManager, diagnostic,
            options, null, inputSourceFiles);
        boolean success = task.call();

        // tidy up
        if (!success && !diagnostic.errorReported()) {
          sandbox.report(new CompilerFailedWithoutDiagnosticsError());
        }
        String additionalInfo = additionalCompilerOutput.toString();
        if (!additionalInfo.isEmpty()) {
          sandbox.report(new AdditionalCompilerInfo(additionalInfo));
        }
        return fileManager.resultClassfiles();
      } finally {
        try {
          fileManager.close();
        } catch (IOException e) {
          sandbox.report(new FileSystemError(e));
        }
      }
    }

    private Iterable<String> options() {
      List<String> result = Lists.newArrayList();

      String sourceArg = params.source();
      if (sourceArg != null) {
        if (!SOURCE_VALUES.contains(sourceArg)) {
          Message error = new IllegalSourceParamError(sourceArg, SOURCE_VALUES);
          throw new ErrorMessageException(error);
        }
        result.add("-source");
        result.add(sourceArg);
      }

      String targetArg = params.target();
      if (targetArg != null) {
        if (!TARGET_VALUES.contains(targetArg)) {
          Message error = new IllegalTargetParamError(targetArg, TARGET_VALUES);
          throw new ErrorMessageException(error);
        }
        result.add("-target");
        result.add(targetArg);
      }

      return result;
    }

    private SandboxedJavaFileManager fileManager(ReportingDiagnosticListener diagnostic) {
      StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostic, null,
          defaultCharset());
      Multimap<String, JavaFileObject> libsClasses = PackagedJavaFileObjects
          .packagedJavaFileObjects(nullToEmpty(params.libs()));
      return new SandboxedJavaFileManager(fileManager, sandbox, libsClasses);
    }

    private static Iterable<InputSourceFile> toJavaFiles(Iterable<File> sourceFiles) {
      return FluentIterable.from(sourceFiles).transform(new Function<File, InputSourceFile>() {
        @Override
        public InputSourceFile apply(File file) {
          return new InputSourceFile(file);
        }
      });
    }
  }
}
