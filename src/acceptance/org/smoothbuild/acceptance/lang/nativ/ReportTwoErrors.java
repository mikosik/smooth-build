package org.smoothbuild.acceptance.lang.nativ;

import static org.smoothbuild.lang.message.MessageException.errorException;

import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class ReportTwoErrors {
  @SmoothFunction
  public static SString reportTwoErrors(NativeApi nativeApi, SString message1, SString message2) {
    nativeApi.log(new ErrorMessage(message1.data()));
    throw errorException(message2.data());
  }
}
