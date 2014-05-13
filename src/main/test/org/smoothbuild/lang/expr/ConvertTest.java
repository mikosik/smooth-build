package org.smoothbuild.lang.expr;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.NIL;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class ConvertTest {

  @Test
  public void testCanConvert() throws Exception {
    assertTrue(Convert.isAssignable(STRING, STRING));
    assertFalse(Convert.isAssignable(STRING, STRING_ARRAY));
    assertFalse(Convert.isAssignable(STRING, BLOB));
    assertFalse(Convert.isAssignable(STRING, BLOB_ARRAY));
    assertFalse(Convert.isAssignable(STRING, FILE));
    assertFalse(Convert.isAssignable(STRING, FILE_ARRAY));
    assertFalse(Convert.isAssignable(STRING, NIL));

    assertFalse(Convert.isAssignable(BLOB, STRING));
    assertTrue(Convert.isAssignable(BLOB, BLOB));
    assertFalse(Convert.isAssignable(BLOB, FILE));
    assertFalse(Convert.isAssignable(BLOB, STRING_ARRAY));
    assertFalse(Convert.isAssignable(BLOB, BLOB_ARRAY));
    assertFalse(Convert.isAssignable(BLOB, FILE_ARRAY));
    assertFalse(Convert.isAssignable(BLOB, NIL));

    assertFalse(Convert.isAssignable(FILE, STRING));
    assertTrue(Convert.isAssignable(FILE, BLOB));
    assertTrue(Convert.isAssignable(FILE, FILE));
    assertFalse(Convert.isAssignable(FILE, STRING_ARRAY));
    assertFalse(Convert.isAssignable(FILE, BLOB_ARRAY));
    assertFalse(Convert.isAssignable(FILE, FILE_ARRAY));
    assertFalse(Convert.isAssignable(FILE, NIL));

    assertFalse(Convert.isAssignable(STRING_ARRAY, STRING));
    assertFalse(Convert.isAssignable(STRING_ARRAY, BLOB));
    assertFalse(Convert.isAssignable(STRING_ARRAY, FILE));
    assertTrue(Convert.isAssignable(STRING_ARRAY, STRING_ARRAY));
    assertFalse(Convert.isAssignable(STRING_ARRAY, BLOB_ARRAY));
    assertFalse(Convert.isAssignable(STRING_ARRAY, FILE_ARRAY));
    assertFalse(Convert.isAssignable(STRING_ARRAY, NIL));

    assertFalse(Convert.isAssignable(BLOB_ARRAY, STRING));
    assertFalse(Convert.isAssignable(BLOB_ARRAY, BLOB));
    assertFalse(Convert.isAssignable(BLOB_ARRAY, FILE));
    assertFalse(Convert.isAssignable(BLOB_ARRAY, STRING_ARRAY));
    assertTrue(Convert.isAssignable(BLOB_ARRAY, BLOB_ARRAY));
    assertFalse(Convert.isAssignable(BLOB_ARRAY, FILE_ARRAY));
    assertFalse(Convert.isAssignable(BLOB_ARRAY, NIL));

    assertFalse(Convert.isAssignable(FILE_ARRAY, STRING));
    assertFalse(Convert.isAssignable(FILE_ARRAY, BLOB));
    assertFalse(Convert.isAssignable(FILE_ARRAY, FILE));
    assertFalse(Convert.isAssignable(FILE_ARRAY, STRING_ARRAY));
    assertTrue(Convert.isAssignable(FILE_ARRAY, BLOB_ARRAY));
    assertTrue(Convert.isAssignable(FILE_ARRAY, FILE_ARRAY));
    assertFalse(Convert.isAssignable(FILE_ARRAY, NIL));

    assertFalse(Convert.isAssignable(NIL, STRING));
    assertFalse(Convert.isAssignable(NIL, BLOB));
    assertFalse(Convert.isAssignable(NIL, FILE));
    assertTrue(Convert.isAssignable(NIL, STRING_ARRAY));
    assertTrue(Convert.isAssignable(NIL, BLOB_ARRAY));
    assertTrue(Convert.isAssignable(NIL, FILE_ARRAY));
    assertTrue(Convert.isAssignable(NIL, NIL));
  }

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
