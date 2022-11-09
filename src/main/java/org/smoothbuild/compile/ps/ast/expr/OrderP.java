package org.smoothbuild.compile.ps.ast.expr;

import java.util.List;

import org.smoothbuild.compile.lang.base.Loc;

import com.google.common.collect.ImmutableList;

public final class OrderP extends ExprP {
  private final List<ExprP> elems;

  public OrderP(List<ExprP> elems, Loc loc) {
    super(loc);
    this.elems = ImmutableList.copyOf(elems);
  }

  public List<ExprP> elems() {
    return elems;
  }
}
