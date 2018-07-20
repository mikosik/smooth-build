package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.reverse;
import static org.smoothbuild.lang.type.TypeNames.NOTHING;

import java.util.List;
import java.util.Objects;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.value.AbstractValue;
import org.smoothbuild.lang.value.Value;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

/**
 * Concrete type in smooth language.
 */
public abstract class ConcreteType implements Value {
  private final AbstractValue value;
  private final ConcreteType superType;
  private final String name;
  private final Class<? extends Value> jType;
  private ImmutableList<ConcreteType> hierarchy;
  protected final HashedDb hashedDb;

  protected ConcreteType(HashCode dataHash, TypeType type, ConcreteType superType, String name,
      Class<? extends Value> jType, HashedDb hashedDb) {
    this.value = new AbstractValue(dataHash, type, hashedDb) {};
    this.superType = superType;
    this.name = checkNotNull(name);
    this.jType = checkNotNull(jType);
    this.hashedDb = hashedDb;
  }

  protected ConcreteType(HashCode hash, HashCode dataHash, TypeType type, ConcreteType superType,
      String name, Class<? extends Value> jType, HashedDb hashedDb) {
    this.value = new AbstractValue(hash, dataHash, type, hashedDb) {};
    this.superType = superType;
    this.name = name;
    this.jType = jType;
    this.hashedDb = hashedDb;
  }

  public String name() {
    return name;
  }

  public Class<? extends Value> jType() {
    return jType;
  }

  public abstract Value newValue(HashCode dataHash);

  public ConcreteType coreType() {
    return this;
  }

  public int coreDepth() {
    return 0;
  }

  public boolean isArray() {
    return this instanceof ArrayType;
  }

  public List<ConcreteType> hierarchy() {
    ImmutableList<ConcreteType> h = hierarchy;
    if (h == null) {
      h = calculateHierarchy();
      hierarchy = h;
    }
    return h;
  }

  private ImmutableList<ConcreteType> calculateHierarchy() {
    if (superType() == null) {
      return ImmutableList.of(this);
    } else {
      return ImmutableList.<ConcreteType> builder()
          .addAll(superType().hierarchy())
          .add(this)
          .build();
    }
  }

  public ConcreteType superType() {
    return superType;
  }

  public boolean isNothing() {
    return name.equals(NOTHING);
  }

  public boolean isAssignableFrom(ConcreteType type) {
    if (this.equals(type)) {
      return true;
    }
    if (type.isNothing()) {
      return true;
    }
    if (this instanceof ArrayType && type instanceof ArrayType) {
      return ((ArrayType) this).elemType().isAssignableFrom(((ArrayType) type).elemType());
    }
    if (type instanceof StructType) {
      return isAssignableFrom(((StructType) type).superType());
    }
    return false;
  }

  public ConcreteType commonSuperType(ConcreteType that) {
    /*
     * Algorithm below works correctly for all smooth types currently existing in smooth because it
     * is not possible to define recursive struct types. It will fail when conversion chain
     * (hierarchy) contains cycle (for example struct type is convertible to itself) or conversion
     * chain has infinite length (for example structure X is convertible to its array [X]).
     */
    List<ConcreteType> hierarchy1 = this.hierarchy();
    List<ConcreteType> hierarchy2 = that.hierarchy();
    ConcreteType type = closestCommonSuperType(hierarchy1, hierarchy2);
    if (type == null) {
      ConcreteType last1 = hierarchy1.get(0);
      ConcreteType last2 = hierarchy2.get(0);
      ConcreteType last1Core = last1.coreType();
      ConcreteType last2Core = last2.coreType();
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

  private static ConcreteType closestCommonSuperType(List<ConcreteType> hierarchy1,
      List<ConcreteType> hierarchy2) {
    int index = 0;
    ConcreteType type = null;
    while (index < hierarchy1.size() && index < hierarchy2.size()
        && hierarchy1.get(index).equals(hierarchy2.get(index))) {
      type = hierarchy1.get(index);
      index++;
    }
    return type;
  }

  private static ConcreteType firstWithDepthNotLowerThan(List<ConcreteType> hierarchy, int depth) {
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
    return type().name + "(\"" + name + "\"):" + hash();
  }
}
