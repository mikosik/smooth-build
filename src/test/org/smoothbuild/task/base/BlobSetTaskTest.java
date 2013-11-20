package org.smoothbuild.task.base;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.lang.function.value.Array;
import org.smoothbuild.lang.function.value.Blob;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.task.base.FakeResult;
import org.smoothbuild.testing.task.exec.FakeSandbox;

public class BlobSetTaskTest {
  FakeFileSystem fileSystem = new FakeFileSystem();
  FakeSandbox sandbox = new FakeSandbox(fileSystem);
  CodeLocation codeLocation = new FakeCodeLocation();

  Blob blob1;
  Blob blob2;
  Result result1;
  Result result2;

  @Before
  public void before() {
    blob1 = sandbox.objectDb().blob(new byte[] { 1, 2, 3, 4 });
    blob2 = sandbox.objectDb().blob(new byte[] { 1, 2, 3 });
    result1 = new FakeResult(blob1);
    result2 = new FakeResult(blob2);
  }

  @Test
  public void execute() throws IOException {
    BlobSetTask fileSetTask = new BlobSetTask(newArrayList(result1, result2), codeLocation);
    @SuppressWarnings("unchecked")
    Array<Blob> result = (Array<Blob>) fileSetTask.execute(sandbox);
    assertThat(result).containsOnly(blob1, blob2);
  }
}
