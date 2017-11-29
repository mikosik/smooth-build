package org.smoothbuild.lang.value;

import static org.smoothbuild.lang.value.Array.storeArrayInDb;

import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.type.ArrayType;

public class ArrayBuilder {
  private final ArrayType type;
  private final HashedDb hashedDb;
  private final List<Value> elements;

  public ArrayBuilder(ArrayType type, HashedDb hashedDb) {
    this.type = type;
    this.hashedDb = hashedDb;
    this.elements = new ArrayList<>();
  }

  public ArrayBuilder add(Value elem) {
    if (!type.elemType().equals(elem.type())) {
      throw new IllegalArgumentException("Element type must be " + type.elemType() + ".");
    }
    Class<?> required = type.elemType().jType();
    if (!required.equals(elem.getClass())) {
      throw new IllegalArgumentException("Element must be instance of java class "
          + required.getCanonicalName() + " but it is instance of "
          + elem.getClass().getCanonicalName() + ".");
    }
    elements.add(elem);
    return this;
  }

  public Array build() {
    return storeArrayInDb(elements, type, hashedDb);
  }
}
