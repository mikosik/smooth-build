package org.smoothbuild.lang.parse.ast;

import java.util.List;

import org.smoothbuild.lang.base.define.Location;

import com.google.common.collect.ImmutableList;

public final class ArrayN extends ExprN {
  private final List<ExprN> elems;

  public ArrayN(List<ExprN> elems, Location location) {
    super(location);
    this.elems = ImmutableList.copyOf(elems);
  }

  public List<ExprN> elems() {
    return elems;
  }
}
