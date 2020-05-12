package org.smoothbuild.slib.testing;

import java.util.Random;

import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class NotCacheableRandom {
  @SmoothFunction(value = "notCacheableRandom", cacheable = false)
  public static SString notCacheableRandom(NativeApi nativeApi) {
    return nativeApi.factory().string(Integer.toString(new Random().nextInt()));
  }
}
