package org.smoothbuild.lang.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.NIL;
import static org.smoothbuild.lang.base.STypes.NOTHING;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.smoothbuild.lang.base.STypes.allSTypes;
import static org.smoothbuild.lang.base.STypes.basicSTypes;
import static org.smoothbuild.lang.base.STypes.canConvert;
import static org.smoothbuild.lang.base.STypes.paramJTypeToSType;
import static org.smoothbuild.lang.base.STypes.paramSTypes;
import static org.smoothbuild.lang.base.STypes.resultJTypeToSType;
import static org.smoothbuild.lang.base.STypes.sArrayTypeContaining;

import java.util.Set;

import org.junit.Test;
import org.smoothbuild.lang.expr.Convert;

import com.google.common.collect.Sets;
import com.google.common.testing.EqualsTester;
import com.google.inject.TypeLiteral;

public class STypesTest {

  @Test
  public void basic_stypes() throws Exception {
    assertThat(basicSTypes()).containsExactly(STRING, BLOB, FILE);
  }

  @Test
  public void param_stypes() throws Exception {
    assertThat(paramSTypes()).containsExactly(STRING, BLOB, FILE, STRING_ARRAY, BLOB_ARRAY,
        FILE_ARRAY, NIL);
  }

  @Test
  public void all_stypes() throws Exception {
    assertThat(allSTypes()).containsExactly(STRING, BLOB, FILE, NOTHING, STRING_ARRAY, BLOB_ARRAY,
        FILE_ARRAY, NIL);
  }

  @Test
  public void array_elem_types() throws Exception {
    assertThat(STRING_ARRAY.elemType()).isEqualTo(STRING);
    assertThat(BLOB_ARRAY.elemType()).isEqualTo(BLOB);
    assertThat(FILE_ARRAY.elemType()).isEqualTo(FILE);
    assertThat(NIL.elemType()).isEqualTo(NOTHING);
  }

  @Test
  public void equals_and_hashcode() throws Exception {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(STRING);
    tester.addEqualityGroup(BLOB);
    tester.addEqualityGroup(FILE);
    tester.addEqualityGroup(STRING_ARRAY);
    tester.addEqualityGroup(BLOB_ARRAY);
    tester.addEqualityGroup(FILE_ARRAY);
    tester.addEqualityGroup(NIL);

    tester.testEquals();
  }

  @Test
  public void to_string() throws Exception {
    assertThat(STRING.toString()).isEqualTo("'String'");
  }

  @Test
  public void all_stypes_returns_list_sorted_by_super_type_dependency() throws Exception {
    Set<SType<?>> visited = Sets.newHashSet();
    for (SType<?> type : allSTypes()) {
      for (SType<?> superType : Convert.superTypesOf(type)) {
        assertThat(visited).contains(superType);
      }
      visited.add(type);
    }
  }

  @Test
  public void paramJTypeToSType_works_for_all_types() {
    assertThat(paramJTypeToSType(STRING.jType())).isEqualTo(STRING);
    assertThat(paramJTypeToSType(BLOB.jType())).isEqualTo(BLOB);
    assertThat(paramJTypeToSType(FILE.jType())).isEqualTo(FILE);

    assertThat(paramJTypeToSType(STRING_ARRAY.jType())).isEqualTo(STRING_ARRAY);
    assertThat(paramJTypeToSType(BLOB_ARRAY.jType())).isEqualTo(BLOB_ARRAY);
    assertThat(paramJTypeToSType(FILE_ARRAY.jType())).isEqualTo(FILE_ARRAY);
  }

  @Test
  public void resultJTypeToSType_works_for_all_types() {
    assertThat(resultJTypeToSType(STRING.jType())).isEqualTo(STRING);
    assertThat(resultJTypeToSType(BLOB.jType())).isEqualTo(BLOB);
    assertThat(resultJTypeToSType(FILE.jType())).isEqualTo(FILE);

    assertThat(resultJTypeToSType(STRING_ARRAY.jType())).isEqualTo(STRING_ARRAY);
    assertThat(resultJTypeToSType(BLOB_ARRAY.jType())).isEqualTo(BLOB_ARRAY);
    assertThat(resultJTypeToSType(FILE_ARRAY.jType())).isEqualTo(FILE_ARRAY);
  }

  @Test
  public void sArrayType_containing() throws Exception {
    assertThat(sArrayTypeContaining(STRING)).isEqualTo(STRING_ARRAY);
    assertThat(sArrayTypeContaining(BLOB)).isEqualTo(BLOB_ARRAY);
    assertThat(sArrayTypeContaining(FILE)).isEqualTo(FILE_ARRAY);
    assertThat(sArrayTypeContaining(NOTHING)).isEqualTo(NIL);
  }

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

  private static TypeLiteral<?> type(Class<?> klass) {
    return TypeLiteral.get(klass);
  }
}
