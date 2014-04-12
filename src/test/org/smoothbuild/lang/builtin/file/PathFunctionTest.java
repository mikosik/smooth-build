package org.smoothbuild.lang.builtin.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.io.cache.value.build.FileBuilder;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.testing.lang.type.FakeBlob;
import org.smoothbuild.testing.task.exec.FakeNativeApi;

public class PathFunctionTest {
  FakeNativeApi nativeApi = new FakeNativeApi();

  @Test
  public void file_path_is_returned_as_string() throws Exception {
    Path path = path("some/path");

    FileBuilder builder = nativeApi.fileBuilder();
    builder.setPath(path);
    builder.setContent(new FakeBlob());
    SFile file = builder.build();

    SString actual = PathFunction.execute(nativeApi, params(file));
    assertThat(actual.value()).isEqualTo(path.value());
  }

  private static PathFunction.Parameters params(final SFile file) {
    return new PathFunction.Parameters() {
      @Override
      public SFile file() {
        return file;
      }
    };
  }
}
