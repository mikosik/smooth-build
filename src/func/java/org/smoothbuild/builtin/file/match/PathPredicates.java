package org.smoothbuild.builtin.file.match;

import org.smoothbuild.builtin.util.Predicate;
import org.smoothbuild.io.fs.base.Path;

public class PathPredicates {

  public static Predicate<Path> hasOnlyOnePart() {
    return HAS_ONLY_ONE_PART;
  }

  private static final HasOnlyOnePart HAS_ONLY_ONE_PART = new HasOnlyOnePart();

  private static final class HasOnlyOnePart implements Predicate<Path> {
    @Override
    public boolean test(Path input) {
      return input.isRoot() || input.value().indexOf(Path.SEPARATOR) == -1;
    }
  }

  public static Predicate<Path> doubleStarPredicate() {
    return DOUBLE_STAR_PREDICATE;
  }

  private static final Predicate<Path> DOUBLE_STAR_PREDICATE = new DoubleStarPredicate();

  private static class DoubleStarPredicate implements Predicate<Path> {
    @Override
    public boolean test(Path input) {
      throw new UnsupportedOperationException();
    }
  }
}
