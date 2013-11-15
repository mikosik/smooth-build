package org.smoothbuild.command;

import static org.smoothbuild.command.SmoothContants.DEFAULT_SCRIPT;
import static org.smoothbuild.lang.function.base.Name.isLegalName;
import static org.smoothbuild.lang.function.base.Name.name;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.command.err.DuplicatedFunctionNameWarning;
import org.smoothbuild.command.err.IllegalFunctionNameError;
import org.smoothbuild.command.err.NothingToDoError;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.message.listen.MessageGroup;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Sets;

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
    if (args.size() == 0) {
      throw new ErrorMessageException(new NothingToDoError());
    }

    return new CommandLineArguments(DEFAULT_SCRIPT, names(args));
  }

  private ImmutableList<Name> names(List<String> args) {
    Set<Name> names = Sets.newHashSet();
    Set<Name> duplicated = Sets.newHashSet();

    Builder<Name> builder = ImmutableList.builder();
    for (String nameString : args) {
      if (isLegalName(nameString)) {
        Name name = name(nameString);
        if (names.contains(name)) {
          duplicated.add(name);
        } else {
          builder.add(name);
          names.add(name);
        }
      } else {
        messages.report(new IllegalFunctionNameError(nameString));
      }
    }

    for (Name name : duplicated) {
      messages.report(new DuplicatedFunctionNameWarning(name));
    }

    return builder.build();
  }
}
