package org.smoothbuild.lang.builtin.java.javac;

import static java.nio.charset.Charset.defaultCharset;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;
import static org.smoothbuild.util.Empty.nullToEmpty;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.smoothbuild.io.fs.base.exc.FileSystemError;
import org.smoothbuild.lang.builtin.java.javac.err.AdditionalCompilerInfo;
import org.smoothbuild.lang.builtin.java.javac.err.CompilerFailedWithoutDiagnosticsError;
import org.smoothbuild.lang.builtin.java.javac.err.IllegalSourceParamError;
import org.smoothbuild.lang.builtin.java.javac.err.IllegalTargetParamError;
import org.smoothbuild.lang.builtin.java.javac.err.NoCompilerAvailableError;
import org.smoothbuild.lang.builtin.java.javac.err.NoJavaSourceFilesFoundWarning;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.task.exec.PluginApiImpl;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class JavacFunction {

  public interface Parameters {
    @Required
    SArray<SFile> sources();

    SArray<SBlob> libs();

    SString source();

    SString target();
  }

  @SmoothFunction(name = "javac")
  public static SArray<SFile> execute(PluginApiImpl pluginApi, Parameters params) {
    return new Worker(pluginApi, params).execute();
  }

  private static class Worker {
    private static final ImmutableSet<String> SOURCE_VALUES = ImmutableSet.of("1.3", "1.4", "1.5",
        "5", "1.6", "6", "1.7", "7");
    private static final ImmutableSet<String> TARGET_VALUES = ImmutableSet.of("1.1", "1.2", "1.3",
        "1.4", "1.5", "5", "1.6", "6", "1.7", "7");

    private final JavaCompiler compiler;
    private final PluginApiImpl pluginApi;
    private final Parameters params;

    public Worker(PluginApiImpl pluginApi, Parameters params) {
      this.compiler = ToolProvider.getSystemJavaCompiler();
      this.pluginApi = pluginApi;
      this.params = params;
    }

    public SArray<SFile> execute() {
      if (compiler == null) {
        throw new ErrorMessageException(new NoCompilerAvailableError());
      }
      return compile(params.sources());
    }

    public SArray<SFile> compile(SArray<SFile> files) {
      // prepare arguments for compilation

      StringWriter additionalCompilerOutput = new StringWriter();
      ReportingDiagnosticListener diagnostic = new ReportingDiagnosticListener(pluginApi);
      Iterable<String> options = options();
      SandboxedJavaFileManager fileManager = fileManager(diagnostic);

      try {
        Iterable<InputSourceFile> inputSourceFiles = toJavaFiles(files);

        /*
         * Java compiler fails miserably when there's no java files.
         */
        if (Iterables.isEmpty(inputSourceFiles)) {
          pluginApi.log(new NoJavaSourceFilesFoundWarning());
          return pluginApi.arrayBuilder(FILE_ARRAY).build();
        }

        // run compilation task
        CompilationTask task =
            compiler.getTask(additionalCompilerOutput, fileManager, diagnostic, options, null,
                inputSourceFiles);
        boolean success = task.call();

        // tidy up
        if (!success && !diagnostic.errorReported()) {
          pluginApi.log(new CompilerFailedWithoutDiagnosticsError());
        }
        String additionalInfo = additionalCompilerOutput.toString();
        if (!additionalInfo.isEmpty()) {
          pluginApi.log(new AdditionalCompilerInfo(additionalInfo));
        }
        return fileManager.resultClassfiles();
      } finally {
        try {
          fileManager.close();
        } catch (IOException e) {
          pluginApi.log(new FileSystemError(e));
        }
      }
    }

    private Iterable<String> options() {
      List<String> result = Lists.newArrayList();

      if (params.source() != null) {
        String sourceArg = params.source().value();
        if (!SOURCE_VALUES.contains(sourceArg)) {
          Message error = new IllegalSourceParamError(sourceArg, SOURCE_VALUES);
          throw new ErrorMessageException(error);
        }
        result.add("-source");
        result.add(sourceArg);
      }

      if (params.target() != null) {
        String targetArg = params.target().value();
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
      StandardJavaFileManager fileManager =
          compiler.getStandardFileManager(diagnostic, null, defaultCharset());
      Multimap<String, JavaFileObject> libsClasses =
          PackagedJavaFileObjects.packagedJavaFileObjects(pluginApi, nullToEmpty(params.libs()));
      return new SandboxedJavaFileManager(fileManager, pluginApi, libsClasses);
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
