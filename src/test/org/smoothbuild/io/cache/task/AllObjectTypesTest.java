package org.smoothbuild.io.cache.task;

import static org.smoothbuild.lang.type.Type.FILE;
import static org.smoothbuild.lang.type.Type.FILE_SET;
import static org.smoothbuild.lang.type.Type.STRING;
import static org.smoothbuild.lang.type.Type.STRING_SET;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class AllObjectTypesTest {

  @Test
  public void byte_value_for_error_is_zero() {
    when(AllObjectTypes.INSTANCE.valueToByte(STRING));
    thenReturned((byte) 0);
  }

  @Test
  public void byte_value_for_warning_is_one() {
    when(AllObjectTypes.INSTANCE.valueToByte(STRING_SET));
    thenReturned((byte) 1);
  }

  @Test
  public void byte_value_for_suggestion_is_two() {
    when(AllObjectTypes.INSTANCE.valueToByte(FILE));
    thenReturned((byte) 2);
  }

  @Test
  public void byte_value_for_info_is_three() {
    when(AllObjectTypes.INSTANCE.valueToByte(FILE_SET));
    thenReturned((byte) 3);
  }
}
