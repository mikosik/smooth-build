package org.smoothbuild.run;

import static org.smoothbuild.out.log.Level.ERROR;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.smoothbuild.common.bindings.Bindings;
import org.smoothbuild.compile.fs.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.fs.lang.define.NamedValueS;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.report.Reporter;

import com.google.common.collect.ImmutableList;

public class FindTopValues {
  public static Optional<ImmutableList<NamedValueS>> findTopValues(
      Reporter reporter, Bindings<NamedEvaluableS> evaluables, List<String> names) {
    var matchingTopEvaluables = new HashSet<NamedValueS>();
    var logs = new LogBuffer();
    for (String name : names) {
      var topEvaluable = evaluables.getOptional(name);
      if (topEvaluable.isPresent()) {
        if (topEvaluable.get() instanceof NamedValueS namedValue) {
          if (namedValue.schema().quantifiedVars().isEmpty()) {
            matchingTopEvaluables.add(namedValue);
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
