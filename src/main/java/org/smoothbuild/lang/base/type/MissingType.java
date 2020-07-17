package org.smoothbuild.lang.base.type;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.type.compound.BasicProperties;
import org.smoothbuild.lang.object.base.SObject;

public class MissingType extends Type {
  private static final String NAME = "--Missing--";

  protected MissingType() {
    super(NAME, null, new BasicProperties(SObject.class));
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
  public Type superType() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Type coreType() {
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
  public <T extends Type> T replaceCoreType(T coreType) {
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
  public Optional<Type> commonSuperType(Type that) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    throw new UnsupportedOperationException();
  }
}
