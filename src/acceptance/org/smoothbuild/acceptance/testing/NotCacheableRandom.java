package org.smoothbuild.acceptance.testing;

import java.util.Random;

import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class NotCacheableRandom {
  @NativeImplementation(value = "notCacheableRandom", cacheable = false)
  public static RString notCacheableRandom(NativeApi nativeApi) {
    return nativeApi.factory().string(Integer.toString(new Random().nextInt()));
  }
}
