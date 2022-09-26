package org.smoothbuild.run;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.smoothbuild.compile.lang.define.DefsS;
import org.smoothbuild.compile.lang.define.EvaluableS;
import org.smoothbuild.compile.lang.define.PolyValS;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.report.Reporter;

import com.google.common.collect.ImmutableList;

public class FindTopEvaluables {
  public static Optional<List<EvaluableS>> findTopEvaluables(
      Reporter reporter, DefsS defs, List<String> names) {
    var topEvaluables = defs.evaluables();
    var matchingTopEvaluables = new HashSet<EvaluableS>();
    var logs = new LogBuffer();
    for (String name : names) {
      var topEvaluable = topEvaluables.getOrNull(name);
      if (topEvaluable != null) {
        if (topEvaluable instanceof PolyValS polyValS) {
          if (polyValS.schema().quantifiedVars().isEmpty()) {
            matchingTopEvaluables.add(polyValS.mono());
          } else {
            logs.error("`" + name + "` cannot be calculated as it is a polymorphic value.");
          }
        } else {
          logs.error("`" + name + "` cannot be calculated as it is not a value but a function.");
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
      return Optional.of(ImmutableList.copyOf(matchingTopEvaluables));
    }
  }
}
