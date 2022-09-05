package org.smoothbuild.parse.ast.expr;

import java.util.List;

import org.smoothbuild.lang.base.Loc;

import com.google.common.collect.ImmutableList;

public final class OrderP extends OperP {
  private final List<ExprP> elems;

  public OrderP(List<ExprP> elems, Loc loc) {
    super(loc);
    this.elems = ImmutableList.copyOf(elems);
  }

  public List<ExprP> elems() {
    return elems;
  }
}
