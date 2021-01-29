package org.smoothbuild.acceptance.testing;

import java.util.Random;

import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.NativeApi;

public class CacheableRandom {
  public static Str function(NativeApi nativeApi) {
    return nativeApi.factory().string(Integer.toString(new Random().nextInt()));
  }
}
