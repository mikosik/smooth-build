package org.smoothbuild.lang.object.type;

import java.util.List;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.object.base.SObject;

public class MissingType implements Type {
  public static final MissingType MISSING_TYPE = new MissingType();

  private MissingType() {
  }

  @Override
  public Type superType() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String name() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Location location() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String q() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Class<? extends SObject> jType() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Type coreType() {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T extends Type> T replaceCoreType(T coreType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int coreDepth() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Type changeCoreDepthBy(int delta) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isGeneric() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isArray() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isNothing() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<? extends Type> hierarchy() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isAssignableFrom(Type type) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isParamAssignableFrom(Type type) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Type commonSuperType(Type type) {
    throw new UnsupportedOperationException();
  }
}
