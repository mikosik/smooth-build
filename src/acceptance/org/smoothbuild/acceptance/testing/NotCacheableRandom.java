package org.smoothbuild.acceptance.testing;

import java.util.Random;

import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class NotCacheableRandom {
  @SmoothFunction(value = "notCacheableRandom", cacheable = false)
  public static SString notCacheableRandom(NativeApi nativeApi) {
    return nativeApi.create().string(Integer.toString(new Random().nextInt()));
  }
}
