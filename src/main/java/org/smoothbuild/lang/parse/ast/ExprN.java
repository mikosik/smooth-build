package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Location;

public sealed class ExprN extends Node
    permits AnnotationN, ArrayN, BlobN, CallN, IntN, RefN, SelectN, StringN {
  public ExprN(Location location) {
    super(location);
  }
}
