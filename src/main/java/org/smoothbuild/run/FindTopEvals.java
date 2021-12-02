package org.smoothbuild.run;

import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.lang.base.define.Loc.commandLineLoc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.lang.base.define.DefsS;
import org.smoothbuild.lang.base.define.ValS;
import org.smoothbuild.lang.expr.RefS;

public class FindTopEvals {
  public static Optional<List<RefS>> findTopEvaluables(
      Reporter reporter, DefsS defs, List<String> names) {
    var topEvals = defs.topEvals();
    var refs = new ArrayList<RefS>();
    var logs = new ArrayList<Log>();
    for (String name : names) {
      var topEval = topEvals.get(name);
      if (topEval != null) {
        if (topEval instanceof ValS value) {
          refs.add(new RefS(value.type(), value.name(), commandLineLoc()));
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
      return Optional.of(refs);
    } else {
      return Optional.empty();
    }
  }
}
