package org.smoothbuild.lang.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.JAVA_PARAM_TO_SMOOTH;
import static org.smoothbuild.lang.base.STypes.JAVA_RESULT_TO_SMOOTH;
import static org.smoothbuild.lang.base.STypes.NIL;
import static org.smoothbuild.lang.base.STypes.NOTHING;
import static org.smoothbuild.lang.base.STypes.PARAM_TYPES;
import static org.smoothbuild.lang.base.STypes.RESULT_TYPES;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.smoothbuild.lang.base.STypes.arrayTypeContaining;
import static org.smoothbuild.lang.base.STypes.arrayTypes;
import static org.smoothbuild.lang.base.STypes.basicTypes;
import static org.smoothbuild.lang.base.STypes.javaParamTypetoType;
import static org.smoothbuild.lang.base.STypes.javaResultTypetoType;

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
    assertThat(basicTypes()).containsExactly(STRING, BLOB, FILE);
  }

  @Test
  public void testArrayTypes() throws Exception {
    assertThat(arrayTypes()).containsExactly(STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY, NIL);
  }

  @Test
  public void testToString() throws Exception {
    assertThat(STRING.toString()).isEqualTo("'String'");
  }

  @Test
  public void all_types_returns_list_sorted_by_super_type_dependency() throws Exception {
    Set<SType<?>> visited = Sets.newHashSet();
    for (SType<?> type : STypes.allTypes()) {
      for (SType<?> superType : Convert.superTypesOf(type)) {
        assertThat(visited).contains(superType);
      }
      visited.add(type);
    }
  }

  @Test
  public void testJavaParamTypetoType() {
    assertThat(javaParamTypetoType(type(SString.class))).isEqualTo(STRING);
    assertThat(javaParamTypetoType(type(SBlob.class))).isEqualTo(BLOB);
    assertThat(javaParamTypetoType(type(SFile.class))).isEqualTo(FILE);

    assertThat(javaParamTypetoType(new TypeLiteral<SArray<SString>>() {})).isEqualTo(STRING_ARRAY);
    assertThat(javaParamTypetoType(new TypeLiteral<SArray<SBlob>>() {})).isEqualTo(BLOB_ARRAY);
    assertThat(javaParamTypetoType(new TypeLiteral<SArray<SFile>>() {})).isEqualTo(FILE_ARRAY);
  }

  @Test
  public void testJavaResultTypetoType() {
    assertThat(javaResultTypetoType(type(SString.class))).isEqualTo(STRING);
    assertThat(javaResultTypetoType(type(SBlob.class))).isEqualTo(BLOB);
    assertThat(javaResultTypetoType(type(SFile.class))).isEqualTo(FILE);

    assertThat(javaResultTypetoType(new TypeLiteral<SArray<SString>>() {})).isEqualTo(STRING_ARRAY);
    assertThat(javaResultTypetoType(new TypeLiteral<SArray<SBlob>>() {})).isEqualTo(BLOB_ARRAY);
    assertThat(javaResultTypetoType(new TypeLiteral<SArray<SFile>>() {})).isEqualTo(FILE_ARRAY);
  }

  @Test
  public void javaResultToSmoothContainsAllResultTypes() throws Exception {
    SType<?>[] array = new SType<?>[] {};
    assertThat(JAVA_RESULT_TO_SMOOTH.values()).containsOnly(RESULT_TYPES.toArray(array));
  }

  @Test
  public void javaParamToSmoothContainsAllResultTypes() throws Exception {
    SType<?>[] array = new SType<?>[] {};
    assertThat(JAVA_PARAM_TO_SMOOTH.values()).containsOnly(PARAM_TYPES.toArray(array));
  }

  @Test
  public void testArrayTypeContaining() throws Exception {
    assertThat(arrayTypeContaining(STRING)).isEqualTo(STRING_ARRAY);
    assertThat(arrayTypeContaining(BLOB)).isEqualTo(BLOB_ARRAY);
    assertThat(arrayTypeContaining(FILE)).isEqualTo(FILE_ARRAY);
    assertThat(arrayTypeContaining(NOTHING)).isEqualTo(NIL);
  }

  private static TypeLiteral<?> type(Class<?> klass) {
    return TypeLiteral.get(klass);
  }
}
