package org.smoothbuild.cli;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.lang.base.Name.isLegalName;

import java.util.List;

import org.smoothbuild.util.DuplicatesDetector;

import com.google.common.collect.ImmutableList;

public class ArgumentValidator {
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

  private static ImmutableList<String> duplicateFunctionNameErrors(
      DuplicatesDetector<String> duplicatesDetector) {
    return duplicatesDetector.getDuplicateValues()
        .stream()
        .map(name -> "Function '" + name + "' has been specified more than once.")
        .collect(toImmutableList());
  }
}
