package org.smoothbuild.db.taskoutputs;

import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.message.base.MessageType.FATAL;
import static org.smoothbuild.message.base.MessageType.INFO;
import static org.smoothbuild.message.base.MessageType.SUGGESTION;
import static org.smoothbuild.message.base.MessageType.WARNING;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.taskoutputs.AllMessageTypes;

public class AllMessageTypesTest {

  @Test
  public void byte_value_for_fatal_is_zero() {
    when(AllMessageTypes.INSTANCE.valueToByte(FATAL));
    thenReturned((byte) 0);
  }

  @Test
  public void byte_value_for_error_is_zero() {
    when(AllMessageTypes.INSTANCE.valueToByte(ERROR));
    thenReturned((byte) 1);
  }

  @Test
  public void byte_value_for_warning_is_one() {
    when(AllMessageTypes.INSTANCE.valueToByte(WARNING));
    thenReturned((byte) 2);
  }

  @Test
  public void byte_value_for_suggestion_is_two() {
    when(AllMessageTypes.INSTANCE.valueToByte(SUGGESTION));
    thenReturned((byte) 3);
  }

  @Test
  public void byte_value_for_info_is_three() {
    when(AllMessageTypes.INSTANCE.valueToByte(INFO));
    thenReturned((byte) 4);
  }
}
