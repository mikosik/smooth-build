package org.smoothbuild.lang.expr;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.NIL;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class ConvertTest {

  @Test
  public void string_has_no_supertype() throws Exception {
    when(Convert.superTypesOf(STRING));
    thenReturned(empty());
  }

  @Test
  public void blob_has_no_supertype() throws Exception {
    when(Convert.superTypesOf(BLOB));
    thenReturned(empty());
  }

  @Test
  public void string_array_has_no_supertype() throws Exception {
    when(Convert.superTypesOf(STRING_ARRAY));
    thenReturned(empty());
  }

  @Test
  public void blob_array_has_no_supertype() throws Exception {
    when(Convert.superTypesOf(BLOB_ARRAY));
    thenReturned(empty());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void file_array_has_blob_array_as_supertype() throws Exception {
    when(Convert.superTypesOf(FILE_ARRAY));
    thenReturned(contains(BLOB_ARRAY));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void nil_has_each_array_as_supertype() throws Exception {
    when(Convert.superTypesOf(NIL));
    thenReturned(containsInAnyOrder(STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY));
  }
}
