package org.smoothbuild.task.save.err;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.function.base.Name.name;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.message.base.Message;

public class DuplicatePathsInFileArrayArtifactErrorTest {

  @Test
  public void test() {
    Name name = name("func");
    Iterable<Path> fileSet = asList(path("path1"), path("path2"), path("path3"));
    Message error = new DuplicatePathsInFileArrayArtifactError(name, fileSet);

    StringBuilder builder = new StringBuilder();
    builder.append("Can't store result of 'func' as it contains files with duplicated paths:\n");
    builder.append("  'path1'\n");
    builder.append("  'path2'\n");
    builder.append("  'path3'");
    assertEquals(error.message(), builder.toString());
  }
}
