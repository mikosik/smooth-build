package org.smoothbuild.cli.work.build.err;

import static org.smoothbuild.message.base.MessageType.WARNING;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.message.base.Message;

@SuppressWarnings("serial")
public class DuplicatedFunctionNameWarning extends Message {
  public DuplicatedFunctionNameWarning(Name name) {
    super(WARNING, "Function " + name + " has been specified more than once.");
  }
}
