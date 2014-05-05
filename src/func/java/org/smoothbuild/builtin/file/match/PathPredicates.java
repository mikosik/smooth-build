package org.smoothbuild.builtin.file.match;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.io.fs.base.Path;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class PathPredicates {

  public static Predicate<Path> alwaysTrue() {
    return Predicates.alwaysTrue();
  }

  public static Predicate<Path> isEqual(final Path path) {
    checkNotNull(path);

    return new Predicate<Path>() {
      @Override
      public boolean apply(Path input) {
        return Objects.equal(input, path);
      }
    };
  }

  public static Predicate<Path> hasOnlyOnePart() {
    return HAS_ONLY_ONE_PART;
  }

  private static final HasOnlyOnePart HAS_ONLY_ONE_PART = new HasOnlyOnePart();

  private static final class HasOnlyOnePart implements Predicate<Path> {
    @Override
    public boolean apply(Path input) {
      return input.value().indexOf(Path.SEPARATOR_CHARACTER) == -1;
    }
  }

  public static Predicate<Path> doubleStarPredicate() {
    return DOUBLE_STAR_PREDICATE;
  }

  private static final Predicate<Path> DOUBLE_STAR_PREDICATE = new DoubleStarPredicate();

  private static class DoubleStarPredicate implements Predicate<Path> {
    @Override
    public boolean apply(Path input) {
      throw new UnsupportedOperationException();
    }
  }
}
