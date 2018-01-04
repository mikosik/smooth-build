package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.reverse;

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

  public List<Type> hierarchy() {
    ImmutableList<Type> h = hierarchy;
    if (h == null) {
      if (superType() == null) {
        h = ImmutableList.of(this);
      } else {
        h = ImmutableList.<Type> builder()
            .addAll(superType().hierarchy())
            .add(this)
            .build();
      }
      hierarchy = h;
    }
    return h;
  }

  public Type superType() {
    return superType;
  }

  public boolean isNothing() {
    return name.equals("Nothing");
  }

  public boolean isAssignableFrom(Type type) {
    if (type.isNothing()) {
      return true;
    }
    if (this.equals(type)) {
      return true;
    }
    if (type instanceof StructType) {
      return isAssignableFrom(((StructType) type).superType());
    }
    if (this instanceof ArrayType && type instanceof ArrayType) {
      return ((ArrayType) this).elemType().isAssignableFrom(((ArrayType) type).elemType());
    }
    return false;
  }

  public Type commonSuperType(Type that) {
    /*
     * Algorithm below works correctly for all smooth types currently existing in smooth but doesn't
     * work when it is possible to define user struct types. It will fail when conversion chain
     * (hierarchy) contains cycle (for example struct type is convertible to itself) or conversion
     * chain has infinite length (for example structure X is convertible to its array [X]). This
     * comment will become obsolete once we get rid of conversion chains (and direct-convertible-to)
     * and instead create normal object oriented type hierarchy with one root (Value type).
     */
    List<Type> hierarchy1 = this.hierarchy();
    List<Type> hierarchy2 = that.hierarchy();
    Type type = closesCommonSuperType(hierarchy1, hierarchy2);
    if (type == null) {
      Type last1 = hierarchy1.get(0);
      Type last2 = hierarchy2.get(0);
      boolean isNothing1 = last1.coreType().isNothing();
      boolean isNothing2 = last2.coreType().isNothing();
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

  private static Type closesCommonSuperType(List<Type> hierarchy1, List<Type> hierarchy2) {
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
    return name;
  }
}
