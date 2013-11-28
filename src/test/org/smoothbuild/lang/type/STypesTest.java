package org.smoothbuild.lang.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.type.STypes.BLOB;
import static org.smoothbuild.lang.type.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.type.STypes.EMPTY_ARRAY;
import static org.smoothbuild.lang.type.STypes.FILE;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.type.STypes.JAVA_PARAM_TO_SMOOTH;
import static org.smoothbuild.lang.type.STypes.JAVA_RESULT_TO_SMOOTH;
import static org.smoothbuild.lang.type.STypes.NOTHING;
import static org.smoothbuild.lang.type.STypes.PARAM_TYPES;
import static org.smoothbuild.lang.type.STypes.RESULT_TYPES;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.smoothbuild.lang.type.STypes.STRING_ARRAY;
import static org.smoothbuild.lang.type.STypes.arrayTypeContaining;
import static org.smoothbuild.lang.type.STypes.arrayTypes;
import static org.smoothbuild.lang.type.STypes.basicTypes;
import static org.smoothbuild.lang.type.STypes.javaParamTypetoType;
import static org.smoothbuild.lang.type.STypes.javaResultTypetoType;

import java.util.Set;

import org.junit.Test;
import org.smoothbuild.lang.convert.Conversions;

import com.google.common.collect.Sets;
import com.google.common.testing.EqualsTester;
import com.google.inject.TypeLiteral;

public class STypesTest {

  @Test
  public void arrayElemTypes() throws Exception {
    assertThat(STRING_ARRAY.elemType()).isEqualTo(STRING);
    assertThat(BLOB_ARRAY.elemType()).isEqualTo(BLOB);
    assertThat(FILE_ARRAY.elemType()).isEqualTo(FILE);
    assertThat(EMPTY_ARRAY.elemType()).isEqualTo(NOTHING);
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
    tester.addEqualityGroup(EMPTY_ARRAY);

    tester.testEquals();
  }

  @Test
  public void testBasicTypes() throws Exception {
    assertThat(basicTypes()).containsExactly(STRING, BLOB, FILE);
  }

  @Test
  public void testArrayTypes() throws Exception {
    assertThat(arrayTypes()).containsExactly(STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY);
  }

  @Test
  public void testToString() throws Exception {
    assertThat(STRING.toString()).isEqualTo("'String'");
  }

  @Test
  public void testAllTypes() throws Exception {
    Set<SType<?>> visited = Sets.newHashSet();
    for (SType<?> type : STypes.allTypes()) {
      for (SType<?> superType : Conversions.superTypesOf(type)) {
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
  }

  private static TypeLiteral<?> type(Class<?> klass) {
    return TypeLiteral.get(klass);
  }
}
