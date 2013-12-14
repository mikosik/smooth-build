package org.smoothbuild.command;

import static org.smoothbuild.command.SmoothContants.DEFAULT_SCRIPT;
import static org.smoothbuild.lang.function.base.Name.isLegalName;
import static org.smoothbuild.lang.function.base.Name.name;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.command.err.DuplicatedFunctionNameWarning;
import org.smoothbuild.command.err.IllegalFunctionNameError;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.util.DuplicatesDetector;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

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

  private ImmutableList<Name> names(List<String> args) {
    DuplicatesDetector<Name> duplicatesDetector = new DuplicatesDetector<Name>();

    Builder<Name> builder = ImmutableList.builder();
    for (String nameString : args) {
      if (isLegalName(nameString)) {
        Name name = name(nameString);
        if (!duplicatesDetector.addValue(name)) {
          builder.add(name);
        }
      } else {
        messages.report(new IllegalFunctionNameError(nameString));
      }
    }

    for (Name name : duplicatesDetector.getDuplicateValues()) {
      messages.report(new DuplicatedFunctionNameWarning(name));
    }

    return builder.build();
  }
}
