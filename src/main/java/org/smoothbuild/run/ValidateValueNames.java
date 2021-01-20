package org.smoothbuild.run;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.lang.base.define.Names.isLegalName;
import static org.smoothbuild.util.Sets.map;

import java.util.List;

import org.smoothbuild.util.DuplicatesDetector;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class ValidateValueNames {
  public static List<String> validateValueNames(List<String> args) {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    args.forEach(duplicatesDetector::addValue);
    return ImmutableList.<String>builder()
        .addAll(illegalValueNameErrors(args))
        .addAll(duplicateValueNameErrors(duplicatesDetector))
        .build();
  }

  private static ImmutableList<String> illegalValueNameErrors(List<String> args) {
    return args
        .stream()
        .filter(a -> !isLegalName(a))
        .map(a -> "Illegal value name `" + a + "` passed in command line.")
        .collect(toImmutableList());
  }

  private static ImmutableSet<String> duplicateValueNameErrors(
      DuplicatesDetector<String> duplicatesDetector) {
    return map(
        duplicatesDetector.getDuplicateValues(),
        name -> "Value `" + name + "` has been specified more than once.");
  }
}
