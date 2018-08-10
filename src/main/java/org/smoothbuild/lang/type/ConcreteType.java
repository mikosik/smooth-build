package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.Objects;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.value.AbstractValue;
import org.smoothbuild.lang.value.Value;

import com.google.common.hash.HashCode;

/**
 * Concrete type in smooth language.
 */
public abstract class ConcreteType extends AbstractType implements Value {
  private final AbstractValue value;
  protected final HashedDb hashedDb;
  private final TypesDb typesDb;

  protected ConcreteType(HashCode dataHash, TypeType type, ConcreteType superType, String name,
      Class<? extends Value> jType, HashedDb hashedDb, TypesDb typesDb) {
    super(superType, name, jType);
    this.value = new AbstractValue(dataHash, type, hashedDb) {};
    this.hashedDb = hashedDb;
    this.typesDb = typesDb;
  }

  protected ConcreteType(HashCode hash, HashCode dataHash, TypeType type, ConcreteType superType,
      String name, Class<? extends Value> jType, HashedDb hashedDb, TypesDb typesDb) {
    super(superType, name, jType);
    this.value = new AbstractValue(hash, dataHash, type, hashedDb) {};
    this.hashedDb = hashedDb;
    this.typesDb = typesDb;
  }

  public abstract Value newValue(HashCode dataHash);

  @Override
  public ConcreteType superType() {
    return (ConcreteType) super.superType();
  }

  @Override
  public ConcreteType coreType() {
    return this;
  }

  @Override
  public ConcreteType increaseCoreDepthBy(int delta) {
    checkArgument(0 <= delta, "delta must be non negative value");
    ConcreteType result = this;
    for (int i = 0; i < delta; i++) {
      result = typesDb.array(result);
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
  public boolean isArgAssignableFrom(Type type) {
    return isAssignableFrom(type);
  }

  @Override
  public HashCode hash() {
    return value.hash();
  }

  @Override
  public HashCode dataHash() {
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
