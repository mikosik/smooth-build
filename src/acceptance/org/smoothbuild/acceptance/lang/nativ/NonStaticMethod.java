package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class NonStaticMethod {
  @SmoothFunction
  public SString function(Container container) {
    return container.create().string("abc");
  }
}