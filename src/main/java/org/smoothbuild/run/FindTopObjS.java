package org.smoothbuild.run;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.lang.define.MonoRefableS;
import org.smoothbuild.lang.define.PolyValS;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.report.Reporter;

import com.google.common.collect.ImmutableList;

public class FindTopObjS {
  public static Optional<List<MonoRefableS>> findTopObjS(
      Reporter reporter, DefsS defs, List<String> names) {
    var topRefables = defs.refables();
    var topRefs = new HashSet<MonoRefableS>();
    var logs = new LogBuffer();
    for (String name : names) {
      var topRefable = topRefables.getOrNull(name);
      if (topRefable != null) {
        if (topRefable instanceof PolyValS polyValS) {
          if (polyValS.schema().quantifiedVars().isEmpty()) {
            topRefs.add(polyValS.mono());
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
      return Optional.of(ImmutableList.copyOf(topRefs));
    }
  }
}
