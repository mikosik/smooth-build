package org.smoothbuild.cli;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.lang.base.Name.isLegalName;
import static org.smoothbuild.util.Maybe.maybe;
import static org.smoothbuild.util.Maybe.value;

import java.util.List;
import java.util.Set;

import org.smoothbuild.util.DuplicatesDetector;
import org.smoothbuild.util.Maybe;

import com.google.common.collect.ImmutableList;

public class ArgumentValidator {
  public static Maybe<Set<String>> validateFunctionNames(List<String> args) {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    args.forEach(duplicatesDetector::addValue);
    return value(duplicatesDetector.getUniqueValues())
        .map(as -> maybe(as, illegalFunctionNameErrors(args)))
        .map(as -> maybe(as, duplicateFunctionNameErrors(duplicatesDetector)));
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
    return duplicatesDetector.getDuplicateValues().stream().map(
        name -> "Function '" + name + "' has been specified more than once.")
        .collect(toImmutableList());
  }
}
