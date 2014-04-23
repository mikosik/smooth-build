package org.smoothbuild.lang.convert;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.base.STypes.NIL;

import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SArrayType;
import org.smoothbuild.lang.base.SNothing;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.base.SValueFactory;

public class NilToTypedArrayConverter<T extends SValue> extends
    Converter<SArray<SNothing>, SArray<T>> {
  private final SArrayType<T> arrayType;

  public NilToTypedArrayConverter(SArrayType<T> arrayType) {
    super(NIL, arrayType);
    this.arrayType = arrayType;
  }

  @Override
  public SArray<T> convert(SValueFactory valueFactory, SArray<SNothing> value) {
    checkArgument(value.type() == NIL);
    return valueFactory.arrayBuilder(arrayType).build();
  }
}
