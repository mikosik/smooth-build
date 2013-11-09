package org.smoothbuild.command;

import static org.smoothbuild.command.SmoothContants.DEFAULT_SCRIPT;
import static org.smoothbuild.function.base.Name.isLegalName;
import static org.smoothbuild.function.base.Name.name;

import java.util.List;

import org.smoothbuild.command.err.IllegalFunctionNameError;
import org.smoothbuild.command.err.NothingToDoError;
import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.listen.ErrorMessageException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class CommandLineParser {
  public CommandLineArguments parse(List<String> args) {
    if (args.size() == 0) {
      throw new ErrorMessageException(new NothingToDoError());
    }

    return new CommandLineArguments(DEFAULT_SCRIPT, names(args));
  }

  private static ImmutableList<Name> names(List<String> args) {
    Builder<Name> builder = ImmutableList.builder();

    for (String nameString : args) {
      if (!isLegalName(nameString)) {
        throw new ErrorMessageException(new IllegalFunctionNameError(nameString));
      }
      builder.add(name(nameString));
    }
    return builder.build();
  }
}
