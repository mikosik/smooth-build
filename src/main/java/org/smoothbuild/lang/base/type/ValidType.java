package org.smoothbuild.lang.base.type;

import static com.google.common.collect.Lists.reverse;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.object.db.ObjectFactory;

import com.google.common.collect.ImmutableList;

public abstract class ValidType extends Type {
  private ImmutableList<Type> hierarchy;

  protected ValidType(String name) {
    super(name);
  }

  @Override
  public boolean isArray() {
    return false;
  }

  @Override
  public boolean isNothing() {
    return false;
  }

  public abstract org.smoothbuild.lang.object.type.Type toRecordType(ObjectFactory objectFactory);

  @Override
  public <T extends Type> T replaceCoreType(T coreType) {
    return coreType;
  }

  @Override
  public int coreDepth() {
    return 0;
  }

  @Override
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

  @Override
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
}
