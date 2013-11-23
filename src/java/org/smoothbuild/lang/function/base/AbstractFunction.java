package org.smoothbuild.lang.function.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.type.Type;

import com.google.common.collect.ImmutableMap;

public abstract class AbstractFunction implements Function {
  private final Signature signature;

  public AbstractFunction(Signature signature) {
    this.signature = checkNotNull(signature);
  }

  @Override
  public Signature signature() {
    return signature;
  }

  @Override
  public Type<?> type() {
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
