package org.smoothbuild.acceptance.clean;

import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.File;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class TempDirectoryTest extends AcceptanceTestCase {
  @Test
  public void temp_directory_is_deleted_after_build_execution() throws Exception {
    givenScript("result: tempFilePath();");
    whenSmoothBuild("result");
    thenReturnedCode(0);
    when(new File(artifactContent("result"))).exists();
    thenReturned(false);
  }
}
