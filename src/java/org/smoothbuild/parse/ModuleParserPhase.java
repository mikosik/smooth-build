package org.smoothbuild.parse;

import javax.inject.Inject;

import org.smoothbuild.command.CommandLineArguments;
import org.smoothbuild.lang.function.base.Module;
import org.smoothbuild.message.listen.MessageCatchingExecutor;
import org.smoothbuild.message.listen.UserConsole;

public class ModuleParserPhase extends MessageCatchingExecutor<CommandLineArguments, Module> {
  private final ModuleParser moduleParser;

  @Inject
  public ModuleParserPhase(UserConsole userConsole, ModuleParserMessages messages,
      ModuleParser moduleParser) {
    super(userConsole, messages);
    this.moduleParser = moduleParser;
  }

  @Override
  public Module executeImpl(CommandLineArguments arguments) {
    return moduleParser.createModule(arguments);
  }
}
