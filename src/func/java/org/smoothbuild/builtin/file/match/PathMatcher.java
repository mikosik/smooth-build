package org.smoothbuild.builtin.file.match;

import static org.smoothbuild.builtin.file.match.Constants.SINGLE_STAR_CHAR;
import static org.smoothbuild.builtin.file.match.NamePattern.namePattern;
import static org.smoothbuild.io.fs.base.Path.path;

import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.builtin.util.Predicate;
import org.smoothbuild.builtin.util.Predicates;
import org.smoothbuild.io.fs.base.Path;

public class PathMatcher implements Predicate<Path> {
  private static final Predicate<Path> DOUBLE_STAR_PREDICATE = null;
  private final List<Predicate<Path>> patternParts;

  public static Predicate<Path> pathMatcher(String patternString) {
    PathPattern pathPattern = PathPattern.pathPattern(patternString);

    if (patternString.indexOf(SINGLE_STAR_CHAR) == -1) {
      return Predicates.equalTo(path(patternString));
    }

    if (patternString.equals("**")) {
      return Predicates.alwaysTrue();
    }
    if (patternString.equals("*")) {
      return hasOnlyOnePartPredicate();
    }

    return new PathMatcher(pathPattern);
  }

  public PathMatcher(PathPattern pattern) {
    this.patternParts = toParts(pattern);
  }

  @Override
  public boolean test(Path path) {
    List<Path> pathParts = path.parts();

    // matching leftmost path parts to first "**"

    int patternLeft = 0;
    int pathLeft = 0;

    while (patternLeft < patternParts.size()) {
      Predicate<Path> patternPart = patternParts.get(patternLeft);
      if (patternPart == DOUBLE_STAR_PREDICATE) {
        break;
      } else if (pathLeft == pathParts.size()) {
        return false;
      } else if (patternPart.test(pathParts.get(pathLeft))) {
        pathLeft++;
        patternLeft++;
      } else {
        return false;
      }
    }

    if (patternLeft == patternParts.size()) {
      return pathLeft == pathParts.size();
    }

    if (pathLeft == pathParts.size()) {
      return false;
    }

    // matching rightmost path parts to last "**"

    int patternRight = patternParts.size() - 1;
    int pathRight = pathParts.size() - 1;

    while (patternLeft < patternRight) {
      Predicate<Path> patternPart = patternParts.get(patternRight);
      if (patternPart == DOUBLE_STAR_PREDICATE) {
        break;
      } else if (pathRight < pathLeft) {
        return false;
      } else if (patternPart.test(pathParts.get(pathRight))) {
        patternRight--;
        pathRight--;
      } else {
        return false;
      }
    }

    /*
     * At this point pLeft and pRight points to parts that contain SINGLE_STAR.
     * If they both point to the same part then we've matched the whole pattern
     * - matching is successful if sRight and sLeft haven't overlapped. All
     * these cases are checked inside 'while' clause below.
     */

    int stillToMatch = minimalPartsNeeded(patternLeft + 1, patternRight - 1);
    int stillAvailable = pathRight - pathLeft + 1;

    while (true) {
      if (stillAvailable < stillToMatch) {
        return false;
      }
      if (patternLeft == patternRight) {
        return true;
      }
      patternLeft++;
      int partsCount = patternPartsCountToNextDoubleStar(patternLeft);
      int steps = stillAvailable - stillToMatch + 1;

      boolean found = false;

      OUT: for (int i = 0; i < steps; i++) {
        for (int j = 0; j < partsCount; j++) {
          Predicate<Path> patternPart = patternParts.get(patternLeft + j);
          Path pathPart = pathParts.get(pathLeft + i + j);
          if (!patternPart.test(pathPart)) {
            continue OUT;
          }
        }
        found = true;
        patternLeft += partsCount;
        pathLeft += i + partsCount;
        break;
      }
      if (!found) {
        return false;
      }
    }
  }

  private int patternPartsCountToNextDoubleStar(int index) {
    int result = 0;
    while (patternParts.get(index) != DOUBLE_STAR_PREDICATE) {
      result++;
      index++;
    }
    return result;
  }

  private int minimalPartsNeeded(int fromIndex, int toIndex) {
    int result = 0;
    for (int i = fromIndex; i <= toIndex; i++) {
      Predicate<Path> part = patternParts.get(i);
      if (part != DOUBLE_STAR_PREDICATE) {
        result++;
      }
    }
    return result;
  }

  private static List<Predicate<Path>> toParts(PathPattern pattern) {
    List<Predicate<Path>> result = new ArrayList<>();
    Predicate<Path> last = null;
    for (String part : pattern.parts()) {
      last = elementPatternToMatcher(namePattern(part));
      result.add(last);
    }

    // If last part == "**" we have to add "*" at the end.
    // This way pattern "abc/**" won't match "abc" file.
    if (last == DOUBLE_STAR_PREDICATE) {
      result.add(Predicates.<Path> alwaysTrue());
    }
    return result;
  }

  private static Predicate<Path> elementPatternToMatcher(NamePattern namePattern) {
    if (!namePattern.hasStars()) {
      return Predicates.equalTo(path(namePattern.value()));
    }

    if (namePattern.isDoubleStar()) {
      return DOUBLE_STAR_PREDICATE;
    }
    if (namePattern.isSingleStar()) {
      return Predicates.alwaysTrue();
    }

    return new NameMatcher(namePattern);
  }

  public static Predicate<Path> hasOnlyOnePartPredicate() {
    return new Predicate<Path>() {
      @Override
      public boolean test(Path path) {
        return path.isRoot() || path.value().indexOf(Path.SEPARATOR) == -1;
      }
    };
  }
}
