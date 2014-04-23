package org.smoothbuild.lang.expr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.NIL;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;

import org.junit.Test;

public class ConvertTest {

  @Test
  public void testCanConvert() throws Exception {
    assertThat(Convert.isAssignable(STRING, STRING)).isTrue();
    assertThat(Convert.isAssignable(STRING, STRING_ARRAY)).isFalse();
    assertThat(Convert.isAssignable(STRING, BLOB)).isFalse();
    assertThat(Convert.isAssignable(STRING, BLOB_ARRAY)).isFalse();
    assertThat(Convert.isAssignable(STRING, FILE)).isFalse();
    assertThat(Convert.isAssignable(STRING, FILE_ARRAY)).isFalse();
    assertThat(Convert.isAssignable(STRING, NIL)).isFalse();

    assertThat(Convert.isAssignable(BLOB, STRING)).isFalse();
    assertThat(Convert.isAssignable(BLOB, BLOB)).isTrue();
    assertThat(Convert.isAssignable(BLOB, FILE)).isFalse();
    assertThat(Convert.isAssignable(BLOB, STRING_ARRAY)).isFalse();
    assertThat(Convert.isAssignable(BLOB, BLOB_ARRAY)).isFalse();
    assertThat(Convert.isAssignable(BLOB, FILE_ARRAY)).isFalse();
    assertThat(Convert.isAssignable(BLOB, NIL)).isFalse();

    assertThat(Convert.isAssignable(FILE, STRING)).isFalse();
    assertThat(Convert.isAssignable(FILE, BLOB)).isTrue();
    assertThat(Convert.isAssignable(FILE, FILE)).isTrue();
    assertThat(Convert.isAssignable(FILE, STRING_ARRAY)).isFalse();
    assertThat(Convert.isAssignable(FILE, BLOB_ARRAY)).isFalse();
    assertThat(Convert.isAssignable(FILE, FILE_ARRAY)).isFalse();
    assertThat(Convert.isAssignable(FILE, NIL)).isFalse();

    assertThat(Convert.isAssignable(STRING_ARRAY, STRING)).isFalse();
    assertThat(Convert.isAssignable(STRING_ARRAY, BLOB)).isFalse();
    assertThat(Convert.isAssignable(STRING_ARRAY, FILE)).isFalse();
    assertThat(Convert.isAssignable(STRING_ARRAY, STRING_ARRAY)).isTrue();
    assertThat(Convert.isAssignable(STRING_ARRAY, BLOB_ARRAY)).isFalse();
    assertThat(Convert.isAssignable(STRING_ARRAY, FILE_ARRAY)).isFalse();
    assertThat(Convert.isAssignable(STRING_ARRAY, NIL)).isFalse();

    assertThat(Convert.isAssignable(BLOB_ARRAY, STRING)).isFalse();
    assertThat(Convert.isAssignable(BLOB_ARRAY, BLOB)).isFalse();
    assertThat(Convert.isAssignable(BLOB_ARRAY, FILE)).isFalse();
    assertThat(Convert.isAssignable(BLOB_ARRAY, STRING_ARRAY)).isFalse();
    assertThat(Convert.isAssignable(BLOB_ARRAY, BLOB_ARRAY)).isTrue();
    assertThat(Convert.isAssignable(BLOB_ARRAY, FILE_ARRAY)).isFalse();
    assertThat(Convert.isAssignable(BLOB_ARRAY, NIL)).isFalse();

    assertThat(Convert.isAssignable(FILE_ARRAY, STRING)).isFalse();
    assertThat(Convert.isAssignable(FILE_ARRAY, BLOB)).isFalse();
    assertThat(Convert.isAssignable(FILE_ARRAY, FILE)).isFalse();
    assertThat(Convert.isAssignable(FILE_ARRAY, STRING_ARRAY)).isFalse();
    assertThat(Convert.isAssignable(FILE_ARRAY, BLOB_ARRAY)).isTrue();
    assertThat(Convert.isAssignable(FILE_ARRAY, FILE_ARRAY)).isTrue();
    assertThat(Convert.isAssignable(FILE_ARRAY, NIL)).isFalse();

    assertThat(Convert.isAssignable(NIL, STRING)).isFalse();
    assertThat(Convert.isAssignable(NIL, BLOB)).isFalse();
    assertThat(Convert.isAssignable(NIL, FILE)).isFalse();
    assertThat(Convert.isAssignable(NIL, STRING_ARRAY)).isTrue();
    assertThat(Convert.isAssignable(NIL, BLOB_ARRAY)).isTrue();
    assertThat(Convert.isAssignable(NIL, FILE_ARRAY)).isTrue();
    assertThat(Convert.isAssignable(NIL, NIL)).isTrue();
  }

  @Test
  public void superTypes() throws Exception {
    assertThat(Convert.superTypesOf(STRING)).containsOnly();
    assertThat(Convert.superTypesOf(BLOB)).containsOnly();
    assertThat(Convert.superTypesOf(FILE)).containsOnly(BLOB);

    assertThat(Convert.superTypesOf(STRING_ARRAY)).containsOnly();
    assertThat(Convert.superTypesOf(BLOB_ARRAY)).containsOnly();
    assertThat(Convert.superTypesOf(FILE_ARRAY)).containsOnly(BLOB_ARRAY);
    assertThat(Convert.superTypesOf(NIL)).containsOnly(STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY);
  }
}
