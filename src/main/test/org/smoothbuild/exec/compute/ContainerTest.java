package org.smoothbuild.exec.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.exec.base.MessageRec.severity;
import static org.smoothbuild.exec.base.MessageRec.text;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.val.Rec;
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
    Iterable<Rec> iterable = container().messages().elements(Rec.class);
    assertThat(iterable)
        .hasSize(1);
    Rec rec = iterable.iterator().next();
    assertThat(text(rec))
        .isEqualTo("message");
    assertThat(severity(rec))
        .isEqualTo("ERROR");
  }
}
