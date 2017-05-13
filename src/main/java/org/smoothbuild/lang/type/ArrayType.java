package org.smoothbuild.lang.type;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Value;

import com.google.inject.TypeLiteral;

public class ArrayType extends Type {
  private final Type elemType;

  protected ArrayType(Type elemType, TypeLiteral<? extends Array<? extends Value>> jType) {
    super("[" + elemType.name() + "]", jType);
    this.elemType = elemType;
  }

  public Type elemType() {
    return elemType;
  }

  public Array<? extends Value> defaultValue(ValuesDb valuesDb) {
    Class<? extends Value> rawType = (Class<? extends Value>) elemType.jType().getRawType();
    return valuesDb.arrayBuilder(rawType).build();
  }
}
