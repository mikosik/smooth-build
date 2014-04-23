package org.smoothbuild.lang.convert;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.base.SValueFactory;

import com.google.common.hash.HashCode;

public abstract class Converter<S extends SValue, T extends SValue> {
  private static final String ARROW_STRING = " => ";

  private final SType<S> sourceType;
  private final SType<T> targetType;

  public Converter(SType<S> sourceType, SType<T> targetType) {
    this.sourceType = sourceType;
    this.targetType = targetType;
  }

  public String name() {
    return sourceType.name() + ARROW_STRING + targetType.name();
  }

  public SType<T> targetType() {
    return targetType;
  }

  public abstract T convert(SValueFactory valueBuilders, S value);

  public HashCode hash() {
    return Hash.string(this.getClass().getCanonicalName());
  }
}
