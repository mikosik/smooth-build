package org.smoothbuild.parse.def.err;

import org.smoothbuild.message.CodeError;
import org.smoothbuild.parse.def.Argument;

@SuppressWarnings("serial")
public class DuplicateArgNameError extends CodeError {
  public DuplicateArgNameError(Argument argument) {
    super(argument.codeLocation(), "Duplicated argument name = " + argument.name());
  }
}
