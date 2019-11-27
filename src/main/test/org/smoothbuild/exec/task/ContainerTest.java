package org.smoothbuild.exec.task;

import static org.hamcrest.Matchers.equalTo;
import static org.smoothbuild.lang.object.base.Messages.severity;
import static org.smoothbuild.lang.object.base.Messages.text;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.then;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;
import static org.testory.common.Matchers.same;

import org.junit.Test;
import org.smoothbuild.io.util.TempDir;
import org.smoothbuild.io.util.TempManager;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.Iterables;

public class ContainerTest extends TestingContext {
  private final TempManager tempManager = mock(TempManager.class);
  private TempDir tempDir;
  private Container container;

  @Test
  public void file_system() {
    when(container().fileSystem());
    thenReturned(same(fullFileSystem()));
  }

  @Test
  public void messages_are_logged() {
    when(container().log()).error("message");
    then(Iterables.size(container().messages().asIterable(SObject.class)), equalTo(1));
    then(text(container().messages().asIterable(Struct.class).iterator().next()),
        equalTo("message"));
    then(severity(container().messages().asIterable(Struct.class).iterator().next()),
        equalTo("ERROR"));
  }

  @Test
  public void create_temp_dir_call_is_forwarded_to_temp_dir_manager() throws Exception {
    given(tempDir = mock(TempDir.class));
    given(container = new Container(null, null, tempManager));
    given(willReturn(tempDir), tempManager).tempDir(container);
    when(() -> container.createTempDir());
    thenReturned(tempDir);
  }
}
