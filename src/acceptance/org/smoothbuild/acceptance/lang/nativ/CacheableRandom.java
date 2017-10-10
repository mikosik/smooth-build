package org.smoothbuild.acceptance.lang.nativ;

import java.util.Random;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class CacheableRandom {
  @SmoothFunction
  public static SString cacheableRandom(Container container) {
    return container.create().string(Integer.toString(new Random().nextInt()));
  }
}
