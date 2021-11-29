package org.smoothbuild.run;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.lang.base.define.Names.isLegalName;
import static org.smoothbuild.util.collect.Sets.map;

import java.util.List;

import org.smoothbuild.util.collect.DuplicatesDetector;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class ValidateValNames {
  public static List<String> validateValNames(List<String> args) {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    args.forEach(duplicatesDetector::addValue);
    return ImmutableList.<String>builder()
        .addAll(illegalValNameErrors(args))
        .addAll(duplicateValNameErrors(duplicatesDetector))
        .build();
  }

  private static ImmutableList<String> illegalValNameErrors(List<String> args) {
    return args
        .stream()
        .filter(a -> !isLegalName(a))
        .map(a -> "Illegal value name `" + a + "` passed in command line.")
        .collect(toImmutableList());
  }

  private static ImmutableSet<String> duplicateValNameErrors(
      DuplicatesDetector<String> duplicatesDetector) {
    return map(
        duplicatesDetector.getDuplicateValues(),
        name -> "Value `" + name + "` has been specified more than once.");
  }
}
