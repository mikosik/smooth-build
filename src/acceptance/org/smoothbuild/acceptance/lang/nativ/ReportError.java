package org.smoothbuild.acceptance.lang.nativ;

import static org.smoothbuild.lang.message.MessageException.errorException;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.SString;

public class ReportError {
  @SmoothFunction
  public static Nothing reportError(NativeApi nativeApi, SString message) {
    throw errorException(message.data());
  }
}
