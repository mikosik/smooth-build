package org.smoothbuild.stdlib.file;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestContext;

public class FileConstructorTest extends StandardLibraryTestContext {
  @Test
  void file_constructor() throws Exception {
    var userModule = """
        result = File(0x41, "name.txt");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bFile("name.txt", "A"));
  }
}
