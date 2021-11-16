package org.smoothbuild.lang.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;

public class ValueNode extends EvaluableNode {
  public ValueNode(Optional<TypeNode> type, String name, Optional<ExprNode> body,
      Optional<AnnotationNode> annotation, Location location) {
    super(type, name, body, annotation, location);
  }
}
