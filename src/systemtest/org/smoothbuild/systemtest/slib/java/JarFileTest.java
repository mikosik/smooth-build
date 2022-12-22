package org.smoothbuild.systemtest.slib.java;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestCase;

public class JarFileTest extends SystemTestCase {
  @Test
  public void jar_unjar() throws IOException {
    createUserModule("""
            aFile =
                [file(0x41, "dir/file1.txt"), file(0x42, "file2.txt")]
              > jarFile("file.jar");
            result = unjar(aFile.content);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/file1.txt", "A", "file2.txt", "B");
  }

}
