package org.smoothbuild.exec.run;

import static org.smoothbuild.cli.console.Log.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.smoothbuild.cli.console.Console;
import org.smoothbuild.cli.console.Log;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.runtime.Functions;
import org.smoothbuild.lang.runtime.SRuntime;

public class ValidateFunctionArguments {
  public static List<Function> validateFunctionArguments(Console console, SRuntime runtime,
      Set<String> names) {
    Functions functions = runtime.functions();
    List<Function> functionsToRun = new ArrayList<>();
    List<Log> logs = new ArrayList<>();
    for (String name : names) {
      if (functions.contains(name)) {
        Function function = functions.get(name);
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
    console.show("Validating arguments", logs);
    return functionsToRun;
  }
}
