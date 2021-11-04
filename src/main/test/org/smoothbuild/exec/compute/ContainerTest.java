package org.smoothbuild.exec.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.exec.base.MessageStruct.severity;
import static org.smoothbuild.exec.base.MessageStruct.text;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.val.Struc_;
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
    Iterable<Struc_> iterable = container().messages().elements(Struc_.class);
    assertThat(iterable)
        .hasSize(1);
    Struc_ struct = iterable.iterator().next();
    assertThat(text(struct))
        .isEqualTo("message");
    assertThat(severity(struct))
        .isEqualTo("ERROR");
  }
}
