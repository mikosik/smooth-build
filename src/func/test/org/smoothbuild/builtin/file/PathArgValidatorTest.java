package org.smoothbuild.builtin.file;

import static org.junit.Assert.fail;
import static org.smoothbuild.builtin.file.PathArgValidator.validatedProjectPath;
import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;

import java.util.List;

import org.junit.Test;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.message.Message;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class PathArgValidatorTest {
  private final ValuesDb valuesDb = memoryValuesDb();

  @Test
  public void illegal_project_paths_are_reported() {
    String name = "name";
    for (String path : listOfInvalidProjectPaths()) {
      try {
        validatedProjectPath(name, valuesDb.string(path));
        fail("exception should be thrown for path = " + path);
      } catch (Message e) {
        // expected
      }
    }
  }

  @Test
  public void valid_project_paths_are_accepted() {
    for (String path : listOfCorrectProjectPaths()) {
      validatedProjectPath("name", valuesDb.string(path));
    }
  }

  private static List<String> listOfCorrectProjectPaths() {
    Builder<String> builder = ImmutableList.builder();

    builder.add("//");

    builder.add("//abc");
    builder.add("//abc/def");
    builder.add("//abc/def/ghi");
    builder.add("//abc/def/ghi/ijk");

    // These paths look really strange but Linux allows creating them.
    // I cannot see any good reason for forbidding them.
    builder.add("//...");
    builder.add("//.../abc");
    builder.add("//abc/...");
    builder.add("//abc/.../def");

    return builder.build();
  }

  private static ImmutableList<String> listOfInvalidProjectPaths() {
    Builder<String> builder = ImmutableList.builder();

    builder.add("");
    builder.add(".");

    builder.add("./");
    builder.add("./.");
    builder.add("././");

    builder.add("abc/");
    builder.add("abc/def/");
    builder.add("abc/def/ghi/");

    builder.add("./abc");
    builder.add("./abc/def");
    builder.add("./abc/def/ghi");

    builder.add("..");
    builder.add("../");
    builder.add("./../");
    builder.add("../abc");
    builder.add("abc/..");
    builder.add("abc/../def");
    builder.add("../..");

    builder.add("");
    builder.add("/");
    builder.add("///");

    builder.add("abc");
    builder.add("/abc");
    builder.add("///abc");

    builder.add("abc//");
    builder.add("abc///");

    builder.add("abc//def");
    builder.add("abc///def");

    return builder.build();
  }
}
