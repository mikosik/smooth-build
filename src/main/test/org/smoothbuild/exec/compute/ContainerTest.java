package org.smoothbuild.exec.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.db.record.base.Messages.severity;
import static org.smoothbuild.db.record.base.Messages.text;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.record.base.Tuple;
import org.smoothbuild.io.util.TempDir;
import org.smoothbuild.io.util.TempManager;
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
    Iterable<Tuple> iterable = container().messages().asIterable(Tuple.class);
    assertThat(iterable)
        .hasSize(1);
    Tuple tuple = iterable.iterator().next();
    assertThat(text(tuple))
        .isEqualTo("message");
    assertThat(severity(tuple))
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
