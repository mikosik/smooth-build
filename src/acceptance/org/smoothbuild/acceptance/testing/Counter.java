package org.smoothbuild.acceptance.testing;

import java.util.concurrent.atomic.AtomicInteger;

import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.NativeApi;

public class Counter {
  private static final AtomicInteger COUNTER = new AtomicInteger();

  public static Str function(NativeApi nativeApi) {
    return nativeApi.factory().string(Integer.toString(COUNTER.incrementAndGet()));
  }
}
