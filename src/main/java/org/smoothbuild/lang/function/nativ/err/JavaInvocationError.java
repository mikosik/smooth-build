package org.smoothbuild.lang.function.nativ.err;

import static org.smoothbuild.lang.message.MessageType.ERROR;

import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.message.Message;

import com.google.common.base.Throwables;

public class JavaInvocationError extends Message {
  public JavaInvocationError(NativeFunction function, Throwable e) {
    super(ERROR, "Invoking function " + function.name() + " caused internal exception:\n"
        + Throwables.getStackTraceAsString(e));
  }
}
