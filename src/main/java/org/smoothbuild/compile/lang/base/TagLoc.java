package org.smoothbuild.compile.lang.base;

public record TagLoc(String tag, Loc loc) implements WithLoc {
  @Override
  public String toString() {
    return tag + " " + loc.toString();
  }
}
