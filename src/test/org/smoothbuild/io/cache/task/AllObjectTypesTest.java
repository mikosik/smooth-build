package org.smoothbuild.io.cache.task;

import static org.smoothbuild.lang.type.STypes.FILE;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.smoothbuild.lang.type.STypes.STRING_ARRAY;
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
    when(AllObjectTypes.INSTANCE.valueToByte(STRING_ARRAY));
    thenReturned((byte) 1);
  }

  @Test
  public void byte_value_for_suggestion_is_two() {
    when(AllObjectTypes.INSTANCE.valueToByte(FILE));
    thenReturned((byte) 2);
  }

  @Test
  public void byte_value_for_info_is_three() {
    when(AllObjectTypes.INSTANCE.valueToByte(FILE_ARRAY));
    thenReturned((byte) 3);
  }
}
