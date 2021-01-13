package org.smoothbuild.run;

import static org.smoothbuild.cli.console.Log.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.lang.base.Declared;
import org.smoothbuild.lang.base.Definitions;
import org.smoothbuild.lang.base.Value;

public class FindValues {
  public static Optional<List<Value>> findValues(Reporter reporter, Definitions definitions,
      List<String> names) {
    var values = definitions.values();
    List<Value> callablesToRun = new ArrayList<>();
    List<Log> logs = new ArrayList<>();
    for (String name : names) {
      Declared declared = values.get(name);
      if (declared != null) {
        if (declared instanceof Value value) {
          callablesToRun.add(value);
        } else {
          logs.add(error(
              "`" + name + "` cannot be calculated as it is not a value but a function."));
        }
      } else {
        logs.add(error("Unknown value `" + name + "`.\n"
            + "Try 'smooth list' to see all available values that can be calculated."));
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
