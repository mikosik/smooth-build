package org.smoothbuild.lang.type;

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

  protected ConcreteType(HashCode dataHash, TypeType type, ConcreteType superType, String name,
      Class<? extends Value> jType, HashedDb hashedDb) {
    super(superType, name, jType);
    this.value = new AbstractValue(dataHash, type, hashedDb) {};
    this.hashedDb = hashedDb;
  }

  protected ConcreteType(HashCode hash, HashCode dataHash, TypeType type, ConcreteType superType,
      String name, Class<? extends Value> jType, HashedDb hashedDb) {
    super(superType, name, jType);
    this.value = new AbstractValue(hash, dataHash, type, hashedDb) {};
    this.hashedDb = hashedDb;
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
  public List<ConcreteType> hierarchy() {
    return (List<ConcreteType>) super.hierarchy();
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
