package org.smoothbuild.lang.base;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.smoothbuild.lang.base.Conversions.canConvert;
import static org.smoothbuild.lang.base.Conversions.convertFunctionName;
import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.NIL;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class ConversionsTest {

  @Test
  public void can_convert() throws Exception {
    assertTrue(canConvert(STRING, STRING));
    assertFalse(canConvert(STRING, STRING_ARRAY));
    assertFalse(canConvert(STRING, BLOB));
    assertFalse(canConvert(STRING, BLOB_ARRAY));
    assertFalse(canConvert(STRING, FILE));
    assertFalse(canConvert(STRING, FILE_ARRAY));
    assertFalse(canConvert(STRING, NIL));

    assertFalse(canConvert(BLOB, STRING));
    assertTrue(canConvert(BLOB, BLOB));
    assertFalse(canConvert(BLOB, FILE));
    assertFalse(canConvert(BLOB, STRING_ARRAY));
    assertFalse(canConvert(BLOB, BLOB_ARRAY));
    assertFalse(canConvert(BLOB, FILE_ARRAY));
    assertFalse(canConvert(BLOB, NIL));

    assertFalse(canConvert(FILE, STRING));
    assertTrue(canConvert(FILE, BLOB));
    assertTrue(canConvert(FILE, FILE));
    assertFalse(canConvert(FILE, STRING_ARRAY));
    assertFalse(canConvert(FILE, BLOB_ARRAY));
    assertFalse(canConvert(FILE, FILE_ARRAY));
    assertFalse(canConvert(FILE, NIL));

    assertFalse(canConvert(STRING_ARRAY, STRING));
    assertFalse(canConvert(STRING_ARRAY, BLOB));
    assertFalse(canConvert(STRING_ARRAY, FILE));
    assertTrue(canConvert(STRING_ARRAY, STRING_ARRAY));
    assertFalse(canConvert(STRING_ARRAY, BLOB_ARRAY));
    assertFalse(canConvert(STRING_ARRAY, FILE_ARRAY));
    assertFalse(canConvert(STRING_ARRAY, NIL));

    assertFalse(canConvert(BLOB_ARRAY, STRING));
    assertFalse(canConvert(BLOB_ARRAY, BLOB));
    assertFalse(canConvert(BLOB_ARRAY, FILE));
    assertFalse(canConvert(BLOB_ARRAY, STRING_ARRAY));
    assertTrue(canConvert(BLOB_ARRAY, BLOB_ARRAY));
    assertFalse(canConvert(BLOB_ARRAY, FILE_ARRAY));
    assertFalse(canConvert(BLOB_ARRAY, NIL));

    assertFalse(canConvert(FILE_ARRAY, STRING));
    assertFalse(canConvert(FILE_ARRAY, BLOB));
    assertFalse(canConvert(FILE_ARRAY, FILE));
    assertFalse(canConvert(FILE_ARRAY, STRING_ARRAY));
    assertTrue(canConvert(FILE_ARRAY, BLOB_ARRAY));
    assertTrue(canConvert(FILE_ARRAY, FILE_ARRAY));
    assertFalse(canConvert(FILE_ARRAY, NIL));

    assertFalse(canConvert(NIL, STRING));
    assertFalse(canConvert(NIL, BLOB));
    assertFalse(canConvert(NIL, FILE));
    assertTrue(canConvert(NIL, STRING_ARRAY));
    assertTrue(canConvert(NIL, BLOB_ARRAY));
    assertTrue(canConvert(NIL, FILE_ARRAY));
    assertTrue(canConvert(NIL, NIL));
  }

  @Test
  public void convert_function_name_for_file_to_blob_conversion() throws Exception {
    when(convertFunctionName(FILE, BLOB));
    thenReturned(name("fileToBlob"));
  }

  @Test
  public void convert_function_name_for_file_array_to_blob_array_conversion() throws Exception {
    when(convertFunctionName(FILE_ARRAY, BLOB_ARRAY));
    thenReturned(name("fileArrayToBlobArray"));
  }

  @Test
  public void convert_function_name_for_nil_to_string_array_conversion() throws Exception {
    when(convertFunctionName(NIL, STRING_ARRAY));
    thenReturned(name("nilToStringArray"));
  }

  @Test
  public void convert_function_name_for_nil_to_blob_array_conversion() throws Exception {
    when(convertFunctionName(NIL, BLOB_ARRAY));
    thenReturned(name("nilToBlobArray"));
  }

  @Test
  public void convert_function_name_for_nil_to_file_array_conversion() throws Exception {
    when(convertFunctionName(NIL, FILE_ARRAY));
    thenReturned(name("nilToFileArray"));
  }
}
