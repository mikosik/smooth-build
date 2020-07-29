package org.smoothbuild.acceptance.testing;

import java.util.Random;

import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class CacheableRandom {
  @SmoothFunction("cacheableRandom")
  public static RString cacheableRandom(NativeApi nativeApi) {
    return nativeApi.factory().string(Integer.toString(new Random().nextInt()));
  }
}
