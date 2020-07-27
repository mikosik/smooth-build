package org.smoothbuild.acceptance.testing;

import java.util.Random;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.RString;

public class CacheableRandom {
  @SmoothFunction("cacheableRandom")
  public static RString cacheableRandom(NativeApi nativeApi) {
    return nativeApi.factory().string(Integer.toString(new Random().nextInt()));
  }
}
