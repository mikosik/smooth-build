package org.smoothbuild.virtualmachine.bytecode.kind.base;

import java.util.Objects;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.base.Strings;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeIllegalKindException;

/**
 * Kind of Bytecode Expression ({@link BExpr}).
 */
public abstract class BKind {
  private final Hash hash;
  private final String name;
  private final Class<? extends BExpr> javaType;

  protected BKind(Hash hash, String name, Class<? extends BExpr> javaType) {
    this.hash = hash;
    this.name = name;
    this.javaType = javaType;
  }

  public Hash hash() {
    return hash;
  }

  public String name() {
    return name;
  }

  public Class<? extends BExpr> javaType() {
    return javaType;
  }

  public String q() {
    return Strings.q(name);
  }

  public abstract BExpr newExpr(MerkleRoot merkleRoot, BExprDb exprDb)
      throws DecodeIllegalKindException;

  @Override
  public boolean equals(Object object) {
    return (object instanceof BKind that) && Objects.equals(hash(), that.hash());
  }

  @Override
  public int hashCode() {
    return hash().hashCode();
  }

  @Override
  public String toString() {
    return name();
  }

  public boolean containsData() {
    return true;
  }
}
