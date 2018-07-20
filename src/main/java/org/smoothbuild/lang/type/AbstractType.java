package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.TypeNames.NOTHING;

import java.util.List;

import org.smoothbuild.lang.value.Value;

import com.google.common.collect.ImmutableList;

public abstract class AbstractType implements Type {
  private final Type superType;
  private final String name;
  private final Class<? extends Value> jType;
  private ImmutableList<Type> hierarchy;

  public AbstractType(Type superType, String name, Class<? extends Value> jType) {
    this.superType = superType;
    this.name = name;
    this.jType = jType;
  }

  @Override
  public Type superType() {
    return superType;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Class<? extends Value> jType() {
    return jType;
  }

  @Override
  public int coreDepth() {
    return 0;
  }

  @Override
  public boolean isArray() {
    return this instanceof ArrayType;
  }

  @Override
  public boolean isNothing() {
    return name.equals(NOTHING);
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
      return ImmutableList.<Type> builder()
          .addAll(superType().hierarchy())
          .add(this)
          .build();
    }
  }
}
