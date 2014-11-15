package org.smoothbuild.builtin.compress;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.ARTIFACTS_PATH;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.script;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.integration.IntegrationTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;

public class ZipSmoothTest {
  @Inject
  @ProjectDir
  private FakeFileSystem fileSystem;
  @Inject
  private FakeUserConsole userConsole;
  @Inject
  private BuildWorker buildWorker;

  @Before
  public void before() {
    createInjector(new IntegrationTestModule()).injectMembers(this);
  }

  @Test
  public void testZipping() throws IOException {
    // given
    fileSystem.createFile(path("dir/fileA.txt"), "fileA.txt");
    fileSystem.createFile(path("dir/fileB.txt"), "fileB.txt");
    script(fileSystem, "run : files('dir') | zip ;");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();

    byte[] buffer = new byte[2048];
    int fileCount = 0;
    Path artifactPath = ARTIFACTS_PATH.append(path("run"));
    InputStream inputStream = fileSystem.openInputStream(artifactPath);
    try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
      ZipEntry entry = null;
      while ((entry = zipInputStream.getNextEntry()) != null) {
        fileCount++;

        Path path = path(entry.getName());
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
          int len = 0;
          while ((len = zipInputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
          }
          assertThat(new String(outputStream.toByteArray(), CHARSET)).isEqualTo(path.value());
        }
      }
    }
    assertThat(fileCount).isEqualTo(2);
  }
}
