package org.smoothbuild.exec.run;

import static org.smoothbuild.cli.console.Log.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.lang.base.Callable;
import org.smoothbuild.parse.Definitions;

public class FindCallables {
  public static Optional<List<Callable>> findCallables(Reporter reporter, Definitions definitions,
      List<String> names) {
    var callables = definitions.callables();
    List<Callable> callablesToRun = new ArrayList<>();
    List<Log> logs = new ArrayList<>();
    for (String name : names) {
      Callable callable = callables.get(name);
      if (callable != null) {
        if (callable.canBeCalledArgless()) {
          callablesToRun.add(callable);
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
      return Optional.of(callablesToRun);
    } else {
      return Optional.empty();
    }
  }
}
