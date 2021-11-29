package org.smoothbuild.run;

import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.lang.base.define.Loc.commandLineLoc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.lang.base.define.DefinitionsS;
import org.smoothbuild.lang.base.define.TopEvalS;
import org.smoothbuild.lang.base.define.ValS;
import org.smoothbuild.lang.expr.RefS;

public class FindTopEvals {
  public static Optional<List<RefS>> findTopEvaluables(
      Reporter reporter, DefinitionsS definitions, List<String> names) {
    var values = definitions.referencables();
    List<RefS> referencables = new ArrayList<>();
    List<Log> logs = new ArrayList<>();
    for (String name : names) {
      TopEvalS referencable = values.get(name);
      if (referencable != null) {
        if (referencable instanceof ValS value) {
          referencables.add(new RefS(value.type(), value.name(), commandLineLoc()));
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
