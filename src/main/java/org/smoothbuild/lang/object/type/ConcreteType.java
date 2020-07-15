package org.smoothbuild.lang.object.type;

import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.base.MerkleRoot;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.SObjectImpl;
import org.smoothbuild.lang.object.db.ObjectDb;

/**
 * Concrete type in smooth language.
 *
 * This class is immutable.
 */
public abstract class ConcreteType extends AbstractType implements SObject {
  private final SObjectImpl object;
  protected final HashedDb hashedDb;
  protected final ObjectDb objectDb;

  protected ConcreteType(MerkleRoot merkleRoot, TypeKind kind, Class<? extends SObject> jType,
      HashedDb hashedDb, ObjectDb objectDb) {
    super(kind, jType);
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
  public ConcreteType type() {
    return object.type();
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof ConcreteType
        && Objects.equals(hash(), ((ConcreteType) object).hash());
  }

  @Override
  public int hashCode() {
    return hash().hashCode();
  }

  @Override
  public String toString() {
    return kind().name() + ":" + hash();
  }
}
