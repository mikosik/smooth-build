package org.smoothbuild.stdlib.common;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestCase;

public class MapTest extends StandardLibraryTestCase {
  @Test
  public void mapping_bools() throws Exception {
    var code = """
        result = map([false, true], not);
        """;
    createUserModule(code);
    evaluate("result");
    assertThat(artifact()).isEqualTo(arrayB(boolB(true), boolB(false)));
  }

  @Test
  public void mapping_files_to_path() throws Exception {
    var code =
        """
        files = [File(0x01, "test01.txt"), File(0x02, "test02.txt")];
        pathOf(File file) = file.path;
        result = map(files, pathOf);
        """;
    createUserModule(code);
    evaluate("result");
    assertThat(artifact()).isEqualTo(arrayB(stringB("test01.txt"), stringB("test02.txt")));
  }
}
