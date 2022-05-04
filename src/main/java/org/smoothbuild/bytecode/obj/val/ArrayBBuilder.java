package org.smoothbuild.bytecode.obj.val;

import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.bytecode.obj.Helpers.wrapHashedDbExcAsObjDbExc;

import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.type.val.ArrayTB;

public class ArrayBBuilder {
  private final ArrayTB type;
  private final ObjDbImpl objDb;
  private final List<ValB> elems;

  public ArrayBBuilder(ArrayTB type, ObjDbImpl objDb) {
    if (!type.vars().isEmpty()) {
      throw new IllegalArgumentException(
          "Cannot create array object with polymorphic type " + type.q() + ".");
    }
    this.type = type;
    this.objDb = objDb;
    this.elems = new ArrayList<>();
  }

  public ArrayBBuilder addAll(Iterable<? extends ValB> objs) {
    stream(objs).forEach(this::add);
    return this;
  }

  public ArrayBBuilder add(ValB elem) {
    if (!type.elem().equals(elem.cat())) {
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
    return wrapHashedDbExcAsObjDbExc(() -> objDb.newArray(type, elems));
  }
}
