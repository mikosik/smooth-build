package org.smoothbuild.run;

import static org.smoothbuild.lang.base.define.Loc.commandLineLoc;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.smoothbuild.cli.console.LogBuffer;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.lang.base.define.DefsS;
import org.smoothbuild.lang.base.define.ValS;
import org.smoothbuild.lang.expr.TopRefS;

import com.google.common.collect.ImmutableList;

public class FindTopEvals {
  public static Optional<List<TopRefS>> findTopEvaluables(
      Reporter reporter, DefsS defs, List<String> names) {
    var topEvals = defs.topEvals();
    var topRefs = new HashSet<TopRefS>();
    var logs = new LogBuffer();
    for (String name : names) {
      var topEval = topEvals.get(name);
      if (topEval != null) {
        if (topEval instanceof ValS value) {
          topRefs.add(new TopRefS(value.type(), value.name(), commandLineLoc()));
        } else {
          logs.error(
              "`" + name + "` cannot be calculated as it is not a value but a function.");
        }
      } else {
        logs.error("Unknown value `" + name + "`.\n"
            + "Try 'smooth list' to see all available values that can be calculated.");
      }
    }
    reporter.report("command line arguments", logs.toList());
    if (logs.containsProblem()) {
      return Optional.empty();
    } else {
      return Optional.of(ImmutableList.copyOf(topRefs));
    }
  }
}
