package org.smoothbuild.builtin.file.match;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.builtin.file.match.PathPattern.pathPattern;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class PathPatternTest {
  @Test
  public void single_star_pattern_has_one_part() {
    assertThat(pathPattern("*").parts())
        .containsExactly("*");
  }

  @Test
  public void double_star_pattern_has_one_part() {
    assertThat(pathPattern("**").parts())
        .containsExactly("**");
  }

  @Test
  public void multi_part_patern_has_many_parts() {
    assertThat(pathPattern("a/b/c").parts())
        .containsExactly("a", "b", "c");
  }

  @ParameterizedTest
  @MethodSource("listOfInvalidPatterns")
  public void validationErrorReturnsMessageForInvalidPattern(String invalidPatter) {
      assertCall(() -> PathPattern.pathPattern(invalidPatter))
          .throwsException(IllegalPathPatternException.class);
  }

  @SuppressWarnings("unused")
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
