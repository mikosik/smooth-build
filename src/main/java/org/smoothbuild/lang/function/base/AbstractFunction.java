package org.smoothbuild.lang.function.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.type.Type;

import com.google.common.collect.ImmutableList;

public abstract class AbstractFunction implements Function {
  private final Signature signature;

  public AbstractFunction(Signature signature) {
    this.signature = checkNotNull(signature);
  }

  public Signature signature() {
    return signature;
  }

  @Override
  public Type type() {
    return signature.type();
  }

  @Override
  public Name name() {
    return signature.name();
  }

  @Override
  public ImmutableList<Parameter> parameters() {
    return signature.parameters();
  }
}
