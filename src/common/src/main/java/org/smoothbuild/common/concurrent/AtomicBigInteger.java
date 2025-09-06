package org.smoothbuild.common.concurrent;

import static java.util.Objects.requireNonNull;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicBigInteger {
  private final AtomicReference<BigInteger> value = new AtomicReference<>(BigInteger.ZERO);

  public BigInteger incrementAndGet() {
    BigInteger oldValue;
    BigInteger newValue;
    do {
      // requireNonNull check to make nullAway happy
      oldValue = requireNonNull(value.get());
      newValue = oldValue.add(BigInteger.ONE);
    } while (!value.compareAndSet(oldValue, newValue));
    return newValue;
  }
}
