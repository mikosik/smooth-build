package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.common.collect.List.listOfAll;

import java.util.ArrayList;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayType;

public class BArrayBuilder {
  private final BArrayType type;
  private final BExprDb exprDb;
  private final java.util.List<BValue> elements;

  public BArrayBuilder(BArrayType type, BExprDb exprDb) {
    this.type = type;
    this.exprDb = exprDb;
    this.elements = new ArrayList<>();
  }

  public BArrayBuilder addAll(Iterable<? extends BValue> elems) {
    stream(elems).forEach(this::add);
    return this;
  }

  public BArrayBuilder add(BValue elem) {
    if (!type.element().equals(elem.type())) {
      throw new IllegalArgumentException("Element type must be "
          + type.element().q() + " but was " + elem.type().q() + ".");
    }
    Class<?> required = type.element().javaType();
    if (!required.isInstance(elem)) {
      throw new IllegalArgumentException("Element must be instance of java class "
          + required.getCanonicalName() + " but it is instance of "
          + elem.getClass().getCanonicalName() + ".");
    }
    this.elements.add(elem);
    return this;
  }

  public BArray build() throws BytecodeException {
    return exprDb.newArray(type, listOfAll(elements));
  }
}
