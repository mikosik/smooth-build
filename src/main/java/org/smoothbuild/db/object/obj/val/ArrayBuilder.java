package org.smoothbuild.db.object.obj.val;

import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.db.object.db.Helpers.wrapHashedDbExceptionAsObjectDbException;

import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.spec.val.ArraySpec;

public class ArrayBuilder {
  private final ArraySpec spec;
  private final ObjectDb objectDb;
  private final List<Obj> elements;

  public ArrayBuilder(ArraySpec spec, ObjectDb objectDb) {
    this.spec = spec;
    this.objectDb = objectDb;
    this.elements = new ArrayList<>();
  }

  public ArrayBuilder addAll(Iterable<? extends Obj> objs) {
    stream(objs).forEach(this::add);
    return this;
  }

  public ArrayBuilder add(Obj elem) {
    if (!spec.elemSpec().equals(elem.spec())) {
      throw new IllegalArgumentException("Element spec must be " + spec.elemSpec().name()
          + " but was " + elem.spec().name() + ".");
    }
    Class<?> required = spec.elemSpec().jType();
    if (!required.equals(elem.getClass())) {
      throw new IllegalArgumentException("Element must be instance of java class "
          + required.getCanonicalName() + " but it is instance of "
          + elem.getClass().getCanonicalName() + ".");
    }
    elements.add(elem);
    return this;
  }

  public Array build() {
    return wrapHashedDbExceptionAsObjectDbException(() -> objectDb.newArrayV(spec, elements));
  }
}
