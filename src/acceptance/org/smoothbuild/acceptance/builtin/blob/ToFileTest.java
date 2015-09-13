package org.smoothbuild.acceptance.builtin.blob;

import static org.smoothbuild.acceptance.FileArrayMatcher.isFileArrayWith;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ToFileTest extends AcceptanceTestCase {
  @Test
  public void to_file_function() throws IOException {
    givenFile("file.txt", "abc");
    givenBuildScript(script("result: [toFile(path='newFile.txt', content=file('file.txt'))];"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", isFileArrayWith("newFile.txt", "abc"));
  }
}
