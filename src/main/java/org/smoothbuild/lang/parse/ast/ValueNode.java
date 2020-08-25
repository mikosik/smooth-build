package org.smoothbuild.lang.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.Type;

public class ValueNode extends EvaluableNode implements RefTarget {
  public ValueNode(TypeNode type, String name, ExprNode expr, Location location) {
    super(type, name, expr, location);
  }

  @Override
  public Optional<Type> inferredType() {
    return type();
  }
}
