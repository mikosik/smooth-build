package org.smoothbuild.exec.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.exec.base.MessageTuple.severity;
import static org.smoothbuild.exec.base.MessageTuple.text;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.val.Tuple;
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
    Iterable<Tuple> iterable = container().messages().elements(Tuple.class);
    assertThat(iterable)
        .hasSize(1);
    Tuple tuple = iterable.iterator().next();
    assertThat(text(tuple))
        .isEqualTo("message");
    assertThat(severity(tuple))
        .isEqualTo("ERROR");
  }
}
