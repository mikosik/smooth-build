package org.smoothbuild.bytecode.type;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.db.Hash;
import org.smoothbuild.util.collect.Named;

/**
 * Category of ObjH.
 */
public abstract class CatB implements Named {
  private final String name;
  private final Hash hash;
  private final CatKindB kind;

  protected CatB(Hash hash, String name, CatKindB kind) {
    this.name = name;
    this.hash = hash;
    this.kind = kind;
  }

  @Override
  public String name() {
    return name;
  }

  /**
   * Creates new java instance of Obj represented by merkleRoot.
   */
  public ExprB newObj(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(this.equals(merkleRoot.cat()));
    return kind.newInstanceJ(merkleRoot, bytecodeDb);
  }

  public Hash hash() {
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    return (object instanceof CatB that) && Objects.equals(hash(), that.hash());
  }

  @Override
  public int hashCode() {
    return hash().hashCode();
  }

  public CatKindB kind() {
    return kind;
  }

  public Class<? extends ExprB> typeJ() {
    return kind.typeJ();
  }

  @Override
  public String toString() {
    return name();
  }
}
