package org.smoothbuild.builtin.file.match;

import static org.smoothbuild.builtin.file.match.Constants.SINGLE_STAR;
import static org.smoothbuild.builtin.util.Utils.checkArgument;

import java.util.List;

import org.smoothbuild.builtin.util.Predicate;
import org.smoothbuild.io.fs.base.Path;

/**
 * Matches file/directory name against given pattern.
 *
 * <pre>
 * Each star '*' in pattern matches zero or more characters.
 * Double stars "**" are forbidden.
 * </pre>
 */
public class NameMatcher implements Predicate<Path> {
  private final List<String> patternParts;

  public NameMatcher(NamePattern pattern) {
    checkArgument(!pattern.isDoubleStar());
    this.patternParts = pattern.parts();
  }

  @Override
  public boolean test(Path path) {
    String string = path.value();

    // matching leftmost characters to first '*'

    int pLeft = 0;
    int sLeft = 0;

    while (pLeft < patternParts.size()) {
      String part = patternParts.get(pLeft);
      if (part.equals(SINGLE_STAR)) {
        break;
      } else if (string.substring(sLeft).startsWith(part)) {
        sLeft += part.length();
        pLeft++;
      } else {
        return false;
      }
    }

    if (pLeft == patternParts.size()) {
      return sLeft == string.length();
    }

    // matching rightmost characters to last '*'

    int pRight = patternParts.size() - 1;
    int sRight = string.length();

    while (pLeft < pRight) {
      String part = patternParts.get(pRight);
      if (part.equals(SINGLE_STAR)) {
        break;
      } else if (string.substring(0, sRight).endsWith(part)) {
        sRight -= part.length();
        pRight--;
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

    int stillToMatch = minimumCharactersNeeded(pLeft + 1, pRight - 1);
    int stillAvailable = sRight - sLeft;

    while (true) {
      if (stillAvailable < stillToMatch) {
        return false;
      }
      if (pLeft == pRight) {
        return true;
      }
      pLeft++;
      String part = patternParts.get(pLeft);
      int steps = stillAvailable - part.length() + 1;

      boolean found = false;
      for (int i = 0; i < steps; i++) {
        if (string.substring(sLeft + i).startsWith(part)) {
          pLeft++;
          sLeft += part.length() + i;
          found = true;
          break;
        }
      }
      if (!found) {
        return false;
      }
    }
  }

  private int minimumCharactersNeeded(int pLeft, int pRight) {
    int result = 0;
    for (int i = pLeft; i <= pRight; i++) {
      String part = patternParts.get(i);
      result += part.equals(SINGLE_STAR) ? 0 : part.length();
    }
    return result;
  }
}
