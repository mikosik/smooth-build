package org.smoothbuild.lang.function.nativ.err;

import static org.smoothbuild.lang.message.MessageType.ERROR;

import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.message.Message;

public class NullResultError extends Message {
  public NullResultError(NativeFunction function) {
    super(ERROR, "Native function " + function.name()
        + " has faulty implementation: it returned 'null' but logged no error.");
  }
}
