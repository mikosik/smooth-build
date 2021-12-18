package org.smoothbuild.db.object.obj.val;

import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.db.object.obj.Helpers.wrapHashedDbExceptionAsObjectDbException;

import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.type.val.ArrayTB;

public class ArrayBBuilder {
  private final ArrayTB type;
  private final ByteDb byteDb;
  private final List<ValB> elems;

  public ArrayBBuilder(ArrayTB type, ByteDb byteDb) {
    this.type = type;
    this.byteDb = byteDb;
    this.elems = new ArrayList<>();
  }

  public ArrayBBuilder addAll(Iterable<? extends ValB> objs) {
    stream(objs).forEach(this::add);
    return this;
  }

  public ArrayBBuilder add(ValB elem) {
    if (!byteDb.typing().isAssignable(type.elem(), elem.cat())) {
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

  public ArrayB build() {
    return wrapHashedDbExceptionAsObjectDbException(() -> byteDb.newArray(type, elems));
  }
}
