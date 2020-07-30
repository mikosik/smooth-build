package org.smoothbuild.db.record.spec;

import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.record.base.MerkleRoot;
import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.db.record.db.RecordDb;

public abstract class Spec {
  private final Hash hash;
  private final SpecKind kind;
  protected final HashedDb hashedDb;
  protected final RecordDb recordDb;

  protected Spec(Hash hash, SpecKind kind, HashedDb hashedDb, RecordDb recordDb) {
    this.hash = hash;
    this.kind = kind;
    this.hashedDb = hashedDb;
    this.recordDb = recordDb;
  }

  /**
   * Creates new java object Record represented by merkleRoot.
   */
  public abstract Record newJObject(MerkleRoot merkleRoot);

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

  public Class<? extends Record> jType() {
    return kind.jType();
  }

  public boolean isArray() {
    return this instanceof ArraySpec;
  }

  public boolean isNothing() {
    return kind == SpecKind.NOTHING;
  }
}
