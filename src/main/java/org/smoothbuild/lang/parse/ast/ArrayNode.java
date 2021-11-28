package org.smoothbuild.lang.parse.ast;

import java.util.List;

import org.smoothbuild.lang.base.define.Location;

import com.google.common.collect.ImmutableList;

public final class ArrayNode extends ExprNode {
  private final List<ExprNode> elems;

  public ArrayNode(List<ExprNode> elems, Location location) {
    super(location);
    this.elems = ImmutableList.copyOf(elems);
  }

  public List<ExprNode> elems() {
    return elems;
  }
}
