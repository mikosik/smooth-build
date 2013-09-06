package org.smoothbuild.run;

import java.io.InputStream;

import javax.inject.Inject;

import org.smoothbuild.command.CommandLineArguments;
import org.smoothbuild.command.CommandLineParser;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.exc.NoSuchFileException;
import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Module;
import org.smoothbuild.function.base.Name;
import org.smoothbuild.parse.ModuleParser;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.problem.DetectingErrorsProblemsListener;
import org.smoothbuild.problem.ProblemsListener;
import org.smoothbuild.run.err.ScriptFileNotFoundError;
import org.smoothbuild.run.err.UnknownFunctionError;
import org.smoothbuild.task.TaskExecutor;
import org.smoothbuild.util.Empty;

public class SmoothRunner {
  private final DetectingErrorsProblemsListener problems;
  private final Cleaner cleaner;
  private final CommandLineParser commandLineParser;
  private final FileSystem fileSystem;
  private final ModuleParser moduleParser;
  private final TaskExecutor taskExecutor;

  @Inject
  public SmoothRunner(ProblemsListener problemsListener, Cleaner cleaner,
      CommandLineParser commandLineParser, FileSystem fileSystem, ModuleParser moduleParser,
      TaskExecutor taskExecutor) {
    this.taskExecutor = taskExecutor;
    this.problems = new DetectingErrorsProblemsListener(problemsListener);
    this.cleaner = cleaner;
    this.commandLineParser = commandLineParser;
    this.fileSystem = fileSystem;
    this.moduleParser = moduleParser;
  }

  public void run(String... commandLine) {
    cleaner.clearBuildDir();

    CommandLineArguments args = commandLineParser.parse(problems, commandLine);
    if (problems.errorDetected()) {
      return;
    }

    Path scriptFile = args.scriptFile();

    InputStream inputStream = scriptInputStream(problems, scriptFile);
    if (problems.errorDetected()) {
      return;
    }

    Module module = moduleParser.createModule(problems, inputStream, scriptFile);
    if (problems.errorDetected()) {
      return;
    }

    Name name = args.functionToRun();
    Function function = module.getFunction(name);
    if (function == null) {
      problems.report(new UnknownFunctionError(name, module.availableNames()));
      return;
    }

    taskExecutor.execute(problems, function.generateTask(Empty.stringTaskMap()));

  }

  private InputStream scriptInputStream(ProblemsListener problems, Path scriptFile) {
    try {
      return fileSystem.openInputStream(scriptFile);
    } catch (NoSuchFileException e) {
      problems.report(new ScriptFileNotFoundError(scriptFile));
      return null;
    }
  }
}
