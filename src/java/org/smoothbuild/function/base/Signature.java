package org.smoothbuild.function.base;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;

/**
 * Function's signature.
 */
public class Signature {
  private final Type type;
  private final Name name;
  private final ImmutableMap<String, Param> params;

  public Signature(Type type, Name name, ImmutableMap<String, Param> params) {
    this.type = checkNotNull(type);
    this.name = checkNotNull(name);
    this.params = checkNotNull(params);
  }

  public Type type() {
    return type;
  }

  public Name name() {
    return name;
  }

  public ImmutableMap<String, Param> params() {
    return params;
  }
}
