package org.smoothbuild.lang.object.type;

import static org.smoothbuild.lang.object.type.TypeKind.NOTHING;

import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.base.MerkleRoot;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.SObjectImpl;
import org.smoothbuild.lang.object.db.ObjectDb;

public abstract class BinaryType implements SObject {
  private final TypeKind kind;
  private final SObjectImpl object;
  protected final HashedDb hashedDb;
  protected final ObjectDb objectDb;

  protected BinaryType(MerkleRoot merkleRoot, TypeKind kind, HashedDb hashedDb,
      ObjectDb objectDb) {
    this.kind = kind;
    this.object = new SObjectImpl(merkleRoot, hashedDb);
    this.hashedDb = hashedDb;
    this.objectDb = objectDb;
  }

  /**
   * Creates new java object representing Binary Object represented by merkleRoot.
   */
  public abstract SObject newJObject(MerkleRoot merkleRoot);

  @Override
  public Hash hash() {
    return object.hash();
  }

  @Override
  public Hash dataHash() {
    return object.dataHash();
  }

  @Override
  public BinaryType type() {
    return object.type();
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof BinaryType
        && Objects.equals(hash(), ((BinaryType) object).hash());
  }

  @Override
  public int hashCode() {
    return hash().hashCode();
  }

  @Override
  public String toString() {
    return kind().name() + ":" + hash();
  }

  public String name() {
    return kind.name();
  }

  public TypeKind kind() {
    return kind;
  }

  public Class<? extends SObject> jType() {
    return kind.jType();
  }

  public boolean isArray() {
    return this instanceof ArrayType;
  }

  public boolean isNothing() {
    return kind == NOTHING;
  }
}
