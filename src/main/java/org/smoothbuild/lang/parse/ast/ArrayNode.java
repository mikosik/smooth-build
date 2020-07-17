package org.smoothbuild.lang.parse.ast;

import java.util.List;

import org.smoothbuild.lang.base.Location;

import com.google.common.collect.ImmutableList;

public class ArrayNode extends ExprNode {
  private final List<ExprNode> elements;

  public ArrayNode(List<ExprNode> elements, Location location) {
    super(location);
    this.elements = ImmutableList.copyOf(elements);
  }

  public List<ExprNode> elements() {
    return elements;
  }
}
