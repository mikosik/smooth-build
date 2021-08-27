package org.smoothbuild.db.object.spec;

import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.MerkleRoot;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.db.ObjectDb;

public abstract class Spec {
  private final Hash hash;
  private final SpecKind kind;
  private final ObjectDb objectDb;

  protected Spec(Hash hash, SpecKind kind, ObjectDb objectDb) {
    this.hash = hash;
    this.kind = kind;
    this.objectDb = objectDb;
  }

  /**
   * Creates new java instance of Obj represented by merkleRoot.
   */
  public abstract Obj newObj(MerkleRoot merkleRoot);

  public Hash hash() {
    return hash;
  }

  public SpecKind kind() {
    return kind;
  }

  protected ObjectDb objectDb() {
    return objectDb;
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
    return name() + ":" + hash();
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
