package org.smoothbuild.acceptance.testing;

import java.util.Random;

import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class CacheableRandom {
  @SmoothFunction("cacheableRandom")
  public static SString cacheableRandom(NativeApi nativeApi) {
    return nativeApi.create().string(Integer.toString(new Random().nextInt()));
  }
}
