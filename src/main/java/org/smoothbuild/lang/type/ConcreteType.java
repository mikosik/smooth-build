package org.smoothbuild.lang.type;

import java.util.List;
import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.value.AbstractValue;
import org.smoothbuild.lang.value.Value;

/**
 * Concrete type in smooth language.
 */
public abstract class ConcreteType extends AbstractType implements Value {
  private final AbstractValue value;
  protected final HashedDb hashedDb;
  private final ValuesDb valuesDb;

  protected ConcreteType(Hash dataHash, TypeType type, ConcreteType superType, String name,
      Class<? extends Value> jType, HashedDb hashedDb, ValuesDb valuesDb) {
    super(superType, name, jType);
    this.value = new AbstractValue(dataHash, type, hashedDb) {};
    this.hashedDb = hashedDb;
    this.valuesDb = valuesDb;
  }

  protected ConcreteType(Hash hash, Hash dataHash, TypeType type, ConcreteType superType,
      String name, Class<? extends Value> jType, HashedDb hashedDb, ValuesDb valuesDb) {
    super(superType, name, jType);
    this.value = new AbstractValue(hash, dataHash, type, hashedDb) {};
    this.hashedDb = hashedDb;
    this.valuesDb = valuesDb;
  }

  public abstract Value newValue(Hash dataHash);

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
      result = valuesDb.arrayType(result);
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
    return value.hash();
  }

  @Override
  public Hash dataHash() {
    return value.dataHash();
  }

  @Override
  public ConcreteType type() {
    return value.type();
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof ConcreteType && equals((ConcreteType) object);
  }

  private boolean equals(ConcreteType value) {
    return Objects.equals(hash(), value.hash());
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
