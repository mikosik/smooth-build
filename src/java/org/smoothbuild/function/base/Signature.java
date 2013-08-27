package org.smoothbuild.function.base;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;

/**
 * Function's signature.
 */
public class Signature {
  private final Type type;
  private final QualifiedName name;
  private final ImmutableMap<String, Param> params;

  public Signature(Type type, QualifiedName name, ImmutableMap<String, Param> params) {
    this.type = checkNotNull(type);
    this.name = checkNotNull(name);
    this.params = checkNotNull(params);
  }

  public Type type() {
    return type;
  }

  public QualifiedName name() {
    return name;
  }

  public ImmutableMap<String, Param> params() {
    return params;
  }
}
