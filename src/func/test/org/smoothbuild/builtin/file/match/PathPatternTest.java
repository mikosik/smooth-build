package org.smoothbuild.builtin.file.match;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.fail;
import static org.smoothbuild.builtin.file.match.PathPattern.pathPattern;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

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

  private static ImmutableList<String> listOfInvalidPatterns() {
    Builder<String> builder = ImmutableList.builder();

    builder.add("");

    builder.add(".");
    builder.add("./");
    builder.add("/.");
    builder.add("./.");
    builder.add("././");

    builder.add("abc/");
    builder.add("abc/def/");
    builder.add("abc/def/ghi/");

    builder.add("./abc");
    builder.add("./abc/def");
    builder.add("./abc/def/ghi");
    builder.add("./abc/def/ghi/ijk");

    builder.add("abc/.");
    builder.add("abc/def/.");
    builder.add("abc/def/ghi/.");
    builder.add("abc/def/ghi/ijk/.");

    builder.add("..");
    builder.add("../");
    builder.add("./../");
    builder.add("../abc");
    builder.add("abc/..");
    builder.add("abc/../def");
    builder.add("../..");

    builder.add("/");
    builder.add("//");
    builder.add("///");

    builder.add("/abc");
    builder.add("//abc");
    builder.add("///abc");

    builder.add("abc//");
    builder.add("abc///");

    builder.add("abc//def");
    builder.add("abc///def");

    builder.add("*/");
    builder.add("/*");

    builder.add("**/");
    builder.add("/**");

    builder.add("a**");
    builder.add("**a");
    builder.add("a/b**/c");
    builder.add("a/**b/c");

    builder.add("**/**");
    builder.add("a/**/**");
    builder.add("**/**/b");
    builder.add("a/**/**/b");

    builder.add("***");

    return builder.build();
  }
}
