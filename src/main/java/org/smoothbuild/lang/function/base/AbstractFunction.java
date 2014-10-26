package org.smoothbuild.lang.function.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.base.Value;

import com.google.common.collect.ImmutableList;

public abstract class AbstractFunction<T extends Value> implements Function<T> {
  private final Signature<T> signature;

  public AbstractFunction(Signature<T> signature) {
    this.signature = checkNotNull(signature);
  }

  @Override
  public Type<T> type() {
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
