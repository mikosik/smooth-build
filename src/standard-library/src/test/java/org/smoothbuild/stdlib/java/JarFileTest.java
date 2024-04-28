package org.smoothbuild.stdlib.java;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestCase;

public class JarFileTest extends StandardLibraryTestCase {
  @Test
  void jar_unjar() throws Exception {
    var userModule =
        """
        aFile =
            [File(0x41, "dir/file1.txt"), File(0x42, "file2.txt")]
          > jarFile("file.jar");
        result = unjar(aFile.content);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFile("dir/file1.txt", "A"), bFile("file2.txt", "B")));
  }
}
