package org.smoothbuild.fs.base.match;

import static org.junit.Assert.fail;
import static org.smoothbuild.fs.base.match.PathPattern.pathPattern;
import static org.smoothbuild.fs.base.match.PathPatternTester.generatePatterns;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class PathPatternTest {

  @Test
  public void allGeneratedPatternsAreValid() throws Exception {
    Function<String, Void> consumer = new Function<String, Void>() {
      public Void apply(String pattern) {
        pathPattern(pattern);
        return null;
      }
    };

    for (int i = 1; i <= 6; i++) {
      generatePatterns(i, consumer);
    }
  }

  @Test
  public void validationErrorReturnsMessageForInvalidPattern() {
    for (String pattern : listOfInvalidPatterns()) {
      try {
        PathPattern.pathPattern(pattern);
        fail("exception should be thrown");
      } catch (IllegalArgumentException e) {
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
