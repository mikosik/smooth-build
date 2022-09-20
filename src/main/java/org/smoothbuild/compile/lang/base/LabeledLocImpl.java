package org.smoothbuild.compile.lang.base;

public class LabeledLocImpl extends WithLocImpl implements LabeledLoc {
  private final String label;

  public LabeledLocImpl(String label, Loc loc) {
    super(loc);
    this.label = label;
  }

  @Override
  public String label() {
    return label;
  }
}
