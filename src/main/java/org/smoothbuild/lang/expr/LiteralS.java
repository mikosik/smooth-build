package org.smoothbuild.lang.expr;

public sealed interface LiteralS extends ExprS permits BlobS, IntS, StringS {
  public String toShortString();
}
