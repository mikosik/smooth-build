package org.smoothbuild.parse.ast;

import java.util.List;

import org.smoothbuild.lang.base.Loc;

import com.google.common.collect.ImmutableList;

public final class OrderN extends MonoExprN {
  private final List<ObjN> elems;

  public OrderN(List<ObjN> elems, Loc loc) {
    super(loc);
    this.elems = ImmutableList.copyOf(elems);
  }

  public List<ObjN> elems() {
    return elems;
  }
}
