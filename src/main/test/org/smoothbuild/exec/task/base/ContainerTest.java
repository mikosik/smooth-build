package org.smoothbuild.exec.task.base;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.lang.object.base.Messages.severity;
import static org.smoothbuild.lang.object.base.Messages.text;

import org.junit.jupiter.api.Test;
import org.smoothbuild.io.util.TempDir;
import org.smoothbuild.io.util.TempManager;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.testing.TestingContext;

public class ContainerTest extends TestingContext {
  @Test
  public void file_system() {
    assertThat(container().fileSystem())
        .isSameInstanceAs(fullFileSystem());
  }

  @Test
  public void messages_are_logged() {
    container().log().error("message");
    Iterable<Struct> iterable = container().messages().asIterable(Struct.class);
    assertThat(iterable)
        .hasSize(1);
    Struct struct = iterable.iterator().next();
    assertThat(text(struct))
        .isEqualTo("message");
    assertThat(severity(struct))
        .isEqualTo("ERROR");
  }

  @Test
  public void create_temp_dir_call_is_forwarded_to_temp_dir_manager() throws Exception {
    TempDir tempDir = mock(TempDir.class);
    TempManager tempManager = mock(TempManager.class);
    Container container = new Container(null, null, tempManager);
    when(tempManager.tempDir(container)).thenReturn(tempDir);
    assertThat(container.createTempDir())
        .isEqualTo(tempDir);
  }
}
