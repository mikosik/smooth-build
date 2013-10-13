package org.smoothbuild.app;

import static org.smoothbuild.message.message.MessageType.INFO;

import java.io.InputStream;

import javax.inject.Inject;

import org.smoothbuild.app.err.ScriptFileNotFoundError;
import org.smoothbuild.command.CommandLineArguments;
import org.smoothbuild.command.CommandLineParser;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.fs.base.exc.NoSuchFileException;
import org.smoothbuild.function.base.Module;
import org.smoothbuild.message.listen.DetectingErrorsMessageListener;
import org.smoothbuild.message.listen.MessageListener;
import org.smoothbuild.message.message.ErrorMessageException;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.parse.ModuleParser;
import org.smoothbuild.task.exec.SmoothExecutor;

public class SmoothApp {
  private final DetectingErrorsMessageListener messages;
  private final Cleaner cleaner;
  private final CommandLineParser commandLineParser;
  private final FileSystem fileSystem;
  private final ModuleParser moduleParser;
  private final SmoothExecutor smoothExecutor;

  @Inject
  public SmoothApp(MessageListener messageListener, Cleaner cleaner,
      CommandLineParser commandLineParser, FileSystem fileSystem, ModuleParser moduleParser,
      SmoothExecutor smoothExecutor) {
    this.messages = new DetectingErrorsMessageListener(messageListener);
    this.cleaner = cleaner;
    this.commandLineParser = commandLineParser;
    this.fileSystem = fileSystem;
    this.moduleParser = moduleParser;
    this.smoothExecutor = smoothExecutor;
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

      smoothExecutor.execute(args, module, messages);
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
