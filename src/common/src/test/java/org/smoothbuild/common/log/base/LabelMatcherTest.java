package org.smoothbuild.common.log.base;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class LabelMatcherTest {
  @ParameterizedTest
  @MethodSource
  void test_pattern_validation(String pattern, boolean valid) {
    if (valid) {
      new LabelMatcher(pattern);
    } else {
      assertCall(() -> new LabelMatcher(pattern)).throwsException(IllegalArgumentException.class);
    }
  }

  static List<Arguments> test_pattern_validation() {
    return list(
        arguments(".", false),
        arguments("^", false),
        arguments("!", false),
        arguments(" ", false),
        arguments("abc ", false),
        arguments("abc", true),
        arguments(":abc", true),
        arguments(":abc:", true),
        arguments(":ABC:", true),
        arguments(":", true),
        arguments("::", true),
        arguments(":::", true),
        arguments("*", true),
        arguments("**", true),
        arguments("***", false),
        arguments("****", false),
        arguments(":abc****", false),
        arguments("****:def", false));
  }

  @ParameterizedTest
  @MethodSource
  void test_matcher(String pattern, String labelString, boolean expected) {
    var labelMatcher = new LabelMatcher(pattern);
    var label = label(labelString);
    assertThat(labelMatcher.test(label)).isEqualTo(expected);
  }

  static List<Arguments> test_matcher() {
    return list(
        arguments("**", ":abc", true),
        arguments("**", ":abc:def", true),
        arguments("**", ":abc:def:ghi", true),
        arguments("**", ":abc:def:ghi", true),
        arguments(":**", ":abc:def:ghi", true),
        arguments(":abc**", ":abc:def:ghi", true),
        arguments(":abc:**", ":abc:def:ghi", true),
        arguments(":abc:def**", ":abc:def:ghi", true),
        arguments(":abc:def:**", ":abc:def:ghi", true),
        arguments(":abc:def:ghi**", ":abc:def:ghi", true),
        arguments(":abc:def:ghi:**", ":abc:def:ghi", false),
        arguments("**:abc:def:ghi", ":abc:def:ghi", true),
        arguments("**abc:def:ghi", ":abc:def:ghi", true),
        arguments("**:def:ghi", ":abc:def:ghi", true),
        arguments("**def:ghi", ":abc:def:ghi", true),
        arguments("**:ghi", ":abc:def:ghi", true),
        arguments("**ghi", ":abc:def:ghi", true),
        arguments(":abc:**:mno", ":abc:def:ghi:jkl:mno", true),
        arguments("**:def:**", ":abc:def:ghi:jkl:mno", true),
        arguments("**:ghi:**", ":abc:def:ghi:jkl:mno", true),
        arguments("**:jkl:**", ":abc:def:ghi:jkl:mno", true),
        arguments(":abc:**:ghi:**:mno", ":abc:def:ghi:jkl:mno", true),
        arguments("*", ":abc", false),
        arguments(":*", ":abc", true),
        arguments(":abc:*", ":abc", false),
        arguments(":abc:*", ":abc:def", true),
        arguments(":abc:*", ":abc:def:ghi", false),
        arguments(":abc:*:ghi", ":abc:def:ghi", true),
        arguments(":abc:*:ghi", ":abc:def:ghi:jkl", false),
        arguments(":abc:*:ghi", ":abc:def:xxx:ghi", false),
        arguments(":*:def", ":abc:def", true),
        arguments(":*:def", ":def", false),
        arguments(":*:def", ":abc:def:ghi", false),
        arguments(":*:def:*", ":abc:def:ghi", true));
  }
}
