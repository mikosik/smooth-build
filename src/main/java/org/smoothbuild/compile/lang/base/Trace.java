package org.smoothbuild.compile.lang.base;

/**
 * Stack trace.
 */
public record Trace(TagLoc tagLoc, Trace chain) {
  public Trace(String tag, Loc loc) {
    this(tag, loc, null);
  }

  public Trace(String tag, Loc loc, Trace previous) {
    this(new TagLoc(tag, loc), previous);
  }

  @Override
  public String toString() {
    var line = tagLoc.tag() + " " + tagLoc.loc();
    if (chain == null) {
      return line;
    } else {
      return line + "\n" + chain;
    }
  }
}
