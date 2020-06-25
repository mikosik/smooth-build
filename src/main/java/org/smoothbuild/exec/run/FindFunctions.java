package org.smoothbuild.exec.run;

import static org.smoothbuild.cli.console.Log.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.parse.Definitions;

public class FindFunctions {
  public static Optional<List<Function>> findFunctions(Reporter reporter, Definitions definitions,
      List<String> names) {
    var functions = definitions.functions();
    List<Function> functionsToRun = new ArrayList<>();
    List<Log> logs = new ArrayList<>();
    for (String name : names) {
      Function function = functions.get(name);
      if (function != null) {
        if (function.canBeCalledArgless()) {
          functionsToRun.add(function);
        } else {
          logs.add(error("Function '" + name
              + "' cannot be invoked from command line as it requires arguments."));
        }
      } else {
        logs.add(error("Unknown function '" + name + "'.\n"
            + "Try 'smooth list' to see all available functions."));
      }
    }
    reporter.report("Validating arguments", logs);
    if (logs.isEmpty()) {
      return Optional.of(functionsToRun);
    } else {
      return Optional.empty();
    }
  }
}
