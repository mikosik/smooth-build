package org.smoothbuild.lang.function.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.type.SType;
import org.smoothbuild.lang.type.SValue;

import com.google.common.collect.ImmutableMap;

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
  public ImmutableMap<String, Param> params() {
    return signature.params();
  }
}
