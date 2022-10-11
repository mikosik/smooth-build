package org.smoothbuild.compile.lang.base;

/**
 * Stack trace.
 */
public record Trace(LabeledLoc labeledLoc, Trace chain) {
  public Trace(String label, Loc loc) {
    this(label, loc, null);
  }

  public Trace(String label, Loc loc, Trace previous) {
    this(new LabeledLocImpl(label, loc), previous);
  }

  @Override
  public String toString() {
    var line = labeledLoc.label() + " @" + labeledLoc.loc();
    if (chain == null) {
      return line;
    } else {
      return line + "\n" + chain;
    }
  }
}
