package org.smoothbuild.acceptance.testing;

import static org.smoothbuild.plugin.Caching.Level.MEMORY;

import java.util.Random;

import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.Caching;
import org.smoothbuild.plugin.NativeApi;

public class CachingMemoryRandom {
  @Caching(level = MEMORY)
  public static Str function(NativeApi nativeApi) {
    return nativeApi.factory().string(Integer.toString(new Random().nextInt()));
  }
}
