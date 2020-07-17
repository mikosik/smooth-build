package org.smoothbuild.lang.base.type;

import static com.google.common.collect.Lists.reverse;
import static org.smoothbuild.lang.base.type.Types.nothing;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.property.TypeProperties;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.parse.ast.Named;

import com.google.common.collect.ImmutableList;

public abstract class Type implements Named {
  private final String name;
  private final Location location;
  protected final TypeProperties properties;
  private ImmutableList<Type> hierarchy;

  protected Type(String name, Location location, TypeProperties properties) {
    this.name = name;
    this.location = location;
    this.properties = properties;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Location location() {
    return location;
  }

  public String q() {
    return "'" + name + "'";
  }

  public abstract boolean isGeneric();

  public boolean isArray() {
    return properties.isArray();
  }

  public boolean isNothing() {
    return this == nothing();
  }

  public abstract Type superType();

  public Type coreType() {
    return properties.coreType(this);
  }

  public <T extends Type> T replaceCoreType(T coreType) {
    @SuppressWarnings("unchecked")
    T result = (T) coreType.changeCoreDepthBy(coreDepth());
    return result;
  }

  public int coreDepth() {
    return properties.coreDepth(this);
  }

  public Type changeCoreDepthBy(int delta) {
    return properties.changeCoreDepthBy(this, delta);
  }

  public List<? extends Type> hierarchy() {
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
      return ImmutableList.<Type>builder()
          .addAll(superType().hierarchy())
          .add(this)
          .build();
    }
  }

  public abstract boolean isAssignableFrom(Type type);

  public abstract boolean isParamAssignableFrom(Type type);

  public Optional<Type> commonSuperType(Type that) {
    /*
     * Algorithm below works correctly for all smooth types currently existing in smooth because it
     * is not possible to define recursive struct types. It will fail when conversion chain
     * (hierarchy) contains cycle (for example struct type is convertible to itself) or conversion
     * chain has infinite length (for example structure X is convertible to its array [X]).
     */

    List<? extends Type> hierarchy1 = this.hierarchy();
    List<? extends Type> hierarchy2 = that.hierarchy();
    Optional<Type> type = closestCommonSuperType(hierarchy1, hierarchy2);
    if (type.isEmpty()) {
      Type last1 = hierarchy1.get(0);
      Type last2 = hierarchy2.get(0);
      Type last1Core = last1.coreType();
      Type last2Core = last2.coreType();
      boolean isNothing1 = last1Core.isNothing();
      boolean isNothing2 = last2Core.isNothing();
      if (isNothing1 && isNothing2) {
        type = Optional.of(last1.coreDepth() < last2.coreDepth() ? last2 : last1);
      } else if (isNothing1) {
        type = firstWithDepthNotLowerThan(hierarchy2, last1.coreDepth());
      } else if (isNothing2) {
        type = firstWithDepthNotLowerThan(hierarchy1, last2.coreDepth());
      }
    }
    return type;
  }

  private static Optional<Type> closestCommonSuperType(List<? extends Type> hierarchy1,
      List<? extends Type> hierarchy2) {
    int index = 0;
    Type type = null;
    while (index < hierarchy1.size() && index < hierarchy2.size()
        && hierarchy1.get(index).equals(hierarchy2.get(index))) {
      type = hierarchy1.get(index);
      index++;
    }
    return Optional.ofNullable(type);
  }

  private static Optional<Type> firstWithDepthNotLowerThan(
      List<? extends Type> hierarchy, int depth) {
    return reverse(hierarchy)
        .stream()
        .map(t -> (Type) t)
        .filter(t -> depth <= t.coreDepth())
        .findFirst();
  }

  public Class<? extends SObject> jType() {
    return properties.jType();
  }

  public abstract <T> T visit(TypeVisitor<T> visitor);

  @Override
  public boolean equals(Object o) {
    return properties.areEqual(this, o);
  }

  @Override
  public int hashCode() {
    return properties.hashCode(this);
  }

  @Override
  public String toString() {
    return "Type(\"" + name() + "\")";
  }
}
