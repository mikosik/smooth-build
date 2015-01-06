package org.smoothbuild.builtin.string;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.smoothbuild.testing.acceptance.AcceptanceTestUtils.artifactPath;
import static org.smoothbuild.testing.acceptance.AcceptanceTestUtils.script;

import java.io.IOException;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.testing.acceptance.AcceptanceTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;

public class ToBlobTest {
  @Inject
  @ProjectDir
  private FakeFileSystem fileSystem;
  @Inject
  private FakeUserConsole userConsole;
  @Inject
  private BuildWorker buildWorker;

  @Before
  public void before() {
    createInjector(new AcceptanceTestModule()).injectMembers(this);
  }

  @Test
  public void to_blob_function() throws IOException {
    String content = "file content";
    script(fileSystem, "result: toBlob('" + content + "');");
    buildWorker.run(asList("result"));

    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), content);
  }
}
