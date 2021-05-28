package org.smoothbuild.acceptance.testing;

import static org.smoothbuild.plugin.Caching.Scope.NONE;

import java.util.concurrent.atomic.AtomicInteger;

import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.Caching;
import org.smoothbuild.plugin.NativeApi;

public class Counter {
  private static final AtomicInteger COUNTER = new AtomicInteger();

  @Caching(scope = NONE)
  public static Str function(NativeApi nativeApi) {
    return nativeApi.factory().string(Integer.toString(COUNTER.incrementAndGet()));
  }
}
