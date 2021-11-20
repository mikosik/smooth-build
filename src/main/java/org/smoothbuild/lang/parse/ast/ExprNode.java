package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Location;

public sealed class ExprNode extends Node
    permits AnnotationNode, ArrayNode, BlobNode, CallNode, IntNode, RefNode, SelectNode, StringNode {
  public ExprNode(Location location) {
    super(location);
  }
}
