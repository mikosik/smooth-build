package org.smoothbuild.run;

import static org.smoothbuild.lang.base.Loc.commandLineLoc;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.lang.define.ValS;
import org.smoothbuild.lang.obj.ObjRefS;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.report.Reporter;

import com.google.common.collect.ImmutableList;

public class FindTopObjS {
  public static Optional<List<ObjRefS>> findTopObjS(
      Reporter reporter, DefsS defs, List<String> names) {
    var topRefables = defs.topRefables();
    var topRefs = new HashSet<ObjRefS>();
    var logs = new LogBuffer();
    for (String name : names) {
      var topRefable = topRefables.get(name);
      if (topRefable != null) {
        if (topRefable instanceof ValS value) {
          topRefs.add(new ObjRefS(value.type(), value.name(), commandLineLoc()));
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
