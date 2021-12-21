package org.smoothbuild.db.object.type.base;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjB;
import org.smoothbuild.lang.base.type.api.AbstractT;
import org.smoothbuild.lang.base.type.api.Var;

import com.google.common.collect.ImmutableSet;

/**
 * Category of ObjH.
 */
public abstract class CatB extends AbstractT {
  private final Hash hash;
  private final CatKindB kind;

  protected CatB(String name, Hash hash, CatKindB kind) {
    this(name, hash, kind, ImmutableSet.of());
  }

  protected CatB(String name, Hash hash, CatKindB kind,
      ImmutableSet<? extends Var> vars) {
    super(name, vars);
    this.hash = hash;
    this.kind = kind;
  }

  /**
   * Creates new java instance of Obj represented by merkleRoot.
   */
  public ObjB newObj(MerkleRoot merkleRoot, ByteDb byteDb) {
    checkArgument(this.equals(merkleRoot.cat()));
    return kind.newInstanceJ(merkleRoot, byteDb);
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

  public boolean isNothing() {
    return false;
  }

  @Override
  public String toString() {
    return "Category(`" + name() + "`)";
  }
}