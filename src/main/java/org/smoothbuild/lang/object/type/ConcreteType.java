package org.smoothbuild.lang.object.type;

import java.util.List;
import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.base.MerkleRoot;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.SObjectImpl;
import org.smoothbuild.lang.object.db.ObjectsDb;

/**
 * Concrete type in smooth language.
 */
public abstract class ConcreteType extends AbstractType implements SObject {
  private final SObjectImpl object;
  protected final HashedDb hashedDb;
  private final ObjectsDb objectsDb;

  protected ConcreteType(MerkleRoot merkleRoot, ConcreteType superType,
      String name, Class<? extends SObject> jType, HashedDb hashedDb, ObjectsDb objectsDb) {
    super(superType, name, jType);
    this.object = new SObjectImpl(merkleRoot, hashedDb);
    this.hashedDb = hashedDb;
    this.objectsDb = objectsDb;
  }

  public abstract SObject newSObject(MerkleRoot merkleRoot);

  @Override
  public ConcreteType superType() {
    return (ConcreteType) super.superType();
  }

  @Override
  public ConcreteType coreType() {
    return this;
  }

  @Override
  public ConcreteType changeCoreDepthBy(int delta) {
    if (delta < 0) {
      throw new IllegalArgumentException(
          "It's not possible to reduce core depth of non array type.");
    }
    ConcreteType result = this;
    for (int i = 0; i < delta; i++) {
      result = objectsDb.arrayType(result);
    }
    return result;
  }

  @Override
  public boolean isGeneric() {
    return false;
  }

  @Override
  public List<ConcreteType> hierarchy() {
    return (List<ConcreteType>) super.hierarchy();
  }

  @Override
  public boolean isAssignableFrom(Type type) {
    if (type.isGeneric()) {
      return false;
    }
    if (this.equals(type)) {
      return true;
    }
    if (type.isNothing()) {
      return true;
    }
    if (this instanceof ConcreteArrayType && type instanceof ConcreteArrayType) {
      ConcreteType thisElemType = ((ConcreteArrayType) this).elemType();
      ConcreteType thatElemType = ((ConcreteArrayType) type).elemType();
      return thisElemType.isAssignableFrom(thatElemType);
    }
    if (type instanceof StructType) {
      return isAssignableFrom(((StructType) type).superType());
    }
    return false;
  }

  @Override
  public boolean isParamAssignableFrom(Type type) {
    return isAssignableFrom(type);
  }

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
    return object instanceof ConcreteType && equals((ConcreteType) object);
  }

  private boolean equals(ConcreteType type) {
    return Objects.equals(hash(), type.hash());
  }

  @Override
  public int hashCode() {
    return hash().hashCode();
  }

  @Override
  public String toString() {
    return type().name() + "(\"" + name() + "\"):" + hash();
  }
}
