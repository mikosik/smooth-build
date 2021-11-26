package org.smoothbuild.db.object.obj.val;

import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.db.object.obj.Helpers.wrapHashedDbExceptionAsObjectDbException;

import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;

public class ArrayHBuilder {
  private final ArrayTypeH type;
  private final ObjectHDb objectHDb;
  private final List<ValueH> elements;

  public ArrayHBuilder(ArrayTypeH type, ObjectHDb objectHDb) {
    this.type = type;
    this.objectHDb = objectHDb;
    this.elements = new ArrayList<>();
  }

  public ArrayHBuilder addAll(Iterable<? extends ValueH> objs) {
    stream(objs).forEach(this::add);
    return this;
  }

  public ArrayHBuilder add(ValueH elem) {
    if (!objectHDb.typing().isAssignable(type.element(), elem.type())) {
      throw new IllegalArgumentException("Element type must be " + type.element().name()
          + " but was " + elem.type().name() + ".");
    }
    Class<?> required = type.element().jType();
    if (!required.equals(elem.getClass())) {
      throw new IllegalArgumentException("Element must be instance of java class "
          + required.getCanonicalName() + " but it is instance of "
          + elem.getClass().getCanonicalName() + ".");
    }
    this.elements.add(elem);
    return this;
  }

  public ArrayH build() {
    return wrapHashedDbExceptionAsObjectDbException(() -> objectHDb.newArray(type, elements));
  }
}
