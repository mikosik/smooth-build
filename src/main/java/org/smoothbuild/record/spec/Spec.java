package org.smoothbuild.record.spec;

import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.record.base.MerkleRoot;
import org.smoothbuild.record.base.Record;
import org.smoothbuild.record.base.RecordImpl;
import org.smoothbuild.record.db.RecordDb;

public abstract class Spec implements Record {
  private final SpecKind kind;
  private final RecordImpl record;
  protected final HashedDb hashedDb;
  protected final RecordDb recordDb;

  protected Spec(MerkleRoot merkleRoot, SpecKind kind, HashedDb hashedDb,
      RecordDb recordDb) {
    this.kind = kind;
    this.record = new RecordImpl(merkleRoot, hashedDb);
    this.hashedDb = hashedDb;
    this.recordDb = recordDb;
  }

  /**
   * Creates new java object Record represented by merkleRoot.
   */
  public abstract Record newJObject(MerkleRoot merkleRoot);

  @Override
  public Hash hash() {
    return record.hash();
  }

  @Override
  public Hash dataHash() {
    return record.dataHash();
  }

  @Override
  public Spec spec() {
    return record.spec();
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
    return valueToString() + ":" + hash();
  }

  @Override
  public String valueToString() {
    return name();
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
