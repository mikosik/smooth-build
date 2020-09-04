package org.smoothbuild.acceptance.testing;

import java.util.Random;

import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class NotCacheableRandom {
  @NativeImplementation(value = "notCacheableRandom", cacheable = false)
  public static Str notCacheableRandom(NativeApi nativeApi) {
    return nativeApi.factory().string(Integer.toString(new Random().nextInt()));
  }
}
