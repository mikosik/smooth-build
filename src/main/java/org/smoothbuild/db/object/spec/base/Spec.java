package org.smoothbuild.db.object.spec.base;

import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Obj;

public abstract class Spec {
  private final Hash hash;
  private final SpecKind kind;

  protected Spec(Hash hash, SpecKind kind) {
    this.hash = hash;
    this.kind = kind;
  }

  /**
   * Creates new java instance of Obj represented by merkleRoot.
   */
  public abstract Obj newObj(MerkleRoot merkleRoot, ObjectDb objectDb);

  public Hash hash() {
    return hash;
  }

  public SpecKind kind() {
    return kind;
  }

  @Override
  public boolean equals(Object object) {
    return (object instanceof Spec that) && Objects.equals(hash(), that.hash());
  }

  @Override
  public int hashCode() {
    return hash().hashCode();
  }

  @Override
  public String toString() {
    return name() + "@" + hash();
  }

  public String name() {
    return kind.name();
  }

  public Class<? extends Obj> jType() {
    return kind.jType();
  }

  public boolean isNothing() {
    return kind == SpecKind.NOTHING;
  }
}
