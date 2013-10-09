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

  public Signature signature() {
    return signature;
  }

  public Type type() {
    return signature.type();
  }

  public Name name() {
    return signature.name();
  }

  public HashCode hash() {
    return hash;
  }

  public ImmutableMap<String, Param> params() {
    return signature.params();
  }
}
