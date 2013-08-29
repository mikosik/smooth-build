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
import org.smoothbuild.function.expr.Expression;
import org.smoothbuild.function.expr.ExpressionIdFactory;
import org.smoothbuild.parse.ModuleParser;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.exc.FunctionException;
import org.smoothbuild.problem.DetectingErrorsProblemsListener;
import org.smoothbuild.problem.ProblemsListener;
import org.smoothbuild.run.err.FunctionError;
import org.smoothbuild.run.err.ScriptFileNotFoundError;
import org.smoothbuild.run.err.UnknownFunctionError;

import com.google.common.collect.ImmutableMap;

public class SmoothRunner {
  private final DetectingErrorsProblemsListener problems;
  private final CommandLineParser commandLineParser;
  private final FileSystem fileSystem;
  private final ModuleParser moduleParser;

  @Inject
  public SmoothRunner(ProblemsListener problemsListener, CommandLineParser commandLineParser,
      FileSystem fileSystem, ModuleParser moduleParser) {
    this.problems = new DetectingErrorsProblemsListener(problemsListener);
    this.commandLineParser = commandLineParser;
    this.fileSystem = fileSystem;
    this.moduleParser = moduleParser;
  }

  public void run(String... commandLine) {
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

    try {
      function.apply(new ExpressionIdFactory(), ImmutableMap.<String, Expression> of()).calculate();
    } catch (FunctionException e) {
      problems.report(new FunctionError(e));
      return;
    }
  }

  private InputStream scriptInputStream(ProblemsListener problems, Path scriptFile) {
    try {
      return fileSystem.createInputStream(scriptFile);
    } catch (NoSuchFileException e) {
      problems.report(new ScriptFileNotFoundError(scriptFile));
      return null;
    }
  }
}
