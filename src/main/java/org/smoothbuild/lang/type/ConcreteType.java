package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.reverse;

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
  public Type commonSuperType(Type that) {
    /*
     * Algorithm below works correctly for all smooth types currently existing in smooth because it
     * is not possible to define recursive struct types. It will fail when conversion chain
     * (hierarchy) contains cycle (for example struct type is convertible to itself) or conversion
     * chain has infinite length (for example structure X is convertible to its array [X]).
     */

    if (that.isGeneric()) {
      return null;
    }
    List<? extends Type> hierarchy1 = this.hierarchy();
    List<? extends Type> hierarchy2 = that.hierarchy();
    Type type = closestCommonSuperType(hierarchy1, hierarchy2);
    if (type == null) {
      Type last1 = hierarchy1.get(0);
      Type last2 = hierarchy2.get(0);
      Type last1Core = last1.coreType();
      Type last2Core = last2.coreType();
      boolean isNothing1 = last1Core.isNothing();
      boolean isNothing2 = last2Core.isNothing();
      if (isNothing1 && isNothing2) {
        type = last1.coreDepth() < last2.coreDepth() ? last2 : last1;
      } else if (isNothing1) {
        type = firstWithDepthNotLowerThan(hierarchy2, last1.coreDepth());
      } else if (isNothing2) {
        type = firstWithDepthNotLowerThan(hierarchy1, last2.coreDepth());
      }
    }
    return type;
  }

  private static Type closestCommonSuperType(List<? extends Type> hierarchy1,
      List<? extends Type> hierarchy2) {
    int index = 0;
    Type type = null;
    while (index < hierarchy1.size() && index < hierarchy2.size()
        && hierarchy1.get(index).equals(hierarchy2.get(index))) {
      type = hierarchy1.get(index);
      index++;
    }
    return type;
  }

  private static Type firstWithDepthNotLowerThan(List<? extends Type> hierarchy, int depth) {
    return reverse(hierarchy)
        .stream()
        .filter(t -> depth <= t.coreDepth())
        .findFirst()
        .orElse(null);
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
