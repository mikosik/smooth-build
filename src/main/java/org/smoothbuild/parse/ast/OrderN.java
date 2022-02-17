package org.smoothbuild.parse.ast;

import java.util.List;

import org.smoothbuild.lang.define.Loc;

import com.google.common.collect.ImmutableList;

public final class OrderN extends ExprN {
  private final List<ExprN> elems;

  public OrderN(List<ExprN> elems, Loc loc) {
    super(loc);
    this.elems = ImmutableList.copyOf(elems);
  }

  public List<ExprN> elems() {
    return elems;
  }
}
