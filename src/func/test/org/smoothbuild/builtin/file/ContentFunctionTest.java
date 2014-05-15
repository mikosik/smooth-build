package org.smoothbuild.builtin.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.builtin.file.ContentFunction.ContentParameters;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.task.exec.NativeApiImpl;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.task.exec.FakeNativeApi;

public class ContentFunctionTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private final NativeApiImpl nativeApi = new FakeNativeApi();

  @Test
  public void content_of_file_is_returned_as_blob() throws Exception {
    SFile file = objectsDb.file(path("some/path"), objectsDb.blob("content"));
    SBlob actual = ContentFunction.execute(nativeApi, params(file));
    assertThat(actual).isSameAs(file.content());
  }

  private static ContentParameters params(final SFile file) {
    return new ContentParameters() {
      @Override
      public SFile file() {
        return file;
      }
    };
  }
}
