package org.smoothbuild.lang.value;

import static org.smoothbuild.lang.value.Array.storeArrayInDb;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.type.ArrayType;

import com.google.common.hash.HashCode;

public class ArrayBuilder<T extends Value> {
  private final ArrayType type;
  private final Function<HashCode, T> elementMarshaller;
  private final HashedDb hashedDb;
  private final List<T> elements;

  public ArrayBuilder(ArrayType type, Function<HashCode, T> elementMarshaller, HashedDb hashedDb) {
    this.type = type;
    this.elementMarshaller = elementMarshaller;
    this.hashedDb = hashedDb;
    this.elements = new ArrayList<>();
  }

  public ArrayBuilder<T> add(T elem) {
    Class<?> required = type.elemType().jType().getRawType();
    if (!required.isAssignableFrom(elem.getClass())) {
      throw new IllegalArgumentException("Element must be of type " + required.getCanonicalName());
    }
    elements.add(elem);
    return this;
  }

  public Array<T> build() {
    return storeArrayInDb(elements, type, elementMarshaller, hashedDb);
  }
}
