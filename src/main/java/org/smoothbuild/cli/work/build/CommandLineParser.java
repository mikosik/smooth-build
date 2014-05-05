package org.smoothbuild.cli.work.build;

import static org.smoothbuild.SmoothConstants.DEFAULT_SCRIPT;
import static org.smoothbuild.lang.function.base.Name.isLegalName;
import static org.smoothbuild.lang.function.base.Name.name;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.cli.work.build.err.DuplicatedFunctionNameWarning;
import org.smoothbuild.cli.work.build.err.IllegalFunctionNameError;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.message.listen.LoggedMessages;
import org.smoothbuild.util.DuplicatesDetector;

public class CommandLineParser {
  private final LoggedMessages messages;

  @Inject
  public CommandLineParser(CommandLineParserMessages messages) {
    this((LoggedMessages) messages);
  }

  public CommandLineParser(LoggedMessages messages) {
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
        messages.log(new IllegalFunctionNameError(nameString));
      }
    }

    for (Name name : duplicatesDetector.getDuplicateValues()) {
      messages.log(new DuplicatedFunctionNameWarning(name));
    }

    return duplicatesDetector.getUniqueValues();
  }
}
