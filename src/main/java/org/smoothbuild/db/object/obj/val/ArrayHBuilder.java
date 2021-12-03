package org.smoothbuild.db.object.obj.val;

import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.db.object.obj.Helpers.wrapHashedDbExceptionAsObjectDbException;

import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.type.val.ArrayTH;

public class ArrayHBuilder {
  private final ArrayTH type;
  private final ObjDb objDb;
  private final List<ValH> elems;

  public ArrayHBuilder(ArrayTH type, ObjDb objDb) {
    this.type = type;
    this.objDb = objDb;
    this.elems = new ArrayList<>();
  }

  public ArrayHBuilder addAll(Iterable<? extends ValH> objs) {
    stream(objs).forEach(this::add);
    return this;
  }

  public ArrayHBuilder add(ValH elem) {
    if (!objDb.typing().isAssignable(type.elem(), elem.cat())) {
      throw new IllegalArgumentException("Element type must be " + type.elem().name()
          + " but was " + elem.cat().name() + ".");
    }
    Class<?> required = type.elem().typeJ();
    if (!required.equals(elem.getClass())) {
      throw new IllegalArgumentException("Element must be instance of java class "
          + required.getCanonicalName() + " but it is instance of "
          + elem.getClass().getCanonicalName() + ".");
    }
    this.elems.add(elem);
    return this;
  }

  public ArrayH build() {
    return wrapHashedDbExceptionAsObjectDbException(() -> objDb.newArray(type, elems));
  }
}
