package org.smoothbuild.lang.builtin.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.io.cache.value.build.FileBuilder;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.testing.lang.type.FakeBlob;
import org.smoothbuild.testing.task.exec.FakePluginApi;

public class ContentFunctionTest {
  FakePluginApi pluginApi = new FakePluginApi();

  @Test
  public void content_of_file_is_returned_as_blob() throws Exception {
    FileBuilder builder = pluginApi.fileBuilder();
    builder.setPath(path("some/path"));
    FakeBlob blob = new FakeBlob();
    builder.setContent(blob);
    SFile file = builder.build();

    SBlob actual = ContentFunction.execute(pluginApi, params(file));
    assertThat(actual).isSameAs(file.content());
  }

  private static ContentFunction.Parameters params(final SFile file) {
    return new ContentFunction.Parameters() {
      @Override
      public SFile file() {
        return file;
      }
    };
  }
}
