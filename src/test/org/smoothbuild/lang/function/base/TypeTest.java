package org.smoothbuild.lang.function.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.function.base.Type.BLOB;
import static org.smoothbuild.lang.function.base.Type.EMPTY_SET;
import static org.smoothbuild.lang.function.base.Type.FILE;
import static org.smoothbuild.lang.function.base.Type.FILE_SET;
import static org.smoothbuild.lang.function.base.Type.JAVA_PARAM_TO_SMOOTH;
import static org.smoothbuild.lang.function.base.Type.JAVA_RESULT_TO_SMOOTH;
import static org.smoothbuild.lang.function.base.Type.PARAM_TYPES;
import static org.smoothbuild.lang.function.base.Type.RESULT_TYPES;
import static org.smoothbuild.lang.function.base.Type.STRING;
import static org.smoothbuild.lang.function.base.Type.STRING_SET;
import static org.smoothbuild.lang.function.base.Type.javaParamTypetoType;
import static org.smoothbuild.lang.function.base.Type.javaResultTypetoType;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Type.EmptySet;
import org.smoothbuild.lang.function.value.Blob;
import org.smoothbuild.lang.function.value.File;
import org.smoothbuild.lang.function.value.FileSet;
import org.smoothbuild.lang.function.value.StringSet;
import org.smoothbuild.lang.function.value.StringValue;

import com.google.inject.TypeLiteral;

public class TypeTest {

  @Test
  public void isAssignableFrom() throws Exception {
    assertThat(STRING.isAssignableFrom(STRING)).isTrue();
    assertThat(STRING.isAssignableFrom(STRING_SET)).isFalse();
    assertThat(STRING.isAssignableFrom(BLOB)).isFalse();
    assertThat(STRING.isAssignableFrom(FILE)).isFalse();
    assertThat(STRING.isAssignableFrom(FILE_SET)).isFalse();
    assertThat(STRING.isAssignableFrom(EMPTY_SET)).isFalse();

    assertThat(STRING_SET.isAssignableFrom(STRING)).isFalse();
    assertThat(STRING_SET.isAssignableFrom(STRING_SET)).isTrue();
    assertThat(STRING_SET.isAssignableFrom(BLOB)).isFalse();
    assertThat(STRING_SET.isAssignableFrom(FILE)).isFalse();
    assertThat(STRING_SET.isAssignableFrom(FILE_SET)).isFalse();
    assertThat(STRING_SET.isAssignableFrom(EMPTY_SET)).isTrue();

    assertThat(BLOB.isAssignableFrom(STRING)).isFalse();
    assertThat(BLOB.isAssignableFrom(STRING_SET)).isFalse();
    assertThat(BLOB.isAssignableFrom(BLOB)).isTrue();
    assertThat(BLOB.isAssignableFrom(FILE)).isFalse();
    assertThat(BLOB.isAssignableFrom(FILE_SET)).isFalse();
    assertThat(BLOB.isAssignableFrom(EMPTY_SET)).isFalse();

    assertThat(FILE.isAssignableFrom(STRING)).isFalse();
    assertThat(FILE.isAssignableFrom(STRING_SET)).isFalse();
    assertThat(FILE.isAssignableFrom(BLOB)).isFalse();
    assertThat(FILE.isAssignableFrom(FILE)).isTrue();
    assertThat(FILE.isAssignableFrom(FILE_SET)).isFalse();
    assertThat(FILE.isAssignableFrom(EMPTY_SET)).isFalse();

    assertThat(FILE_SET.isAssignableFrom(STRING)).isFalse();
    assertThat(FILE_SET.isAssignableFrom(STRING_SET)).isFalse();
    assertThat(FILE_SET.isAssignableFrom(BLOB)).isFalse();
    assertThat(FILE_SET.isAssignableFrom(FILE)).isFalse();
    assertThat(FILE_SET.isAssignableFrom(FILE_SET)).isTrue();
    assertThat(FILE_SET.isAssignableFrom(EMPTY_SET)).isTrue();

    assertThat(EMPTY_SET.isAssignableFrom(STRING)).isFalse();
    assertThat(EMPTY_SET.isAssignableFrom(STRING_SET)).isFalse();
    assertThat(EMPTY_SET.isAssignableFrom(BLOB)).isFalse();
    assertThat(EMPTY_SET.isAssignableFrom(FILE)).isFalse();
    assertThat(EMPTY_SET.isAssignableFrom(FILE_SET)).isFalse();
    assertThat(EMPTY_SET.isAssignableFrom(EMPTY_SET)).isTrue();
  }

