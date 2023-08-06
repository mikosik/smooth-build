package org.smoothbuild.virtualmachine.bytecode.expr.value;

import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.common.collect.List.listOfAll;

import java.util.ArrayList;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.type.value.ArrayTB;

public class ArrayBBuilder {
  private final ArrayTB type;
  private final ExprDb exprDb;
  private final java.util.List<ValueB> elems;

  public ArrayBBuilder(ArrayTB type, ExprDb exprDb) {
    this.type = type;
    this.exprDb = exprDb;
    this.elems = new ArrayList<>();
  }

  public ArrayBBuilder addAll(Iterable<? extends ValueB> elems) {
    stream(elems).forEach(this::add);
    return this;
  }

  public ArrayBBuilder add(ValueB elem) {
    if (!type.elem().equals(elem.type())) {
      throw new IllegalArgumentException(
          "Element type must be " + type.elem().q() + " but was " + elem.type().q() + ".");
    }
    Class<?> required = type.elem().typeJ();
    if (!required.isInstance(elem)) {
      throw new IllegalArgumentException("Element must be instance of java class "
          + required.getCanonicalName() + " but it is instance of "
          + elem.getClass().getCanonicalName() + ".");
    }
    this.elems.add(elem);
    return this;
  }

  public ArrayB build() throws BytecodeException {
    return exprDb.newArray(type, listOfAll(elems));
  }
}
