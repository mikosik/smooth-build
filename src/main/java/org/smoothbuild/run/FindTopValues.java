package org.smoothbuild.run;

import static org.smoothbuild.out.log.Level.ERROR;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.smoothbuild.compile.lang.define.DefsS;
import org.smoothbuild.compile.lang.define.PolyValS;
import org.smoothbuild.compile.lang.define.ValS;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.report.Reporter;

import com.google.common.collect.ImmutableList;

public class FindTopValues {
  public static Optional<List<ValS>> findTopValues(
      Reporter reporter, DefsS defs, List<String> names) {
    var topEvaluables = defs.evaluables();
    var matchingTopEvaluables = new HashSet<ValS>();
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
    if (logs.containsAtLeast(ERROR)) {
      return Optional.empty();
    } else {
      return Optional.of(ImmutableList.copyOf(matchingTopEvaluables));
    }
  }
}
