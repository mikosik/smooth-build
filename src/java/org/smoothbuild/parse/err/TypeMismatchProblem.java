package org.smoothbuild.parse.err;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.parse.Argument;
import org.smoothbuild.problem.Error;

public class TypeMismatchProblem extends Error {
  public TypeMismatchProblem(Argument argument, Type type) {
    super(argument.sourceLocation(), "Type mismatch, cannot convert from "
        + argument.definitionNode().type().name() + " to " + type.name());
  }
}
