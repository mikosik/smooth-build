package org.smoothbuild.fs.match;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class PathPatternTest {

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
