package org.smoothbuild.lang.type;

import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;
import static org.testory.Testory.given;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.values.ValuesDb;

public class NothingTypeTest {
  private Type type;
  private ValuesDb valuesDb;

  @Test
  public void nothing_type_does_not_have_default_value() {
    given(type = Types.NOTHING);
    given(valuesDb = memoryValuesDb());
    when(type).defaultValue(valuesDb);
    thenThrown(UnsupportedOperationException.class);
  }
}
