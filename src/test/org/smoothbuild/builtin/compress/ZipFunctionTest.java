package org.smoothbuild.builtin.compress;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.plugin.api.Path.path;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Test;
import org.smoothbuild.builtin.compress.ZipFunction.Parameters;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.FileSet;
import org.smoothbuild.testing.plugin.internal.TestFile;
import org.smoothbuild.testing.plugin.internal.TestFileSet;
import org.smoothbuild.testing.plugin.internal.TestSandbox;

// TODO pass as argument object that throws exception when second file is
// created. Or maybe even method that instantiate function given
// ZipFunction.class
public class ZipFunctionTest {
  TestSandbox sandbox = new TestSandbox();

  @Test
  public void testZipping() throws IOException {
    TestFileSet inputFiles = new TestFileSet();
    inputFiles.createFile(path("fileA.txt")).createContentWithFilePath();
    inputFiles.createFile(path("fileB.txt")).createContentWithFilePath();

    File result = runExecute(params(inputFiles));

    TestFileSet unpackedFiles = new TestFileSet();

    byte[] buffer = new byte[2048];
    int fileCount = 0;
    try (ZipInputStream zipInputStream = new ZipInputStream(result.openInputStream());) {
      ZipEntry entry = null;
      while ((entry = zipInputStream.getNextEntry()) != null) {
        fileCount++;
        TestFile file = unpackedFiles.createFile(path(entry.getName()));
        try (OutputStream outputStream = file.openOutputStream()) {
          int len = 0;
          while ((len = zipInputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
          }
        }
        file.assertContentContainsFilePath();
      }
    }
    assertThat(fileCount).isEqualTo(2);
  }

  private static ZipFunction.Parameters params(final FileSet fileSet) {
    return new ZipFunction.Parameters() {
      @Override
      public FileSet fileSet() {
        return fileSet;
      }
    };
  }

  private File runExecute(Parameters params) {
    return ZipFunction.execute(sandbox, params);
  }
}
