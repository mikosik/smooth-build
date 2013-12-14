package org.smoothbuild.command;

import static org.smoothbuild.command.SmoothContants.DEFAULT_SCRIPT;
import static org.smoothbuild.lang.function.base.Name.isLegalName;
import static org.smoothbuild.lang.function.base.Name.name;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.command.err.DuplicatedFunctionNameWarning;
import org.smoothbuild.command.err.IllegalFunctionNameError;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.util.DuplicatesDetector;

public class CommandLineParser {
  private final MessageGroup messages;

  @Inject
  public CommandLineParser(CommandLineParserMessages messages) {
    this((MessageGroup) messages);
  }

  public CommandLineParser(MessageGroup messages) {
    this.messages = messages;
  }

  public CommandLineArguments parse(List<String> args) {
    return new CommandLineArguments(DEFAULT_SCRIPT, names(args));
  }

  private Set<Name> names(List<String> args) {
    DuplicatesDetector<Name> duplicatesDetector = new DuplicatesDetector<Name>();

    for (String nameString : args) {
      if (isLegalName(nameString)) {
        duplicatesDetector.addValue(name(nameString));
      } else {
        messages.report(new IllegalFunctionNameError(nameString));
      }
    }

    for (Name name : duplicatesDetector.getDuplicateValues()) {
      messages.report(new DuplicatedFunctionNameWarning(name));
    }

    return duplicatesDetector.getUniqueValues();
  }
}
