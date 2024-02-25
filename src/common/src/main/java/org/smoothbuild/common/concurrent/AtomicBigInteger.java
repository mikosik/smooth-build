package org.smoothbuild.common.concurrent;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicBigInteger {
  private final AtomicReference<BigInteger> value = new AtomicReference<>(BigInteger.ZERO);

  public BigInteger incrementAndGet() {
    BigInteger oldValue;
    BigInteger newValue;
    do {
      oldValue = value.get();
      newValue = oldValue.add(BigInteger.ONE);
    } while (!value.compareAndSet(oldValue, newValue));
    return newValue;
  }
}
