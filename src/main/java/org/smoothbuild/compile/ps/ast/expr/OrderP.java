package org.smoothbuild.compile.ps.ast.expr;

import java.util.List;

import org.smoothbuild.compile.lang.base.location.Location;

import com.google.common.collect.ImmutableList;

public final class OrderP extends ExprP {
  private final List<ExprP> elems;

  public OrderP(List<ExprP> elems, Location location) {
    super(location);
    this.elems = ImmutableList.copyOf(elems);
  }

  public List<ExprP> elems() {
    return elems;
  }
}
