package org.smoothbuild.parse.def.err;

import java.util.Set;

import org.smoothbuild.function.base.Param;
import org.smoothbuild.parse.def.Argument;
import org.smoothbuild.problem.CodeError;

public class AmbiguousNamelessArgsError extends CodeError {

  public AmbiguousNamelessArgsError(Set<Argument> availableArgs, Set<Param> availableParams) {
    // TODO Error messages have to
    // be more detailed. Ideally they should contain info about all
    // successful assignments of nameless arguments to param names and the
    // failed one.
    // Also case when availableParams is empty.

    // TODO fix code location
    super(availableArgs.iterator().next().codeLocation(),
        "Ambiguous nameless arguments to parameters assignment.");
  }
}
