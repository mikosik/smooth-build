package org.smoothbuild.fs.base.match;

import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.fs.base.match.Constants.SINGLE_STAR_CHAR;
import static org.smoothbuild.fs.base.match.NamePattern.namePattern;
import static org.smoothbuild.fs.base.match.PathPredicates.doubleStarPredicate;

import org.smoothbuild.fs.base.Path;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

public class PathMatcher implements Predicate<Path> {
  private final ImmutableList<Predicate<Path>> patternParts;

  public static Predicate<Path> pathMatcher(String patternString) {
    PathPattern pathPattern = PathPattern.pathPattern(patternString);

    if (patternString.indexOf(SINGLE_STAR_CHAR) == -1) {
      return PathPredicates.isEqual(path(patternString));
    }

    if (patternString.equals("**")) {
      return PathPredicates.alwaysTrue();
    }
    if (patternString.equals("*")) {
      return PathPredicates.hasOnlyOnePart();
    }

    return new PathMatcher(pathPattern);
  }

  public PathMatcher(PathPattern pattern) {
    this.patternParts = toParts(pattern);
  }

  @Override
  public boolean apply(Path path) {
    ImmutableList<Path> pathParts = path.parts();

    // matching leftmost path parts to first "**"

    int patternLeft = 0;
    int pathLeft = 0;

    while (patternLeft < patternParts.size()) {
      Predicate<Path> patternPart = patternParts.get(patternLeft);
      if (patternPart == doubleStarPredicate()) {
        break;
      } else if (pathLeft == pathParts.size()) {
        return false;
      } else if (patternPart.apply(pathParts.get(pathLeft))) {
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
      if (patternPart == doubleStarPredicate()) {
        break;
      } else if (pathRight < pathLeft) {
        return false;
      } else if (patternPart.apply(pathParts.get(pathRight))) {
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
          if (!patternPart.apply(pathPart)) {
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
    while (patternParts.get(index) != doubleStarPredicate()) {
      result++;
      index++;
    }
    return result;
  }

  private int minimalPartsNeeded(int fromIndex, int toIndex) {
    int result = 0;
    for (int i = fromIndex; i <= toIndex; i++) {
      Predicate<Path> part = patternParts.get(i);
      if (part != doubleStarPredicate()) {
        result++;
      }
    }
    return result;
  }

  private static ImmutableList<Predicate<Path>> toParts(PathPattern pattern) {
    ImmutableList.Builder<Predicate<Path>> builder = ImmutableList.builder();
    Predicate<Path> last = null;
    for (String part : pattern.parts()) {
      last = elementPatternToMatcher(namePattern(part));
      builder.add(last);
    }

    // If last part == "**" we have to add "*" at the end.
    // This way pattern "abc/**" won't match "abc" file.
    if (last == doubleStarPredicate()) {
      builder.add(PathPredicates.alwaysTrue());
    }
    return builder.build();
  }

  private static Predicate<Path> elementPatternToMatcher(NamePattern namePattern) {
    if (!namePattern.hasStars()) {
      return PathPredicates.isEqual(path(namePattern.value()));
    }

    if (namePattern.isDoubleStar()) {
      return doubleStarPredicate();
    }
    if (namePattern.isSingleStar()) {
      return PathPredicates.alwaysTrue();
    }

    return new NameMatcher(namePattern);
  }
}
