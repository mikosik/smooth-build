package org.smoothbuild.testing.io.fs.base;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class PathTesting {

  public static List<String> listOfCorrectPaths() {
    Builder<String> builder = ImmutableList.builder();

    builder.add(".");

    builder.add("abc");
    builder.add("abc/def");
    builder.add("abc/def/ghi");
    builder.add("abc/def/ghi/ijk");

    builder.add("./abc");
    builder.add("./abc/def");
    builder.add("./abc/def/ghi");
    builder.add("./abc/def/ghi/ijk");

    // These paths look really strange but Linux allows creating them.
    // I cannot see any good reason for forbidding them.
    builder.add("...");
    builder.add(".../abc");
    builder.add("abc/...");
    builder.add("abc/.../def");

    builder.add("./...");
    builder.add("./.../abc");
    builder.add("./abc/...");
    builder.add("./abc/.../def");

    return builder.build();
  }

  public static ImmutableList<String> listOfInvalidPaths() {
    Builder<String> builder = ImmutableList.builder();

    builder.add("");

    builder.add("./");
    builder.add("./.");
    builder.add("././");

    builder.add("abc/");
    builder.add("abc/def/");
    builder.add("abc/def/ghi/");

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

    return builder.build();
  }

}
