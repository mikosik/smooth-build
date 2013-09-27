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
import org.smoothbuild.message.listen.DetectingErrorsMessageListener;
import org.smoothbuild.message.listen.MessageListener;
import org.smoothbuild.message.message.Info;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.parse.ModuleParser;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.run.err.ScriptFileNotFoundError;
import org.smoothbuild.run.err.UnknownFunctionError;
import org.smoothbuild.task.TaskExecutor;
import org.smoothbuild.util.Empty;

public class SmoothRunner {
  private final DetectingErrorsMessageListener messages;
  private final Cleaner cleaner;
  private final CommandLineParser commandLineParser;
  private final FileSystem fileSystem;
  private final ModuleParser moduleParser;
  private final TaskExecutor taskExecutor;

  @Inject
  public SmoothRunner(MessageListener messageListener, Cleaner cleaner,
      CommandLineParser commandLineParser, FileSystem fileSystem, ModuleParser moduleParser,
      TaskExecutor taskExecutor) {
    this.taskExecutor = taskExecutor;
    this.messages = new DetectingErrorsMessageListener(messageListener);
    this.cleaner = cleaner;
    this.commandLineParser = commandLineParser;
    this.fileSystem = fileSystem;
    this.moduleParser = moduleParser;
  }

  public void run(String... commandLine) {
    cleaner.clearBuildDir();

    try {
      CommandLineArguments args = commandLineParser.parse(commandLine);
      Path scriptFile = args.scriptFile();

      InputStream inputStream = scriptInputStream(messages, scriptFile);

      Module module = moduleParser.createModule(messages, inputStream, scriptFile);
      if (messages.errorDetected()) {
        return;
      }

      Name name = args.functionToRun();
      Function function = module.getFunction(name);
      if (function == null) {
        messages.report(new UnknownFunctionError(name, module.availableNames()));
        return;
      }

      taskExecutor.execute(messages, function.generateTask(Empty.stringTaskMap()));
    } catch (Message message) {
      messages.report(message);
    }

    if (messages.errorDetected()) {
      messages.report(new Info("BUILD FAILED"));
    } else {
      messages.report(new Info("SUCCESS"));
    }
  }

  private InputStream scriptInputStream(MessageListener messages, Path scriptFile) {
    try {
      return fileSystem.openInputStream(scriptFile);
    } catch (NoSuchFileException e) {
      throw new ScriptFileNotFoundError(scriptFile);
    }
  }
}
