package org.smoothbuild.db.object.obj.val;

import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.db.object.obj.Helpers.wrapHashedDbExceptionAsObjectDbException;

import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.type.val.ArrayTypeO;

public class ArrayBuilder {
  private final ArrayTypeO type;
  private final ObjDb objDb;
  private final List<Obj> elements;

  public ArrayBuilder(ArrayTypeO type, ObjDb objDb) {
    this.type = type;
    this.objDb = objDb;
    this.elements = new ArrayList<>();
  }

  public ArrayBuilder addAll(Iterable<? extends Obj> objs) {
    stream(objs).forEach(this::add);
    return this;
  }

  public ArrayBuilder add(Obj elements) {
    if (!type.element().equals(elements.type())) {
      throw new IllegalArgumentException("Element type must be " + type.element().name()
          + " but was " + elements.type().name() + ".");
    }
    Class<?> required = type.element().jType();
    if (!required.equals(elements.getClass())) {
      throw new IllegalArgumentException("Element must be instance of java class "
          + required.getCanonicalName() + " but it is instance of "
          + elements.getClass().getCanonicalName() + ".");
    }
    this.elements.add(elements);
    return this;
  }

  public Array build() {
    return wrapHashedDbExceptionAsObjectDbException(() -> objDb.newArray(type, elements));
  }
}
