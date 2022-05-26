package org.smoothbuild.vm.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.run.eval.MessageStruct.severity;
import static org.smoothbuild.run.eval.MessageStruct.text;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
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
    Iterable<TupleB> iterable = container().messages().elems(TupleB.class);
    assertThat(iterable)
        .hasSize(1);
    TupleB tuple = iterable.iterator().next();
    assertThat(text(tuple))
        .isEqualTo("message");
    assertThat(severity(tuple))
        .isEqualTo("ERROR");
  }
}
