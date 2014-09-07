package org.smoothbuild.lang.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.NIL;
import static org.smoothbuild.lang.base.STypes.NOTHING;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.smoothbuild.lang.base.STypes.allSTypes;
import static org.smoothbuild.lang.base.STypes.arraySTypes;
import static org.smoothbuild.lang.base.STypes.basicSTypes;
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
  public void array_stypes() throws Exception {
    assertThat(arraySTypes()).containsExactly(STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY, NIL);
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

  private static TypeLiteral<?> type(Class<?> klass) {
    return TypeLiteral.get(klass);
  }
}
