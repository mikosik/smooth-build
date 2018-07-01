package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.reverse;
import static java.lang.Character.isLowerCase;

import java.util.List;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.value.Value;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

/**
 * Type in smooth language.
 */
public abstract class Type extends Value {
  private final Type superType;
  private final String name;
  private final Class<? extends Value> jType;
  private ImmutableList<Type> hierarchy;

  protected Type(HashCode dataHash, TypeType type, Type superType, String name,
      Class<? extends Value> jType, HashedDb hashedDb) {
    super(dataHash, type, hashedDb);
    this.superType = superType;
    this.name = checkNotNull(name);
    this.jType = checkNotNull(jType);
  }

  protected Type(HashCode hash, HashCode dataHash, TypeType type, Type superType, String name,
      Class<? extends Value> jType, HashedDb hashedDb) {
    super(hash, dataHash, type, hashedDb);
    this.superType = superType;
    this.name = name;
    this.jType = jType;
  }

  public String name() {
    return name;
  }

  public Class<? extends Value> jType() {
    return jType;
  }

  public abstract Value newValue(HashCode dataHash);

  public Type coreType() {
    return this;
  }

  public int coreDepth() {
    return 0;
  }

  public boolean isArray() {
    return this instanceof ArrayType;
  }

  public List<Type> hierarchy() {
    ImmutableList<Type> h = hierarchy;
    if (h == null) {
      h = calculateHierarchy();
      hierarchy = h;
    }
    return h;
  }

  private ImmutableList<Type> calculateHierarchy() {
    if (superType() == null) {
      return ImmutableList.of(this);
    } else {
      return ImmutableList.<Type> builder()
          .addAll(superType().hierarchy())
          .add(this)
          .build();
    }
  }

  public Type superType() {
    return superType;
  }

  public boolean isGeneric() {
    return isLowerCase(coreType().name().charAt(0));
  }

  public boolean isAssignableFrom(Type type) {
    if (this.equals(type)) {
      return true;
    }
    if (this instanceof ArrayType && type instanceof ArrayType) {
      return ((ArrayType) this).elemType().isAssignableFrom(((ArrayType) type).elemType());
    }
    if (type instanceof ArrayType) {
      return this.isGeneric() && !this.equals(type.coreType());
    }
    if (this instanceof ArrayType) {
      return type.isGeneric() && !type.equals(this.coreType());
    }
    if (this.isGeneric() || type.isGeneric()) {
      return true;
    }
    if (type instanceof StructType) {
      return isAssignableFrom(((StructType) type).superType());
    }
    return false;
  }

  public Type commonSuperType(Type that) {
    /*
     * Algorithm below works correctly for all smooth types currently existing in smooth because it
     * is not possible to define recursive struct types. It will fail when conversion chain
     * (hierarchy) contains cycle (for example struct type is convertible to itself) or conversion
     * chain has infinite length (for example structure X is convertible to its array [X]).
     */
    List<Type> hierarchy1 = this.hierarchy();
    List<Type> hierarchy2 = that.hierarchy();
    Type type = closestCommonSuperType(hierarchy1, hierarchy2);
    if (type == null) {
      Type last1 = hierarchy1.get(0);
      Type last2 = hierarchy2.get(0);
      boolean isGeneric1 = last1.coreType().isGeneric();
      boolean isGeneric2 = last2.coreType().isGeneric();
      if (isGeneric1 && isGeneric2) {
        type = last1.coreDepth() < last2.coreDepth() ? last2 : last1;
      } else if (isGeneric1) {
        type = firstWithDepthNotLowerThan(hierarchy2, last1.coreDepth());
      } else if (isGeneric2) {
        type = firstWithDepthNotLowerThan(hierarchy1, last2.coreDepth());
      }
    }
    return type;
  }

  private static Type closestCommonSuperType(List<Type> hierarchy1, List<Type> hierarchy2) {
    int index = 0;
    Type type = null;
    while (index < hierarchy1.size() && index < hierarchy2.size()
        && hierarchy1.get(index).equals(hierarchy2.get(index))) {
      type = hierarchy1.get(index);
      index++;
    }
    return type;
  }

  private static Type firstWithDepthNotLowerThan(List<Type> hierarchy, int depth) {
    return reverse(hierarchy)
        .stream()
        .filter(t -> depth <= t.coreDepth())
        .findFirst()
        .orElse(null);
  }

  @Override
  public String toString() {
    return type().name + "(\"" + name + "\"):" + hash();
  }
}
