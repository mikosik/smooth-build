package org.smoothbuild.lang.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.type.STypes.ANY;
import static org.smoothbuild.lang.type.STypes.BLOB;
import static org.smoothbuild.lang.type.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.type.STypes.EMPTY_ARRAY;
import static org.smoothbuild.lang.type.STypes.FILE;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.type.STypes.JAVA_PARAM_TO_SMOOTH;
import static org.smoothbuild.lang.type.STypes.JAVA_RESULT_TO_SMOOTH;
import static org.smoothbuild.lang.type.STypes.PARAM_TYPES;
import static org.smoothbuild.lang.type.STypes.RESULT_TYPES;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.smoothbuild.lang.type.STypes.STRING_ARRAY;
import static org.smoothbuild.lang.type.STypes.arrayTypeContaining;
import static org.smoothbuild.lang.type.STypes.arrayTypes;
import static org.smoothbuild.lang.type.STypes.basicTypes;
import static org.smoothbuild.lang.type.STypes.javaParamTypetoType;
import static org.smoothbuild.lang.type.STypes.javaResultTypetoType;

import org.junit.Test;

import com.google.common.testing.EqualsTester;
import com.google.inject.TypeLiteral;

public class STypesTest {

  @Test
  public void isAssignableFrom() throws Exception {
    assertThat(ANY.isAssignableFrom(ANY)).isTrue();
    assertThat(ANY.isAssignableFrom(STRING)).isTrue();
    assertThat(ANY.isAssignableFrom(STRING_ARRAY)).isTrue();
    assertThat(ANY.isAssignableFrom(BLOB)).isTrue();
    assertThat(ANY.isAssignableFrom(BLOB_ARRAY)).isTrue();
    assertThat(ANY.isAssignableFrom(FILE)).isTrue();
    assertThat(ANY.isAssignableFrom(FILE_ARRAY)).isTrue();
    assertThat(ANY.isAssignableFrom(EMPTY_ARRAY)).isTrue();

    assertThat(STRING.isAssignableFrom(ANY)).isFalse();
    assertThat(STRING.isAssignableFrom(STRING)).isTrue();
    assertThat(STRING.isAssignableFrom(STRING_ARRAY)).isFalse();
    assertThat(STRING.isAssignableFrom(BLOB)).isFalse();
    assertThat(STRING.isAssignableFrom(BLOB_ARRAY)).isFalse();
    assertThat(STRING.isAssignableFrom(FILE)).isFalse();
    assertThat(STRING.isAssignableFrom(FILE_ARRAY)).isFalse();
    assertThat(STRING.isAssignableFrom(EMPTY_ARRAY)).isFalse();

    assertThat(STRING_ARRAY.isAssignableFrom(ANY)).isFalse();
    assertThat(STRING_ARRAY.isAssignableFrom(STRING)).isFalse();
    assertThat(STRING_ARRAY.isAssignableFrom(STRING_ARRAY)).isTrue();
    assertThat(STRING_ARRAY.isAssignableFrom(BLOB)).isFalse();
    assertThat(STRING_ARRAY.isAssignableFrom(BLOB_ARRAY)).isFalse();
    assertThat(STRING_ARRAY.isAssignableFrom(FILE)).isFalse();
    assertThat(STRING_ARRAY.isAssignableFrom(FILE_ARRAY)).isFalse();
    assertThat(STRING_ARRAY.isAssignableFrom(EMPTY_ARRAY)).isTrue();

    assertThat(BLOB.isAssignableFrom(ANY)).isFalse();
    assertThat(BLOB.isAssignableFrom(STRING)).isFalse();
    assertThat(BLOB.isAssignableFrom(STRING_ARRAY)).isFalse();
    assertThat(BLOB.isAssignableFrom(BLOB)).isTrue();
    assertThat(BLOB.isAssignableFrom(BLOB_ARRAY)).isFalse();
    assertThat(BLOB.isAssignableFrom(FILE)).isTrue();
    assertThat(BLOB.isAssignableFrom(FILE_ARRAY)).isFalse();
    assertThat(BLOB.isAssignableFrom(EMPTY_ARRAY)).isFalse();

    assertThat(BLOB_ARRAY.isAssignableFrom(ANY)).isFalse();
    assertThat(BLOB_ARRAY.isAssignableFrom(STRING)).isFalse();
    assertThat(BLOB_ARRAY.isAssignableFrom(STRING_ARRAY)).isFalse();
    assertThat(BLOB_ARRAY.isAssignableFrom(BLOB)).isFalse();
    assertThat(BLOB_ARRAY.isAssignableFrom(BLOB_ARRAY)).isTrue();
    assertThat(BLOB_ARRAY.isAssignableFrom(FILE)).isFalse();
    assertThat(BLOB_ARRAY.isAssignableFrom(FILE_ARRAY)).isTrue();
    assertThat(BLOB_ARRAY.isAssignableFrom(EMPTY_ARRAY)).isTrue();

    assertThat(FILE.isAssignableFrom(ANY)).isFalse();
    assertThat(FILE.isAssignableFrom(STRING)).isFalse();
    assertThat(FILE.isAssignableFrom(STRING_ARRAY)).isFalse();
    assertThat(FILE.isAssignableFrom(BLOB)).isFalse();
    assertThat(FILE.isAssignableFrom(BLOB_ARRAY)).isFalse();
    assertThat(FILE.isAssignableFrom(FILE)).isTrue();
    assertThat(FILE.isAssignableFrom(FILE_ARRAY)).isFalse();
    assertThat(FILE.isAssignableFrom(EMPTY_ARRAY)).isFalse();

    assertThat(FILE_ARRAY.isAssignableFrom(ANY)).isFalse();
    assertThat(FILE_ARRAY.isAssignableFrom(STRING)).isFalse();
    assertThat(FILE_ARRAY.isAssignableFrom(STRING_ARRAY)).isFalse();
    assertThat(FILE_ARRAY.isAssignableFrom(BLOB)).isFalse();
    assertThat(FILE_ARRAY.isAssignableFrom(BLOB_ARRAY)).isFalse();
    assertThat(FILE_ARRAY.isAssignableFrom(FILE)).isFalse();
    assertThat(FILE_ARRAY.isAssignableFrom(FILE_ARRAY)).isTrue();
    assertThat(FILE_ARRAY.isAssignableFrom(EMPTY_ARRAY)).isTrue();

    assertThat(EMPTY_ARRAY.isAssignableFrom(ANY)).isFalse();
    assertThat(EMPTY_ARRAY.isAssignableFrom(STRING)).isFalse();
    assertThat(EMPTY_ARRAY.isAssignableFrom(STRING_ARRAY)).isFalse();
    assertThat(EMPTY_ARRAY.isAssignableFrom(BLOB)).isFalse();
    assertThat(EMPTY_ARRAY.isAssignableFrom(BLOB_ARRAY)).isFalse();
    assertThat(EMPTY_ARRAY.isAssignableFrom(FILE)).isFalse();
    assertThat(EMPTY_ARRAY.isAssignableFrom(FILE_ARRAY)).isFalse();
    assertThat(EMPTY_ARRAY.isAssignableFrom(EMPTY_ARRAY)).isTrue();
  }

  @Test
  public void superTypes() throws Exception {
    assertThat(STRING.superTypes()).containsOnly(ANY);
    assertThat(BLOB.superTypes()).containsOnly(ANY);
    assertThat(FILE.superTypes()).containsOnly(BLOB);

    assertThat(STRING_ARRAY.superTypes()).containsOnly(ANY);
    assertThat(BLOB_ARRAY.superTypes()).containsOnly(ANY);
    assertThat(FILE_ARRAY.superTypes()).containsOnly(BLOB_ARRAY);
    assertThat(EMPTY_ARRAY.superTypes()).containsOnly(STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY);
  }

  @Test
  public void arrayElemTypes() throws Exception {
    assertThat(STRING_ARRAY.elemType()).isEqualTo(STRING);
    assertThat(BLOB_ARRAY.elemType()).isEqualTo(BLOB);
    assertThat(FILE_ARRAY.elemType()).isEqualTo(FILE);
    assertThat(EMPTY_ARRAY.elemType()).isEqualTo(ANY);
  }

  @Test
  public void equalsAndHashCode() throws Exception {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(ANY);
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
