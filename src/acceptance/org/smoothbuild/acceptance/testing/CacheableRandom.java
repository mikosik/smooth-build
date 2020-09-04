package org.smoothbuild.acceptance.testing;

import java.util.Random;

import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class CacheableRandom {
  @NativeImplementation("cacheableRandom")
  public static Str cacheableRandom(NativeApi nativeApi) {
    return nativeApi.factory().string(Integer.toString(new Random().nextInt()));
  }
}
