package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class ReportError {
  @SmoothFunction
  public static SString reportError(Container container, SString message) {
    throw new ErrorMessage(message.value());
  }
}
