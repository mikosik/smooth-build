package org.smoothbuild.task.save.err;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.function.base.Name.name;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.util.LineBuilder;

public class DuplicatePathsInFileArrayArtifactErrorTest {

  @Test
  public void test() {
    Name name = name("func");
    Iterable<Path> fileSet = asList(path("path1"), path("path2"), path("path3"));
    Message error = new DuplicatePathsInFileArrayArtifactError(name, fileSet);

    LineBuilder builder = new LineBuilder();
    builder.addLine("Can't store result of 'func' as it contains files with duplicated paths:");
    builder.addLine("  'path1'");
    builder.addLine("  'path2'");
    builder.add("  'path3'");

    assertEquals(error.message(), builder.build());
  }
}
