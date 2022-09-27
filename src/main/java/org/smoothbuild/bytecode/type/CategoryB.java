package org.smoothbuild.bytecode.type;

import java.util.Objects;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.util.collect.Named;

/**
 * Category of ExprB.
 */
public abstract class CategoryB implements Named {
  private final String name;
  private final Hash hash;
  private final CategoryKindB kind;

  protected CategoryB(Hash hash, String name, CategoryKindB kind) {
    this.name = name;
    this.hash = hash;
    this.kind = kind;
  }

  @Override
  public String name() {
    return name;
  }

  public abstract ExprB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb);

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