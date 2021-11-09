package org.smoothbuild.exec.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.exec.base.MessageStruct.severity;
import static org.smoothbuild.exec.base.MessageStruct.text;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.val.TupleH;
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
    Iterable<TupleH> iterable = container().messages().elements(TupleH.class);
    assertThat(iterable)
        .hasSize(1);
    TupleH tuple = iterable.iterator().next();
    assertThat(text(tuple))
        .isEqualTo("message");
    assertThat(severity(tuple))
        .isEqualTo("ERROR");
  }
}
