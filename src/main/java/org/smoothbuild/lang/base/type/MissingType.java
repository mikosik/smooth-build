package org.smoothbuild.lang.base.type;

import java.util.List;
import java.util.Optional;

public class MissingType extends IType {
  private static final String NAME = "--Missing--";

  protected MissingType() {
    super(NAME);
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
  public IType superType() {
    throw new UnsupportedOperationException();
  }

  @Override
  public IType coreType() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int coreDepth() {
    throw new UnsupportedOperationException();
  }

  @Override
  public IType changeCoreDepthBy(int coreDepth) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T extends IType> T replaceCoreType(T coreType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<? extends IType> hierarchy() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isAssignableFrom(IType type) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isParamAssignableFrom(IType type) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Optional<IType> commonSuperType(IType that) {
    throw new UnsupportedOperationException();
  }
}
