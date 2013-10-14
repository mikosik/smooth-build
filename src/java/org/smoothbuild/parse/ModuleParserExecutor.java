package org.smoothbuild.parse;

import javax.inject.Inject;

import org.smoothbuild.command.CommandLineArguments;
import org.smoothbuild.function.base.Module;
import org.smoothbuild.message.listen.MessageCatchingExecutor;
import org.smoothbuild.message.listen.UserConsole;

public class ModuleParserExecutor extends MessageCatchingExecutor<CommandLineArguments, Module> {
  private final ModuleParser moduleParser;

  @Inject
  public ModuleParserExecutor(UserConsole userConsole, ScriptParserMessageGroup messages,
      ModuleParser moduleParser) {
    super(userConsole, messages);
    this.moduleParser = moduleParser;
  }

  @Override
  public Module executeImpl(CommandLineArguments arguments) {
    return moduleParser.createModule(arguments);
  }
}
