package org.smoothbuild.lang.object.base;

import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.lang.object.db.Helpers.wrapException;

import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.lang.object.type.ConcreteArrayType;

public class ArrayBuilder {
  private final ConcreteArrayType type;
  private final ObjectsDb objectsDb;
  private final List<SObject> elements;

  public ArrayBuilder(ConcreteArrayType type, ObjectsDb objectsDb) {
    this.type = type;
    this.objectsDb = objectsDb;
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
    return wrapException(() -> objectsDb.newArraySObject(type, elements));
  }
}
