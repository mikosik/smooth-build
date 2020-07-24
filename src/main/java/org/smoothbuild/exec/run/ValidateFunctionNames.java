package org.smoothbuild.exec.run;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.lang.base.Name.isLegalName;
import static org.smoothbuild.util.Sets.map;

import java.util.List;

import org.smoothbuild.util.DuplicatesDetector;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class ValidateFunctionNames {
  public static List<String> validateFunctionNames(List<String> args) {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    args.forEach(duplicatesDetector::addValue);
    return ImmutableList.<String>builder()
        .addAll(illegalFunctionNameErrors(args))
        .addAll(duplicateFunctionNameErrors(duplicatesDetector))
        .build();
  }

  private static ImmutableList<String> illegalFunctionNameErrors(List<String> args) {
    return args
        .stream()
        .filter(a -> !isLegalName(a))
        .map(a -> "Illegal function name '" + a + "' passed in command line.")
        .collect(toImmutableList());
  }

  private static ImmutableSet<String> duplicateFunctionNameErrors(
      DuplicatesDetector<String> duplicatesDetector) {
    return map(
        duplicatesDetector.getDuplicateValues(),
        name -> "Function '" + name + "' has been specified more than once.");
  }
}
