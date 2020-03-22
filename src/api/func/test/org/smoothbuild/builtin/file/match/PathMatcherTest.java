package org.smoothbuild.builtin.file.match;

import static org.junit.Assert.assertEquals;
import static org.smoothbuild.builtin.file.match.PathMatcher.pathMatcher;
import static org.smoothbuild.io.fs.base.Path.path;

import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.smoothbuild.io.fs.base.Path;

public class PathMatcherTest {
  @Test
  public void one_part_pattern_without_wildcards() throws Exception {
    assertPatternMatches("abc", "abc");

    assertPatternDoesNotMatch("abc", "abcabc");
    assertPatternDoesNotMatch("abc", "abbc");
    assertPatternDoesNotMatch("abc", "abc/abc");
  }

  @Test
  public void pattern_without_wildcards() throws Exception {
    assertPatternMatches("abc/def/ghi", "abc/def/ghi");

    assertPatternDoesNotMatch("abc/def/ghi", "abcdefghi");
    assertPatternDoesNotMatch("abc/def/ghi", "abcdef/ghi");
    assertPatternDoesNotMatch("abc/def/ghi", "bc/def/ghi");
    assertPatternDoesNotMatch("abc/def/ghi", "abc/def/gh");
    assertPatternDoesNotMatch("abc/def/ghi", "abc/ghi");

    assertPatternDoesNotMatch("abc/def/ghi", "abc/def/ghi/ghi");
    assertPatternDoesNotMatch("abc/def/ghi", "abc/abc/def/ghi");

    assertPatternDoesNotMatch("abc/def/ghi", "abc/def/def/ghi");
  }

  @Test
  public void single_star_alone() throws Exception {
    assertPatternMatches("*", "abc");
    assertPatternMatches("*", "abcghi");

    assertPatternDoesNotMatch("*", "abc/def");
    assertPatternDoesNotMatch("*", "abc/def/ghi");
  }

  @Test
  public void single_star_as_whole_part_prefix() throws Exception {
    assertPatternMatches("*/abc/def", "xxx/abc/def");

    assertPatternDoesNotMatch("*/abc/def", "abc/def");
    assertPatternDoesNotMatch("*/abc/def", "xxx/yyy/abc/def");
    assertPatternDoesNotMatch("*/abc/def", "abc/xxx/def");
  }

  @Test
  public void single_star_as_whole_part_suffix() throws Exception {
    assertPatternMatches("abc/def/*", "abc/def/xxx");

    assertPatternDoesNotMatch("abc/def/*", "abc/def");
    assertPatternDoesNotMatch("abc/def/*", "abc/def/xxx/yyy");
    assertPatternDoesNotMatch("abc/def/*", "abc/xxx/def");
  }

  @Test
  public void single_star_as_whole_part_prefix_and_suffix() throws Exception {
    assertPatternMatches("*/abc/*", "xxx/abc/yyy");

    assertPatternDoesNotMatch("*/abc/*", "abc");
    assertPatternDoesNotMatch("*/abc/*", "abc/yyy");
    assertPatternDoesNotMatch("*/abc/*", "xxx/abc");

    assertPatternDoesNotMatch("*/abc/*", "xxx/zzz/abc/yyy");
    assertPatternDoesNotMatch("*/abc/*", "xxx/abc/yyy/zzz");

    assertPatternDoesNotMatch("*/abc/*", "xxx/abcabc/yyy");
    assertPatternDoesNotMatch("*/abc/*", "xxx/abc/abc/yyy");
  }

  @Test
  public void single_star_in_the_middle_as_whole_part() throws Exception {
    assertPatternMatches("abc/*/def", "abc/xxx/def");

    assertPatternDoesNotMatch("abc/*/def", "abcdef");
    assertPatternDoesNotMatch("abc/*/def", "abc/def");
    assertPatternDoesNotMatch("abc/*/def", "abc/xxx/yyy/def");
  }

  @Test
  public void single_star_twice() throws Exception {
    assertPatternMatches("*/*", "abc/def");

    assertPatternDoesNotMatch("*/*", "abc");
    assertPatternDoesNotMatch("*/*", "abc/def/ghi");
  }

