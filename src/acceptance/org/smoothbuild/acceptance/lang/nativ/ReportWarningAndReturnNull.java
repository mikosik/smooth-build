package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.message.WarningMessage;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class ReportWarningAndReturnNull {
  @SmoothFunction
  public static SString reportWarning(Container container, SString message) {
    container.log(new WarningMessage(message.value()));
    return null;
  }
}
