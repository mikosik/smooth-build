package org.smoothbuild.stdlib.compress;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.log.base.Log.error;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestContext;

public class ZipTest extends StandardLibraryTestContext {
  @Test
  void zip_unzip() throws Exception {
    var userModule =
        """
        result = [file("dir/file1.txt"), file("file2.txt")] > compressZip() > compressUnzip();
        """;
    createUserModule(userModule);
    createProjectFile("dir/file1.txt", "abc");
    createProjectFile("file2.txt", "def");

    evaluate("result");

    assertThat(artifact())
        .isEqualTo(bArray(bFile("dir/file1.txt", "abc"), bFile("file2.txt", "def")));
  }

  @Test
  void corrupted_archive_causes_error() throws IOException {
    var userModule =
        """
        randomJunk = 0x123456;
        result = compressUnzip(randomJunk);
        """;
    createUserModule(userModule);

    evaluate("result");

    assertThat(logs())
        .containsExactly(
            error(
                "Error reading archive: Cannot read archive. Corrupted data? Internal message: Could not fill buffer"));
  }
}
