package org.smoothbuild.db.object.spec;

import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.base.MerkleRoot;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.db.ObjectDb;

public abstract class Spec {
  private final Hash hash;
  private final SpecKind kind;
  protected final HashedDb hashedDb;
  protected final ObjectDb objectDb;

  protected Spec(Hash hash, SpecKind kind, HashedDb hashedDb, ObjectDb objectDb) {
    this.hash = hash;
    this.kind = kind;
    this.hashedDb = hashedDb;
    this.objectDb = objectDb;
  }

  /**
   * Creates new java instance of Obj represented by merkleRoot.
   */
  public abstract Obj newJObject(MerkleRoot merkleRoot);

  public Hash hash() {
    return hash;
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

  public SpecKind kind() {
    return kind;
  }

  public Class<? extends Obj> jType() {
    return kind.jType();
  }

  public boolean isArray() {
    return this instanceof ArraySpec;
  }

  public boolean isNothing() {
    return kind == SpecKind.NOTHING;
  }
}
