package org.smoothbuild.run;

import static org.smoothbuild.cli.console.Log.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.GlobalReferencable;
import org.smoothbuild.lang.base.define.ValueS;

public class FindReferencables {
  public static Optional<List<ValueS>> findReferencables(
      Reporter reporter, Definitions definitions, List<String> names) {
    var values = definitions.referencables();
    List<ValueS> referencables = new ArrayList<>();
    List<Log> logs = new ArrayList<>();
    for (String name : names) {
      GlobalReferencable referencable = values.get(name);
      if (referencable != null) {
        if (referencable instanceof ValueS value) {
          referencables.add(value);
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
      return Optional.of(referencables);
    } else {
      return Optional.empty();
    }
  }
}
