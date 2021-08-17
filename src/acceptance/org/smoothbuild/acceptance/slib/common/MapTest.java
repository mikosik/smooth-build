package org.smoothbuild.acceptance.slib.common;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class MapTest extends AcceptanceTestCase {
  @Test
  public void mapping_bools() throws Exception {
    String code = """
        result = map([false, true], not);
        """;
    createUserModule(code);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactStringified("result"))
        .isEqualTo(list(Character.toString(1), Character.toString(0)));
  }

  @Test
  public void mapping_files_to_path() throws Exception {
    String code = """
        files = [ file(0x01, "test01.txt"), file(0x02, "test02.txt") ];
        pathOf(File file) = file.path;
        result = map(files, pathOf);
        """;
    createUserModule(code);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactStringified("result"))
        .isEqualTo(list("test01.txt", "test02.txt"));
  }
}
