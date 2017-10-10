package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class OneStringParameter {
  @SmoothFunction
  public static SString oneStringParameter(Container container, SString string) {
    return string;
  }
}