package org.smoothbuild.run;

import static org.smoothbuild.message.message.MessageType.INFO;

import java.io.InputStream;

import javax.inject.Inject;

import org.smoothbuild.command.CommandLineArguments;
import org.smoothbuild.command.CommandLineParser;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.fs.base.exc.NoSuchFileException;
import org.smoothbuild.function.base.Module;
import org.smoothbuild.function.base.Name;
import org.smoothbuild.function.def.DefinedFunction;
import org.smoothbuild.message.listen.DetectingErrorsMessageListener;
import org.smoothbuild.message.listen.MessageListener;
import org.smoothbuild.message.message.ErrorMessageException;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.parse.ModuleParser;
import org.smoothbuild.run.err.ScriptFileNotFoundError;
import org.smoothbuild.run.err.UnknownFunctionError;
import org.smoothbuild.task.exec.HashedTasks;
import org.smoothbuild.task.exec.TaskExecutor;
import org.smoothbuild.task.exec.TaskGenerator;

import com.google.common.hash.HashCode;

public class SmoothRunner {
  private final DetectingErrorsMessageListener messages;
  private final Cleaner cleaner;
  private final CommandLineParser commandLineParser;
  private final FileSystem fileSystem;
  private final ModuleParser moduleParser;
  private final TaskGenerator taskGenerator;
  private final TaskExecutor taskExecutor;

  @Inject
  public SmoothRunner(MessageListener messageListener, Cleaner cleaner,
      CommandLineParser commandLineParser, FileSystem fileSystem, ModuleParser moduleParser,
      TaskGenerator taskGenerator, TaskExecutor taskExecutor) {
    this.messages = new DetectingErrorsMessageListener(messageListener);
    this.cleaner = cleaner;
    this.commandLineParser = commandLineParser;
    this.fileSystem = fileSystem;
    this.moduleParser = moduleParser;
    this.taskGenerator = taskGenerator;
    this.taskExecutor = taskExecutor;
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
      DefinedFunction function = module.getFunction(name);
      if (function == null) {
        messages.report(new UnknownFunctionError(name, module.availableNames()));
        return;
      }

      HashCode hash = taskGenerator.generateTask(function);
      HashedTasks hashedTasks = new HashedTasks(taskGenerator.allTasks());
      taskExecutor.execute(messages, hashedTasks, hash);
    } catch (ErrorMessageException e) {
      messages.report(e.errorMessage());
    }

    if (messages.errorDetected()) {
      messages.report(new Message(INFO, "BUILD FAILED"));
    } else {
      messages.report(new Message(INFO, "SUCCESS"));
    }
  }

  private InputStream scriptInputStream(MessageListener messages, Path scriptFile) {
    try {
      return fileSystem.openInputStream(scriptFile);
    } catch (NoSuchFileException e) {
      throw new ErrorMessageException(new ScriptFileNotFoundError(scriptFile));
    }
  }
}
