package org.smoothbuild.stdlib.file.match;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.common.filesystem.base.PathS.path;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.filesystem.base.PathS;

public class PathMatcherTest {
  @ParameterizedTest
  @MethodSource("data_set")
  public void test_matching(String pattern, PathS path, boolean expected) {
    assertThat(new PathMatcher(pattern).test(path)).isEqualTo(expected);
  }

  public static Stream<Arguments> data_set() {
    return Stream.of(
        // without wildcards
        arguments("abc", path("abc"), true),
        arguments("abc", path("abcabc"), false),
        arguments("abc", path("abbc"), false),
        arguments("abc", path("abc/abc"), false),
        arguments("abc/def/ghi", path("abc/def/ghi"), true),
        arguments("abc/def/ghi", path("abcdefghi"), false),
        arguments("abc/def/ghi", path("abcdef/ghi"), false),
        arguments("abc/def/ghi", path("bc/def/ghi"), false),
        arguments("abc/def/ghi", path("abc/def/gh"), false),
        arguments("abc/def/ghi", path("abc/ghi"), false),
        arguments("abc/def/ghi", path("abc/def/ghi/ghi"), false),
        arguments("abc/def/ghi", path("abc/abc/def/ghi"), false),
        arguments("abc/def/ghi", path("abc/def/def/ghi"), false),

        // ?
        arguments("?", path("a"), true),
        arguments("?", path("b"), true),
        arguments("?", path("ab"), false),
        arguments("a?c", path("ac"), false),
        arguments("a?c", path("abc"), true),
        arguments("a?c", path("axc"), true),
        arguments("a?c", path("a/c"), false),
        arguments("??", path("a"), false),
        arguments("??", path("ab"), true),
        arguments("??", path("abc"), false),
        arguments("??c", path("c"), false),
        arguments("??c", path("bc"), false),
        arguments("??c", path("abc"), true),
        arguments("??c", path("xabc"), false),
        arguments("a??c", path("ac"), false),
        arguments("a??c", path("axc"), false),
        arguments("a??c", path("axxc"), true),
        arguments("a??c", path("axxxc"), false),

        // [abc]
        arguments("[a]", path("a"), true),
        arguments("[a]", path("b"), false),
        arguments("[a]", path("aa"), false),
        arguments("[ab]", path("a"), true),
        arguments("[ab]", path("b"), true),
        arguments("[ab]", path("c"), false),
        arguments("[ab]", path("ab"), false),
        arguments("[ab]", path("aa"), false),
        arguments("[ab]", path("bb"), false),
        arguments("[ab][ab]", path("aa"), true),
        arguments("[ab][ab]", path("ab"), true),
        arguments("[ab][ab]", path("ba"), true),
        arguments("[ab][ab]", path("bb"), true),

        // [!abc]
        arguments("[!a]", path("a"), false),
        arguments("[!a]", path("b"), true),
        arguments("[!a]", path("aa"), false),
        arguments("[!ab]", path("a"), false),
        arguments("[!ab]", path("b"), false),
        arguments("[!ab]", path("c"), true),
        arguments("[!ab]", path("ab"), false),
        arguments("[!ab]", path("aa"), false),
        arguments("[!ab]", path("bb"), false),
        arguments("[!ab][!ab]", path("aa"), false),
        arguments("[!ab][!ab]", path("ab"), false),
        arguments("[!ab][!ab]", path("ba"), false),
        arguments("[!ab][!ab]", path("bb"), false),
        arguments("[!ab][!ab]", path("ac"), false),
        arguments("[!ab][!ab]", path("ca"), false),
        arguments("[!ab][!ab]", path("cc"), true),
        arguments("a[!x]b", path("a/b"), false),

        // [a-c]
        arguments("[a-c]", path("a"), true),
        arguments("[a-c]", path("b"), true),
        arguments("[a-c]", path("c"), true),
        arguments("[a-c]", path("d"), false),
        arguments("[a-c]", path("aa"), false),
        arguments("[a-c]", path("ab"), false),
        arguments("[a-c]", path("ac"), false),
        arguments("[a-c]", path("abc"), false),

        // [!a-c]
        arguments("[!a-c]", path("a"), false),
        arguments("[!a-c]", path("b"), false),
        arguments("[!a-c]", path("c"), false),
        arguments("[!a-c]", path("d"), true),
        arguments("[!a-c]", path("aa"), false),
        arguments("[!a-c]", path("ab"), false),
        arguments("[!a-c]", path("ac"), false),
        arguments("[!a-c]", path("abc"), false),

        // [a-cxyz]
        arguments("[a-cxyz]", path("a"), true),
        arguments("[a-cxyz]", path("b"), true),
        arguments("[a-cxyz]", path("c"), true),
        arguments("[a-cxyz]", path("x"), true),
        arguments("[a-cxyz]", path("y"), true),
        arguments("[a-cxyz]", path("z"), true),
        arguments("[a-cxyz]", path("d"), false),
        arguments("[a-cxyz]", path("aa"), false),
        arguments("[a-cxyz]", path("ab"), false),
        arguments("[a-cxyz]", path("ac"), false),
        arguments("[a-cxyz]", path("ax"), false),
        arguments("[a-cxyz]", path("ay"), false),
        arguments("[a-cxyz]", path("az"), false),
        arguments("[a-cxyz]", path("xy"), false),

        // [-]
        arguments("[-]", path("-"), true),
        arguments("[-]", path("a"), false),
        arguments("[-]a", path("-a"), true),
        arguments("[-]a", path("-"), false),
        arguments("[-]a", path("a"), false),
        arguments("[!-]", path("-"), false),
        arguments("[!-]", path("a"), true),

        // [?], [*], [\], [!]
        arguments("[?]", path("a"), false),
        arguments("[*]", path("a"), false),
        arguments("[a!]", path("!"), true),
        arguments("[a!]", path("a"), true),
        arguments("[a!]", path("b"), false),

        // {abc,def}
        arguments("{abc}", path("abc"), true),
        arguments("{abc}", path("a"), false),
        arguments("{abc,def}", path("abc"), true),
        arguments("{abc,def}", path("def"), true),
        arguments("{abc,def}", path("a"), false),
        arguments("{abc,def}", path("abc,def"), false),
        arguments("{abc}xyz", path("abcxyz"), true),
        arguments("{abc/}xyz", path("abc/xyz"), true),
        arguments("{abc[xyz]}", path("abc"), false),
        arguments("{abc[xyz]}", path("abcx"), true),

        // *
        arguments("*", path("abc"), true),
        arguments("*", path("abcghi"), true),
        arguments("*", path("abc/def"), false),
        arguments("*", path("abc/def/ghi"), false),
        arguments("*/abc/def", path("xxx/abc/def"), true),
        arguments("abc/def/*", path("abc/def/xxx"), true),
        arguments("*/abc/*", path("xxx/abc/yyy"), true),
        arguments("abc/*/def", path("abc/xxx/def"), true),
        arguments("*/*", path("abc/def"), true),
        arguments("abc*def", path("abcdef"), true),
        arguments("abc*def", path("abcxxxdef"), true),
        arguments("abc*def*ghi", path("abcdefghi"), true),
        arguments("abc*def*ghi", path("abcxxxdefghi"), true),
        arguments("abc*def*ghi", path("abcdefyyyghi"), true),
        arguments("abc*def*ghi", path("abcxxxdefyyyghi"), true),

        // **
        arguments("**", path("abc"), true),
        arguments("**", path("abc/def"), true),
        arguments("**", path("abc/def/ghi"), true),
        arguments("**/abc", path("xxx/abc"), true),
        arguments("**/abc", path("xxx/yyy/abc"), true),
        arguments("**abc", path("abc"), true),
        arguments("abc/**", path("abc/xxx"), true),
        arguments("abc/**", path("abc/xxx/yyy"), true),
        arguments("**abc**", path("abc"), true),
        arguments("**abc**", path("xxxabc"), true),
        arguments("**abc**", path("xxx/abc"), true),
        arguments("**abc**", path("abcyyy"), true),
        arguments("**abc**", path("abc/yyy"), true),
        arguments("**abc**", path("xxxabcyyy"), true),
        arguments("**abc**", path("xxx/abcyyy"), true),
        arguments("**abc**", path("xxxabc/yyy"), true),
        arguments("**abc**", path("xxx/abc/yyy"), true),
        arguments("**/abc/**", path("xxx/abc/yyy"), true),
        arguments("**/abc/**", path("xxx/zzz/abc/yyy"), true),
        arguments("abc**def", path("abcdef"), true),
        arguments("abc**def", path("abcxxxdef"), true),
        arguments("abc**def", path("abc/def"), true),
        arguments("abc**def", path("abc/xxx/def"), true),
        arguments("abc**def", path("abc/xxx/yyy/def"), true),
        arguments("abc/**/def", path("abc/xxx/def"), true),
        arguments("abc/**/def", path("abc/xxx/yyy/def"), true),
        arguments("abc/**/def", path("abc/xxx/yyy/zzz/def"), true),
        arguments("abc/**/def", path("abc/abc/def"), true),
        arguments("abc/**/def", path("abc/def/def"), true),
        arguments("abc/**/def", path("abc/abc/def/def"), true),
        arguments("abc/**/def", path("abc/def/abc/def"), true),
        arguments("*/abc/def", path("abc/def"), false),
        arguments("*/abc/def", path("xxx/yyy/abc/def"), false),
        arguments("*/abc/def", path("abc/xxx/def"), false),
        arguments("abc/def/*", path("abc/def"), false),
        arguments("abc/def/*", path("abc/def/xxx/yyy"), false),
        arguments("abc/def/*", path("abc/xxx/def"), false),
        arguments("*/abc/*", path("abc"), false),
        arguments("*/abc/*", path("abc/yyy"), false),
        arguments("*/abc/*", path("xxx/abc"), false),
        arguments("*/abc/*", path("xxx/zzz/abc/yyy"), false),
        arguments("*/abc/*", path("xxx/abc/yyy/zzz"), false),
        arguments("*/abc/*", path("xxx/abcabc/yyy"), false),
        arguments("*/abc/*", path("xxx/abc/abc/yyy"), false),
        arguments("abc/*/def", path("abcdef"), false),
        arguments("abc/*/def", path("abc/def"), false),
        arguments("abc/*/def", path("abc/xxx/yyy/def"), false),
        arguments("*/*", path("abc"), false),
        arguments("*/*", path("abc/def/ghi"), false),
        arguments("abc*def", path("abc/def"), false),
        arguments("abc*def", path("abdef"), false),
        arguments("abc/xxx/def", path("abc/def"), false),
        arguments("abc*def*ghi", path("abc/def/ghi"), false),
        arguments("abc*def*ghi", path("abcdefgh"), false),
        arguments("**/abc", path("xxx/yyyabc"), false),
        arguments("**/abc", path("xxx/abc/xxx"), false),
        arguments("abc/**", path("abc"), false),
        arguments("abc/**", path("abcxxx/yyy"), false),
        arguments("abc/**", path("xxx/abc/xxx"), false),
        arguments("**/abc/**", path("abc/xxx"), false),
        arguments("**/abc/**", path("xxx/abc"), false),
        arguments("**/abc/**", path("xxx/yyy/abc"), false),
        arguments("**/abc/**", path("abc"), false),
        arguments("abc/**/def", path("abcdef"), false),
        arguments("abc/**/def", path("abc/def"), false),
        arguments("abc/**/def", path("abc/xxx/zzz"), false),
        arguments("abc/**/def", path("xxx/zzz/def"), false),
        arguments("abc/**/def", path("abc"), false),
        arguments("abc/**/def", path("def"), false));
  }

