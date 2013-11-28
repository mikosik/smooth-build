package org.smoothbuild.lang.convert;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.type.STypes.EMPTY_ARRAY;

import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SArrayType;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.lang.type.SValueBuilders;

public class EmptyArrayToTypedArrayConverter<T extends SValue> extends Converter<SArray<T>> {
  private final SArrayType<T> arrayType;

  public EmptyArrayToTypedArrayConverter(SArrayType<T> arrayType) {
    super(EMPTY_ARRAY, arrayType);
    this.arrayType = arrayType;
  }

  @Override
  public SArray<T> convert(SValueBuilders valueBuilders, SValue value) {
    checkArgument(value.type() == EMPTY_ARRAY);
    return valueBuilders.arrayBuilder(arrayType).build();
  }
}
