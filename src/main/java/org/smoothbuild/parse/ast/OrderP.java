package org.smoothbuild.parse.ast;

import java.util.List;

import org.smoothbuild.lang.base.Loc;

import com.google.common.collect.ImmutableList;

public final class OrderP extends MonoP implements MonoExprP {
  private final List<ObjP> elems;

  public OrderP(List<ObjP> elems, Loc loc) {
    super(loc);
    this.elems = ImmutableList.copyOf(elems);
  }

  public List<ObjP> elems() {
    return elems;
  }
}
