package org.smoothbuild.parse;

import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.module.Module;
import org.smoothbuild.message.listen.MessageCatchingExecutor;
import org.smoothbuild.message.listen.UserConsole;

public class ModuleParserPhase extends MessageCatchingExecutor<Set<Name>, Module> {
  private final ModuleParser moduleParser;

  @Inject
  public ModuleParserPhase(UserConsole userConsole, ModuleParserMessages messages,
      ModuleParser moduleParser) {
    super(userConsole, "SCRIPT PARSER", messages);
    this.moduleParser = moduleParser;
  }

  @Override
  public Module executeImpl(Set<Name> arguments) {
    return moduleParser.createModule();
  }
}
