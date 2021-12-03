package org.smoothbuild.db.object.type.base;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.lang.base.type.api.AbstractT;
import org.smoothbuild.lang.base.type.api.Var;

import com.google.common.collect.ImmutableSet;

/**
 * Category of ObjH.
 */
public abstract class CatH extends AbstractT {
  private final Hash hash;
  private final CatKindH kind;

  protected CatH(String name, Hash hash, CatKindH kind) {
    this(name, hash, kind, ImmutableSet.of());
  }

  protected CatH(String name, Hash hash, CatKindH kind,
      ImmutableSet<? extends Var> vars) {
    super(name, vars);
    this.hash = hash;
    this.kind = kind;
  }

  /**
   * Creates new java instance of Obj represented by merkleRoot.
   */
  public ObjH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    checkArgument(this.equals(merkleRoot.cat()));
    return kind.newInstanceJ(merkleRoot, objDb);
  }

  public Hash hash() {
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    return (object instanceof CatH that) && Objects.equals(hash(), that.hash());
  }

  @Override
  public int hashCode() {
    return hash().hashCode();
  }

  public CatKindH kind() {
    return kind;
  }

  public Class<? extends ObjH> typeJ() {
    return kind.typeJ();
  }

  public boolean isNothing() {
    return false;
  }
}
