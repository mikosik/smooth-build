package org.smoothbuild.parse.def.err;

import static org.smoothbuild.function.base.Type.VOID;

import org.smoothbuild.message.CodeError;
import org.smoothbuild.parse.def.Argument;

public class VoidArgError extends CodeError {
  public VoidArgError(Argument argument) {
    super(argument.codeLocation(), "Arguments with " + VOID.name() + " type are not allowed.");
  }
}
