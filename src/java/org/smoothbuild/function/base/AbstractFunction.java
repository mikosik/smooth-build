package org.smoothbuild.function.base;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public abstract class AbstractFunction implements Function {
  private final Signature signature;
  private final HashCode hash;

  public AbstractFunction(Signature signature, HashCode hash) {
    this.signature = checkNotNull(signature);
    this.hash = checkNotNull(hash);
  }

  @Override
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
  public HashCode hash() {
    return hash;
  }

  @Override
  public ImmutableMap<String, Param> params() {
    return signature.params();
  }
}
