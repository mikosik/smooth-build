package org.smoothbuild.lang.parse.ast;

import java.util.List;

import org.smoothbuild.lang.base.define.Loc;

import com.google.common.collect.ImmutableList;

public final class ArrayN extends ExprN {
  private final List<ExprN> elems;

  public ArrayN(List<ExprN> elems, Loc loc) {
    super(loc);
    this.elems = ImmutableList.copyOf(elems);
  }

  public List<ExprN> elems() {
    return elems;
  }
}
