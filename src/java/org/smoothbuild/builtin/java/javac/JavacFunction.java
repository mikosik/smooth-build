package org.smoothbuild.builtin.java.javac;

import static java.nio.charset.Charset.defaultCharset;
import static org.smoothbuild.util.Empty.nullToEmpty;

import java.io.IOException;
import java.io.StringWriter;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.smoothbuild.builtin.java.javac.err.AdditionalCompilerInfo;
import org.smoothbuild.builtin.java.javac.err.CompilerFailedWithoutDiagnosticsError;
import org.smoothbuild.builtin.java.javac.err.NoCompilerAvailableError;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.FileSet;
import org.smoothbuild.plugin.api.Required;
import org.smoothbuild.plugin.api.SmoothFunction;
import org.smoothbuild.task.SandboxImpl;
import org.smoothbuild.task.err.FileSystemError;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Multimap;

public class JavacFunction {

  public interface Parameters {
    @Required
    FileSet sources();

    FileSet libs();
  }

  @SmoothFunction("javac")
  public static FileSet execute(SandboxImpl sandbox, Parameters params) {
    return new Worker(sandbox, params).execute();
  }

  private static class Worker {
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
        throw new NoCompilerAvailableError();
      }
      return compile(params.sources());
    }

    public FileSet compile(Iterable<File> files) {
      // prepare arguments for compilation

      StringWriter additionalCompilerOutput = new StringWriter();
      ReportingDiagnosticListener diagnostic = new ReportingDiagnosticListener(sandbox);
      SandboxedJavaFileManager fileManager = fileManager(diagnostic);

      try {
        // add handling of various compiler options 'source', 'target', etc
        Iterable<String> options = null;
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
