package org.smoothbuild.compile.lang.base;

public class ExprInfoImpl extends WithLocImpl implements ExprInfo {
  private final String description;

  public ExprInfoImpl(String description, Loc loc) {
    super(loc);
    this.description = description;
  }

  @Override
  public String label() {
    return description;
  }
}
