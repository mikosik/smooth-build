package org.smoothbuild.stdlib.common;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestContext;

public class MapTest extends StandardLibraryTestContext {
  @Test
  void mapping_bools() throws Exception {
    var code = """
        result = map([false, true], not);
        """;
    createUserModule(code);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bBool(true), bBool(false)));
  }

  @Test
  void mapping_files_to_path() throws Exception {
    var code =
        """
        files = [File(0x01, "test01.txt"), File(0x02, "test02.txt")];
        pathOf(File file) = file.path;
        result = map(files, pathOf);
        """;
    createUserModule(code);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bString("test01.txt"), bString("test02.txt")));
  }
}
