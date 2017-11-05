package org.smoothbuild.lang.value;

import static org.smoothbuild.lang.value.Array.storeArrayInDb;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.type.ArrayType;

import com.google.common.hash.HashCode;

public class ArrayBuilder {
  private final ArrayType type;
  private final Function<HashCode, ? extends Value> elementMarshaller;
  private final HashedDb hashedDb;
  private final List<Value> elements;

  public ArrayBuilder(ArrayType type, Function<HashCode, ? extends Value> elementMarshaller,
      HashedDb hashedDb) {
    this.type = type;
    this.elementMarshaller = elementMarshaller;
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
    return storeArrayInDb(elements, type, elementMarshaller, hashedDb);
  }
}
