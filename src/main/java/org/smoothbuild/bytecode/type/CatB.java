package org.smoothbuild.bytecode.type;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.db.Hash;

/**
 * Category of ObjH.
 */
public abstract class CatB {
  private final String name;
  private final Hash hash;
  private final CatKindB kind;

  protected CatB(Hash hash, String name, CatKindB kind) {
    this.name = name;
    this.hash = hash;
    this.kind = kind;
  }

  public String name() {
    return name;
  }

  public String q() {
    return "`" + name() + "`";
  }

  /**
   * Creates new java instance of Obj represented by merkleRoot.
   */
  public ObjB newObj(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    checkArgument(this.equals(merkleRoot.cat()));
    return kind.newInstanceJ(merkleRoot, objDb);
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

  public Class<? extends ObjB> typeJ() {
    return kind.typeJ();
  }

  @Override
  public String toString() {
    return "Category(`" + name() + "`)";
  }
}
