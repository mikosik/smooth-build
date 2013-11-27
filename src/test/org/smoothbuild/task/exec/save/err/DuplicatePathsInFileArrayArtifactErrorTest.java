package org.smoothbuild.task.exec.save.err;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.function.base.Name.name;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.message.base.Message;

import com.google.common.collect.ImmutableList;

public class DuplicatePathsInFileArrayArtifactErrorTest {

  @Test
  public void test() {
    Name name = name("func");
    Iterable<Path> fileSet = ImmutableList.of(path("path1"), path("path2"), path("path3"));
    Message error = new DuplicatePathsInFileArrayArtifactError(name, fileSet);

    StringBuilder builder = new StringBuilder();
    builder.append("Can't store result of 'func' as it contains files with duplicated paths:");
    builder.append("\n  'path1'");
    builder.append("\n  'path2'");
    builder.append("\n  'path3'");

    assertThat(error.message()).isEqualTo(builder.toString());
  }
}
