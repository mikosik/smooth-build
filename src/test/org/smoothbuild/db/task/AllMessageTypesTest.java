package org.smoothbuild.db.task;

import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.message.message.MessageType.INFO;
import static org.smoothbuild.message.message.MessageType.SUGGESTION;
import static org.smoothbuild.message.message.MessageType.WARNING;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class AllMessageTypesTest {

  @Test
  public void byte_value_for_error_is_zero() {
    when(AllMessageTypes.INSTANCE.valueToByte(ERROR));
    thenReturned((byte) 0);
  }

  @Test
  public void byte_value_for_warning_is_one() {
    when(AllMessageTypes.INSTANCE.valueToByte(WARNING));
    thenReturned((byte) 1);
  }

  @Test
  public void byte_value_for_suggestion_is_two() {
    when(AllMessageTypes.INSTANCE.valueToByte(SUGGESTION));
    thenReturned((byte) 2);
  }

  @Test
  public void byte_value_for_info_is_three() {
    when(AllMessageTypes.INSTANCE.valueToByte(INFO));
    thenReturned((byte) 3);
  }
}
