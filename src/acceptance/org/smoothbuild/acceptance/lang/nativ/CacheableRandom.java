package org.smoothbuild.acceptance.lang.nativ;

import java.util.Random;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class CacheableRandom {
  @SmoothFunction("cacheableRandom")
  public static SString cacheableRandom(NativeApi nativeApi) {
    return nativeApi.create().string(Integer.toString(new Random().nextInt()));
  }
}
