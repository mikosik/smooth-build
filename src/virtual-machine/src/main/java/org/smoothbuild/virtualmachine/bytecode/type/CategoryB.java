package org.smoothbuild.virtualmachine.bytecode.type;

import java.util.Objects;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.base.Strings;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeIllegalCategoryException;

/**
 * Category of ExprB.
 */
public abstract class CategoryB {
  private final Hash hash;
  private final String name;
  private final Class<? extends ExprB> javaType;

  protected CategoryB(Hash hash, String name, Class<? extends ExprB> javaType) {
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

  public Class<? extends ExprB> javaType() {
    return javaType;
  }

  public String q() {
    return Strings.q(name);
  }

  public abstract ExprB newExpr(MerkleRoot merkleRoot, ExprDb exprDb)
      throws DecodeIllegalCategoryException;

  @Override
  public boolean equals(Object object) {
    return (object instanceof CategoryB that) && Objects.equals(hash(), that.hash());
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
