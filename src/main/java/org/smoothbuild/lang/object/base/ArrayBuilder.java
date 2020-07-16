package org.smoothbuild.lang.object.base;

import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.lang.object.db.Helpers.wrapException;

import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.lang.object.db.ObjectDb;
import org.smoothbuild.lang.object.type.ArrayType;

public class ArrayBuilder {
  private final ArrayType type;
  private final ObjectDb objectDb;
  private final List<SObject> elements;

  public ArrayBuilder(ArrayType type, ObjectDb objectDb) {
    this.type = type;
    this.objectDb = objectDb;
    this.elements = new ArrayList<>();
  }

  public ArrayBuilder addAll(Iterable<? extends SObject> objects) {
    stream(objects).forEach(this::add);
    return this;
  }

  public ArrayBuilder add(SObject elem) {
    if (!type.elemType().equals(elem.type())) {
      throw new IllegalArgumentException("Element type must be " + type.elemType().name()
          + " but was " + elem.type().name() + ".");
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
    return wrapException(() -> objectDb.newArray(type, elements));
  }
}
