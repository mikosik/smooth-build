package org.smoothbuild.bytecode.expr.val;

import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.bytecode.expr.Helpers.wrapHashedDbExcAsBytecodeDbExc;

import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.type.val.ArrayTB;

public class ArrayBBuilder {
  private final ArrayTB type;
  private final BytecodeDb bytecodeDb;
  private final List<ValB> elems;

  public ArrayBBuilder(ArrayTB type, BytecodeDb bytecodeDb) {
    this.type = type;
    this.bytecodeDb = bytecodeDb;
    this.elems = new ArrayList<>();
  }

  public ArrayBBuilder addAll(Iterable<? extends ValB> elems) {
    stream(elems).forEach(this::add);
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
    return wrapHashedDbExcAsBytecodeDbExc(() -> bytecodeDb.newArray(type, elems));
  }
}
