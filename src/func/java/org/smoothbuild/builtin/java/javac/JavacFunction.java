package org.smoothbuild.builtin.java.javac;

import static java.nio.charset.Charset.defaultCharset;
import static org.smoothbuild.builtin.java.javac.PackagedJavaFileObjects.classesFromJars;
import static org.smoothbuild.lang.message.MessageType.ERROR;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.smoothbuild.builtin.java.javac.err.AdditionalCompilerInfo;
import org.smoothbuild.builtin.java.javac.err.NoJavaSourceFilesFoundWarning;
import org.smoothbuild.io.fs.base.err.FileSystemException;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;

public class JavacFunction {
  @SmoothFunction
  public static Array<SFile> javac(
      Container container,
      @Required @Name("sources") Array<SFile> sources,
      @Name("libs") Array<Blob> libs,
      @Name("source") SString source,
      @Name("target") SString target) {
    return new Worker(container, sources, libs, source, target).execute();
  }

  private static class Worker {
    private static final Set<String> SOURCE_VALUES = unmodifiableSet("1.3", "1.4", "1.5", "5",
        "1.6", "6", "1.7", "7", "1.8", "8");
    private static final Set<String> TARGET_VALUES = unmodifiableSet("1.1", "1.2", "1.3", "1.4",
        "1.5", "5", "1.6", "6", "1.7", "7", "1.8", "8");

    private final JavaCompiler compiler;
    private final Container container;
    private final Array<SFile> sources;
    private final Array<Blob> libs;
    private final SString source;
    private final SString target;

    public Worker(Container container, //
        Array<SFile> sources, //
        Array<Blob> libs, //
        SString source, //
        SString target) {
      this.compiler = ToolProvider.getSystemJavaCompiler();
      this.container = container;
      this.sources = sources;
      this.libs = libs;
      this.source = source;
      this.target = target;
    }

    public Array<SFile> execute() {
      if (compiler == null) {
        throw new Message(ERROR, "Couldn't find JavaCompiler implementation. "
            + "You have to run Smooth tool using JDK (not JVM). Only JDK contains java compiler.");
      }
      return compile(sources);
    }

    public Array<SFile> compile(Array<SFile> files) {
      // prepare arguments for compilation

      StringWriter additionalCompilerOutput = new StringWriter();
      LoggingDiagnosticListener diagnostic = new LoggingDiagnosticListener(container);
      Iterable<String> options = options();
      SandboxedJavaFileManager fileManager = fileManager(diagnostic);

      try {
        Iterable<InputSourceFile> inputSourceFiles = toJavaFiles(files);

        /*
         * Java compiler fails miserably when there's no java files.
         */
        if (!inputSourceFiles.iterator().hasNext()) {
          container.log(new NoJavaSourceFilesFoundWarning());
          return container.create().arrayBuilder(SFile.class).build();
        }

        // run compilation task
        CompilationTask task =
            compiler.getTask(additionalCompilerOutput, fileManager, diagnostic, options, null,
                inputSourceFiles);
        boolean success = task.call();

        // tidy up
        if (!success && !diagnostic.errorReported()) {
          container.log(new Message(ERROR,
              "Internal error: Compilation failed but JavaCompiler reported no error message."));
        }
        String additionalInfo = additionalCompilerOutput.toString();
        if (!additionalInfo.isEmpty()) {
          container.log(new AdditionalCompilerInfo(additionalInfo));
        }
        return fileManager.resultClassfiles();
      } finally {
        try {
          fileManager.close();
        } catch (IOException e) {
          throw new FileSystemException(e);
        }
      }
    }

    private Iterable<String> options() {
      List<String> result = new ArrayList<>();

      if (!source.value().isEmpty()) {
        String sourceArg = source.value();
        if (!SOURCE_VALUES.contains(sourceArg)) {
          throw new Message(ERROR,
              "Parameter source has illegal value = '" + sourceArg + "'.\n"
                  + "Only following values are allowed " + SOURCE_VALUES + "\n");
        }
        result.add("-source");
        result.add(sourceArg);
      }

      if (!target.value().isEmpty()) {
        String targetArg = target.value();
        if (!TARGET_VALUES.contains(targetArg)) {
          throw new Message(ERROR, "Parameter target has illegal value = '" + targetArg + "'.\n"
              + "Only following values are allowed " + TARGET_VALUES);
        }
        result.add("-target");
        result.add(targetArg);
      }

      return result;
    }

    private SandboxedJavaFileManager fileManager(LoggingDiagnosticListener diagnostic) {
      StandardJavaFileManager fileManager =
          compiler.getStandardFileManager(diagnostic, null, defaultCharset());
      Iterable<InputClassFile> libsClasses = classesFromJars(container, libs);
      return new SandboxedJavaFileManager(fileManager, container, libsClasses);
    }

    private static Iterable<InputSourceFile> toJavaFiles(Iterable<SFile> sourceFiles) {
      ArrayList<InputSourceFile> result = new ArrayList<>();
      for (SFile file : sourceFiles) {
        result.add(new InputSourceFile(file));
      }
      return result;
    }
  }

  public static <T> Set<T> unmodifiableSet(T... elements) {
    Set<T> set = new HashSet<>(elements.length);
    Collections.addAll(set, elements);
    return Collections.unmodifiableSet(set);
  }
}
