package org.smoothbuild.stdlib.java;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.log.base.Log.error;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestCase;

public class JarTest extends StandardLibraryTestCase {
  @Test
  void jar_unjar() throws Exception {
    var userModule =
        """
        result = [File(0x41, "dir/file1.txt"), File(0x42, "file2.txt")]
          > jar() > unjar();
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFile("dir/file1.txt", "A"), bFile("file2.txt", "B")));
  }

  @Test
  void corrupted_archive_causes_error() throws Exception {
    var userModule =
        """
        randomJunk = 0x123456;
        result =  unjar(randomJunk);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(logs())
        .contains(error("Error reading archive: Cannot read archive. "
            + "Corrupted data? Internal message: Could not fill buffer"));
  }
}