  @Test
  public void equalsAndHashCode() throws Exception {
    assertThat(STRING).isEqualTo(STRING);
    assertThat(STRING).isNotEqualTo(STRING_SET);
    assertThat(STRING).isNotEqualTo(BLOB);
    assertThat(STRING).isNotEqualTo(FILE);
    assertThat(STRING).isNotEqualTo(FILE_SET);

    assertThat(STRING_SET).isNotEqualTo(STRING);
    assertThat(STRING_SET).isEqualTo(STRING_SET);
    assertThat(STRING_SET).isNotEqualTo(BLOB);
    assertThat(STRING_SET).isNotEqualTo(FILE);
    assertThat(STRING_SET).isNotEqualTo(FILE_SET);

    assertThat(BLOB).isNotEqualTo(STRING);
    assertThat(BLOB).isNotEqualTo(STRING_SET);
    assertThat(BLOB).isEqualTo(BLOB);
    assertThat(BLOB).isNotEqualTo(FILE);
    assertThat(BLOB).isNotEqualTo(FILE_SET);

    assertThat(FILE).isNotEqualTo(STRING);
    assertThat(FILE).isNotEqualTo(STRING_SET);
    assertThat(FILE).isNotEqualTo(BLOB);
    assertThat(FILE).isEqualTo(FILE);
    assertThat(FILE).isNotEqualTo(FILE_SET);

    assertThat(FILE_SET).isNotEqualTo(STRING);
    assertThat(FILE_SET).isNotEqualTo(STRING_SET);
    assertThat(FILE_SET).isNotEqualTo(BLOB);
    assertThat(FILE_SET).isNotEqualTo(FILE);
    assertThat(FILE_SET).isEqualTo(FILE_SET);

    assertThat(STRING.hashCode()).isNotEqualTo(STRING_SET.hashCode());
    assertThat(STRING.hashCode()).isNotEqualTo(FILE.hashCode());
    assertThat(STRING.hashCode()).isNotEqualTo(BLOB.hashCode());
    assertThat(STRING.hashCode()).isNotEqualTo(FILE_SET.hashCode());
    assertThat(STRING.hashCode()).isNotEqualTo(EMPTY_SET.hashCode());

    assertThat(STRING_SET.hashCode()).isNotEqualTo(STRING.hashCode());
    assertThat(STRING_SET.hashCode()).isNotEqualTo(FILE.hashCode());
    assertThat(STRING_SET.hashCode()).isNotEqualTo(BLOB.hashCode());
    assertThat(STRING_SET.hashCode()).isNotEqualTo(FILE_SET.hashCode());
    assertThat(STRING_SET.hashCode()).isNotEqualTo(EMPTY_SET.hashCode());

    assertThat(BLOB.hashCode()).isNotEqualTo(STRING.hashCode());
    assertThat(BLOB.hashCode()).isNotEqualTo(STRING_SET.hashCode());
    assertThat(BLOB.hashCode()).isNotEqualTo(FILE.hashCode());
    assertThat(BLOB.hashCode()).isNotEqualTo(FILE_SET.hashCode());
    assertThat(BLOB.hashCode()).isNotEqualTo(EMPTY_SET.hashCode());

    assertThat(FILE.hashCode()).isNotEqualTo(STRING.hashCode());
    assertThat(FILE.hashCode()).isNotEqualTo(STRING_SET.hashCode());
    assertThat(FILE.hashCode()).isNotEqualTo(BLOB.hashCode());
    assertThat(FILE.hashCode()).isNotEqualTo(FILE_SET.hashCode());
    assertThat(FILE.hashCode()).isNotEqualTo(EMPTY_SET.hashCode());

    assertThat(FILE_SET.hashCode()).isNotEqualTo(STRING.hashCode());
    assertThat(FILE_SET.hashCode()).isNotEqualTo(STRING_SET.hashCode());
    assertThat(FILE_SET.hashCode()).isNotEqualTo(BLOB.hashCode());
    assertThat(FILE_SET.hashCode()).isNotEqualTo(FILE.hashCode());
    assertThat(FILE_SET.hashCode()).isNotEqualTo(EMPTY_SET.hashCode());

    assertThat(EMPTY_SET.hashCode()).isNotEqualTo(STRING.hashCode());
    assertThat(EMPTY_SET.hashCode()).isNotEqualTo(STRING_SET.hashCode());
    assertThat(EMPTY_SET.hashCode()).isNotEqualTo(BLOB.hashCode());
    assertThat(EMPTY_SET.hashCode()).isNotEqualTo(FILE.hashCode());
    assertThat(EMPTY_SET.hashCode()).isNotEqualTo(FILE_SET.hashCode());
  }

  @Test
  public void testToString() throws Exception {
    assertThat(STRING.toString()).isEqualTo("'String'");
  }

  @Test
  public void testJavaParamTypetoType() {
    assertThat(javaParamTypetoType(type(StringValue.class))).isEqualTo(STRING);
    assertThat(javaParamTypetoType(type(StringSet.class))).isEqualTo(STRING_SET);
    assertThat(javaParamTypetoType(type(File.class))).isEqualTo(FILE);
    assertThat(javaParamTypetoType(type(FileSet.class))).isEqualTo(FILE_SET);
  }

  @Test
  public void voidIsNotValidParamType() throws Exception {
    assertThat(javaParamTypetoType(type(Void.class))).isNull();
  }

  @Test
  public void blobIsNotValidParamType() throws Exception {
    assertThat(javaParamTypetoType(type(Blob.class))).isNull();
  }

  @Test
  public void emptySetIsNotValidParamType() throws Exception {
    assertThat(javaParamTypetoType(type(EmptySet.class))).isNull();
  }

  @Test
  public void testJavaResultTypetoType() {
    assertThat(javaResultTypetoType(type(StringValue.class))).isEqualTo(STRING);
    assertThat(javaResultTypetoType(type(StringSet.class))).isEqualTo(STRING_SET);
    assertThat(javaResultTypetoType(type(File.class))).isEqualTo(FILE);
    assertThat(javaResultTypetoType(type(FileSet.class))).isEqualTo(FILE_SET);
  }

  @Test
  public void javaResultToSmoothContainsAllResultTypes() throws Exception {
    Type[] array = new Type[] {};
    assertThat(JAVA_RESULT_TO_SMOOTH.values()).containsOnly(RESULT_TYPES.toArray(array));
  }

  @Test
  public void javaParamToSmoothContainsAllResultTypes() throws Exception {
    Type[] array = new Type[] {};
    assertThat(JAVA_PARAM_TO_SMOOTH.values()).containsOnly(PARAM_TYPES.toArray(array));
  }

  private static TypeLiteral<?> type(Class<?> klass) {
    return TypeLiteral.get(klass);
  }
}
