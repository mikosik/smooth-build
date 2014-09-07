package org.smoothbuild.lang.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.PARAM_JTYPE_TO_STYPE;
import static org.smoothbuild.lang.base.STypes.RESULT_JTYPE_TO_STYPE;
import static org.smoothbuild.lang.base.STypes.NIL;
import static org.smoothbuild.lang.base.STypes.NOTHING;
import static org.smoothbuild.lang.base.STypes.PARAM_STYPES;
import static org.smoothbuild.lang.base.STypes.RESULT_STYPES;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.smoothbuild.lang.base.STypes.sArrayTypeContaining;
import static org.smoothbuild.lang.base.STypes.arraySTypes;
import static org.smoothbuild.lang.base.STypes.basicSTypes;
import static org.smoothbuild.lang.base.STypes.paramJTypeToSType;
import static org.smoothbuild.lang.base.STypes.resultJTypeToSType;

import java.util.Set;

import org.junit.Test;
import org.smoothbuild.lang.expr.Convert;

import com.google.common.collect.Sets;
import com.google.common.testing.EqualsTester;
import com.google.inject.TypeLiteral;

public class STypesTest {

  @Test
  public void arrayElemTypes() throws Exception {
    assertThat(STRING_ARRAY.elemType()).isEqualTo(STRING);
    assertThat(BLOB_ARRAY.elemType()).isEqualTo(BLOB);
    assertThat(FILE_ARRAY.elemType()).isEqualTo(FILE);
    assertThat(NIL.elemType()).isEqualTo(NOTHING);
  }

  @Test
  public void equalsAndHashCode() throws Exception {
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
  public void testBasicTypes() throws Exception {
    assertThat(basicSTypes()).containsExactly(STRING, BLOB, FILE);
  }

  @Test
  public void testArrayTypes() throws Exception {
    assertThat(arraySTypes()).containsExactly(STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY, NIL);
  }

  @Test
  public void testToString() throws Exception {
    assertThat(STRING.toString()).isEqualTo("'String'");
  }

  @Test
  public void all_types_returns_list_sorted_by_super_type_dependency() throws Exception {
    Set<SType<?>> visited = Sets.newHashSet();
    for (SType<?> type : STypes.allSTypes()) {
      for (SType<?> superType : Convert.superTypesOf(type)) {
        assertThat(visited).contains(superType);
      }
      visited.add(type);
    }
  }

  @Test
  public void paramJTypeToSType_works_for_all_types() {
    assertThat(paramJTypeToSType(type(SString.class))).isEqualTo(STRING);
    assertThat(paramJTypeToSType(type(SBlob.class))).isEqualTo(BLOB);
    assertThat(paramJTypeToSType(type(SFile.class))).isEqualTo(FILE);

    assertThat(paramJTypeToSType(new TypeLiteral<SArray<SString>>() {})).isEqualTo(STRING_ARRAY);
    assertThat(paramJTypeToSType(new TypeLiteral<SArray<SBlob>>() {})).isEqualTo(BLOB_ARRAY);
    assertThat(paramJTypeToSType(new TypeLiteral<SArray<SFile>>() {})).isEqualTo(FILE_ARRAY);
  }

  @Test
  public void resultJTypeToSType_works_for_all_types() {
    assertThat(resultJTypeToSType(type(SString.class))).isEqualTo(STRING);
    assertThat(resultJTypeToSType(type(SBlob.class))).isEqualTo(BLOB);
    assertThat(resultJTypeToSType(type(SFile.class))).isEqualTo(FILE);

    assertThat(resultJTypeToSType(new TypeLiteral<SArray<SString>>() {})).isEqualTo(STRING_ARRAY);
    assertThat(resultJTypeToSType(new TypeLiteral<SArray<SBlob>>() {})).isEqualTo(BLOB_ARRAY);
    assertThat(resultJTypeToSType(new TypeLiteral<SArray<SFile>>() {})).isEqualTo(FILE_ARRAY);
  }

  @Test
  public void javaResultToSmoothContainsAllResultTypes() throws Exception {
    SType<?>[] array = new SType<?>[]{};
    assertThat(RESULT_JTYPE_TO_STYPE.values()).containsOnly(RESULT_STYPES.toArray(array));
  }

  @Test
  public void javaParamToSmoothContainsAllResultTypes() throws Exception {
    SType<?>[] array = new SType<?>[]{};
    assertThat(PARAM_JTYPE_TO_STYPE.values()).containsOnly(PARAM_STYPES.toArray(array));
  }

  @Test
  public void testArrayTypeContaining() throws Exception {
    assertThat(sArrayTypeContaining(STRING)).isEqualTo(STRING_ARRAY);
    assertThat(sArrayTypeContaining(BLOB)).isEqualTo(BLOB_ARRAY);
    assertThat(sArrayTypeContaining(FILE)).isEqualTo(FILE_ARRAY);
    assertThat(sArrayTypeContaining(NOTHING)).isEqualTo(NIL);
  }

  private static TypeLiteral<?> type(Class<?> klass) {
    return TypeLiteral.get(klass);
  }
}
