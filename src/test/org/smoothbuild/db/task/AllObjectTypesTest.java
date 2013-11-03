package org.smoothbuild.db.task;

import static org.smoothbuild.db.task.ObjectType.FILE_SET_TYPE;
import static org.smoothbuild.db.task.ObjectType.FILE_TYPE;
import static org.smoothbuild.db.task.ObjectType.STRING_SET_TYPE;
import static org.smoothbuild.db.task.ObjectType.STRING_TYPE;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class AllObjectTypesTest {

  @Test
  public void byte_value_for_error_is_zero() {
    when(AllObjectTypes.INSTANCE.valueToByte(STRING_TYPE));
    thenReturned((byte) 0);
  }

  @Test
  public void byte_value_for_warning_is_one() {
    when(AllObjectTypes.INSTANCE.valueToByte(STRING_SET_TYPE));
    thenReturned((byte) 1);
  }

  @Test
  public void byte_value_for_suggestion_is_two() {
    when(AllObjectTypes.INSTANCE.valueToByte(FILE_TYPE));
    thenReturned((byte) 2);
  }

  @Test
  public void byte_value_for_info_is_three() {
    when(AllObjectTypes.INSTANCE.valueToByte(FILE_SET_TYPE));
    thenReturned((byte) 3);
  }
}
