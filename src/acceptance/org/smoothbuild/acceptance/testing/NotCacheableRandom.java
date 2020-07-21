package org.smoothbuild.acceptance.testing;

import java.util.Random;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.SString;

public class NotCacheableRandom {
  @SmoothFunction(value = "notCacheableRandom", cacheable = false)
  public static SString notCacheableRandom(NativeApi nativeApi) {
    return nativeApi.factory().string(Integer.toString(new Random().nextInt()));
  }
}