  @ParameterizedTest
  @MethodSource("illegal_pattern_data_set")
  public void illegal_pattern(String pattern) {
    assertCall(() -> new PathMatcher(pattern).test(path("abc")))
        .throwsException(IllegalPathPatternException.class);
  }

  public static Stream<Arguments> illegal_pattern_data_set() {
    return Stream.of(
        // path separator cannot be inside brackets
        arguments("a[/]b"),
        // groups cannot be nested
        arguments("{abc{d,f,g}}"),
        arguments(""),
        arguments("."),
        arguments("./"),
        arguments("/."),
        arguments("./."),
        arguments("././"),
        arguments("abc/"),
        arguments("abc/def/"),
        arguments("abc/def/ghi/"),
        arguments("./abc"),
        arguments("./abc/def"),
        arguments("./abc/def/ghi"),
        arguments("./abc/def/ghi/ijk"),
        arguments("abc/."),
        arguments("abc/def/."),
        arguments("abc/def/ghi/."),
        arguments("abc/def/ghi/ijk/."),
        arguments(".."),
        arguments("../"),
        arguments("./../"),
        arguments("../abc"),
        arguments("abc/.."),
        arguments("abc/../def"),
        arguments("../.."),
        arguments("/"),
        arguments("//"),
        arguments("///"),
        arguments("/abc"),
        arguments("//abc"),
        arguments("///abc"),
        arguments("abc//"),
        arguments("abc///"),
        arguments("abc//def"),
        arguments("abc///def"),
        arguments("*/"),
        arguments("/*"),
        arguments("**/"),
        arguments("/**"));

    // should be illegal but JDK implementation allows them
    // double -
    // arguments("a--b"),
    // trailing -
    // arguments("a-"),
  }
}