  @Test
  public void single_star_in_the_middle_of_only_part() throws Exception {
    assertPatternMatches("abc*def", "abcdef");
    assertPatternMatches("abc*def", "abcxxxdef");

    assertPatternDoesNotMatch("abc*def", "abc/def");
    assertPatternDoesNotMatch("abc*def", "abdef");
    assertPatternDoesNotMatch("abc/xxx/def", "abc/def");
  }

  @Test
  public void single_star_twice_in_the_middle_of_only_part() throws Exception {
    assertPatternMatches("abc*def*ghi", "abcdefghi");
    assertPatternMatches("abc*def*ghi", "abcxxxdefghi");
    assertPatternMatches("abc*def*ghi", "abcdefyyyghi");
    assertPatternMatches("abc*def*ghi", "abcxxxdefyyyghi");

    assertPatternDoesNotMatch("abc*def*ghi", "abc/def/ghi");
    assertPatternDoesNotMatch("abc*def*ghi", "abcdefgh");
  }

  @Test
  public void double_star_as_prefix() throws Exception {
    assertPatternMatches("**/abc", "abc");
    assertPatternMatches("**/abc", "xxx/abc");
    assertPatternMatches("**/abc", "xxx/yyy/abc");

    assertPatternDoesNotMatch("**/abc", "xxx/yyyabc");
    assertPatternDoesNotMatch("**/abc", "xxx/abc/xxx");
  }

  @Test
  public void double_star_as_suffix() throws Exception {
    assertPatternMatches("abc/**", "abc/xxx");
    assertPatternMatches("abc/**", "abc/xxx/yyy");

    assertPatternDoesNotMatch("abc/**", "abc");
    assertPatternDoesNotMatch("abc/**", "abcxxx/yyy");
    assertPatternDoesNotMatch("abc/**", "xxx/abc/xxx");
  }

  @Test
  public void double_star_as_prefix_and_suffix() throws Exception {
    assertPatternMatches("**/abc/**", "abc/xxx");
    assertPatternMatches("**/abc/**", "abc/xxx/yyy");
    assertPatternMatches("**/abc/**", "xxx/abc/yyy");
    assertPatternMatches("**/abc/**", "xxx/zzz/abc/yyy");

    assertPatternDoesNotMatch("**/abc/**", "xxx/abc");
    assertPatternDoesNotMatch("**/abc/**", "xxx/yyy/abc");
    assertPatternDoesNotMatch("**/abc/**", "abc");
  }

  @Test
  public void double_star_in_the_middle() throws Exception {
    assertPatternMatches("abc/**/def", "abc/def");

    assertPatternMatches("abc/**/def", "abc/xxx/def");
    assertPatternMatches("abc/**/def", "abc/xxx/yyy/def");
    assertPatternMatches("abc/**/def", "abc/xxx/yyy/zzz/def");

    assertPatternMatches("abc/**/def", "abc/abc/def");
    assertPatternMatches("abc/**/def", "abc/def/def");
    assertPatternMatches("abc/**/def", "abc/abc/def/def");
    assertPatternMatches("abc/**/def", "abc/def/abc/def");

    assertPatternDoesNotMatch("abc/**/def", "abcdef");
    assertPatternDoesNotMatch("abc/**/def", "abc/xxx/zzz");
    assertPatternDoesNotMatch("abc/**/def", "xxx/zzz/def");
    assertPatternDoesNotMatch("abc/**/def", "abc");
    assertPatternDoesNotMatch("abc/**/def", "def");
  }

  private static void assertPatternMatches(String pattern, String path) {
    assertMatchingResult(pattern, path, true);
  }

  private static void assertPatternDoesNotMatch(String pattern, String path) {
    assertMatchingResult(pattern, path, false);
  }

  private static void assertMatchingResult(String pattern, String path, boolean expected) {
    Predicate<Path> matcher = pathMatcher(pattern);
    assertEquals(matcher.test(path(path)), expected);
  }
}
