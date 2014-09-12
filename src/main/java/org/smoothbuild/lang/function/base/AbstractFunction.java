package org.smoothbuild.lang.function.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;

import com.google.common.collect.ImmutableList;

public abstract class AbstractFunction<T extends SValue> implements Function<T> {
  private final Signature<T> signature;

  public AbstractFunction(Signature<T> signature) {
    this.signature = checkNotNull(signature);
  }

  @Override
  public Signature<T> signature() {
    return signature;
  }

  @Override
  public SType<T> type() {
    return signature.type();
  }

  @Override
  public Name name() {
    return signature.name();
  }

  @Override
  public ImmutableList<Param> params() {
    return signature.params();
  }
}
