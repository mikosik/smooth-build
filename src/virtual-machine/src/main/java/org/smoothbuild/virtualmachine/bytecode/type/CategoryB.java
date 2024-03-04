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
  private final String name;
  private final Hash hash;
  private final CategoryKindB kind;

  protected CategoryB(Hash hash, String name, CategoryKindB kind) {
    this.name = name;
    this.hash = hash;
    this.kind = kind;
  }

  public String name() {
    return name;
  }

  public String q() {
    return Strings.q(name);
  }

  public abstract ExprB newExpr(MerkleRoot merkleRoot, ExprDb exprDb)
      throws DecodeIllegalCategoryException;

  public Hash hash() {
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    return (object instanceof CategoryB that) && Objects.equals(hash(), that.hash());
  }

  @Override
  public int hashCode() {
    return hash().hashCode();
  }

  public CategoryKindB kind() {
    return kind;
  }

  public Class<? extends ExprB> typeJ() {
    return kind.typeJ();
  }

  @Override
  public String toString() {
    return name();
  }

  public boolean containsData() {
    return true;
  }
}
