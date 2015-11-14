package org.smoothbuild.acceptance.builtin.blob;

import static org.smoothbuild.acceptance.FileArrayMatcher.isFileArrayWith;
import static org.testory.Testory.then;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ToFileTest extends AcceptanceTestCase {
  @Test
  public void to_file_function() throws IOException {
    givenFile("file.txt", "abc");
    givenScript("result: [toFile(path='newFile.txt', content=file('//file.txt'))];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isFileArrayWith("newFile.txt", "abc"));
  }
}
