package org.smoothbuild.builtin.file.match;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.fail;
import static org.smoothbuild.builtin.file.match.PathPattern.pathPattern;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class PathPatternTest {
  private PathPattern pattern;

  @Test
  public void single_star_pattern_has_one_part() throws Exception {
    given(pattern = pathPattern("**"));
    when(pattern.parts());
    thenReturned(contains("**"));
  }

  @Test
  public void double_star_pattern_has_one_part() throws Exception {
    given(pattern = pathPattern("**"));
    when(pattern.parts());
    thenReturned(contains("**"));
  }

  @Test
  public void multi_part_patern_has_many_parts() throws Exception {
    given(pattern = pathPattern("a/b/c"));
    when(pattern.parts());
    thenReturned(contains("a", "b", "c"));
  }

  @Test
  public void validationErrorReturnsMessageForInvalidPattern() {
    for (String pattern : listOfInvalidPatterns()) {
      try {
        PathPattern.pathPattern(pattern);
        fail("exception should be thrown");
      } catch (IllegalPathPatternException e) {
        // expected
      }
    }
  }

  private static List<String> listOfInvalidPatterns() {
    List<String> result = new ArrayList<>();

    result.add("");

    result.add(".");
    result.add("./");
    result.add("/.");
    result.add("./.");
    result.add("././");

    result.add("abc/");
    result.add("abc/def/");
    result.add("abc/def/ghi/");

    result.add("./abc");
    result.add("./abc/def");
    result.add("./abc/def/ghi");
    result.add("./abc/def/ghi/ijk");

    result.add("abc/.");
    result.add("abc/def/.");
    result.add("abc/def/ghi/.");
    result.add("abc/def/ghi/ijk/.");

    result.add("..");
    result.add("../");
    result.add("./../");
    result.add("../abc");
    result.add("abc/..");
    result.add("abc/../def");
    result.add("../..");

    result.add("/");
    result.add("//");
    result.add("///");

    result.add("/abc");
    result.add("//abc");
    result.add("///abc");

    result.add("abc//");
    result.add("abc///");

    result.add("abc//def");
    result.add("abc///def");

    result.add("*/");
    result.add("/*");

    result.add("**/");
    result.add("/**");

    result.add("a**");
    result.add("**a");
    result.add("a/b**/c");
    result.add("a/**b/c");

    result.add("**/**");
    result.add("a/**/**");
    result.add("**/**/b");
    result.add("a/**/**/b");

    result.add("***");

    return result;
  }
}
