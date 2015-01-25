package org.smoothbuild.lang.type;

import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Value;

import com.google.inject.TypeLiteral;

public class ArrayType extends Type {
  private final Type elemType;

  protected ArrayType(Type elemType, TypeLiteral<? extends Array<? extends Value>> jType) {
    super(elemType.name() + "[]", jType);
    this.elemType = elemType;
  }

  public Type elemType() {
    return elemType;
  }

  @Override
  public Value defaultValue(ObjectsDb objectsDb) {
    Class<? extends Value> rawType = (Class<? extends Value>) elemType.jType().getRawType();
    return objectsDb.arrayBuilder(rawType).build();
  }

  @Override
  public boolean isAllowedAsResult() {
    return true;
  }

  @Override
  public boolean isAllowedAsParameter() {
    return true;
  }
}
