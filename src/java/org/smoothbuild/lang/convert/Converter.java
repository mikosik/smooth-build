package org.smoothbuild.lang.convert;

import org.smoothbuild.lang.type.SType;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.lang.type.SValueBuilders;

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

  public abstract T convert(SValueBuilders valueBuilders, S value);
}
