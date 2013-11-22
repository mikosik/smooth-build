package org.smoothbuild.lang.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.type.Type.BLOB;
import static org.smoothbuild.lang.type.Type.BLOB_ARRAY;
import static org.smoothbuild.lang.type.Type.EMPTY_ARRAY;
import static org.smoothbuild.lang.type.Type.FILE;
import static org.smoothbuild.lang.type.Type.FILE_ARRAY;
import static org.smoothbuild.lang.type.Type.JAVA_PARAM_TO_SMOOTH;
import static org.smoothbuild.lang.type.Type.JAVA_RESULT_TO_SMOOTH;
import static org.smoothbuild.lang.type.Type.PARAM_TYPES;
import static org.smoothbuild.lang.type.Type.RESULT_TYPES;
import static org.smoothbuild.lang.type.Type.STRING;
import static org.smoothbuild.lang.type.Type.STRING_ARRAY;
import static org.smoothbuild.lang.type.Type.javaParamTypetoType;
import static org.smoothbuild.lang.type.Type.javaResultTypetoType;

import org.junit.Test;
import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.Blob;
import org.smoothbuild.lang.type.File;
import org.smoothbuild.lang.type.StringValue;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Type.EmptyArray;

import com.google.inject.TypeLiteral;

public class TypeTest {

  @Test
  public void isAssignableFrom() throws Exception {
    assertThat(STRING.isAssignableFrom(STRING)).isTrue();
    assertThat(STRING.isAssignableFrom(STRING_ARRAY)).isFalse();
    assertThat(STRING.isAssignableFrom(BLOB)).isFalse();
    assertThat(STRING.isAssignableFrom(BLOB_ARRAY)).isFalse();
    assertThat(STRING.isAssignableFrom(FILE)).isFalse();
    assertThat(STRING.isAssignableFrom(FILE_ARRAY)).isFalse();
    assertThat(STRING.isAssignableFrom(EMPTY_ARRAY)).isFalse();

    assertThat(STRING_ARRAY.isAssignableFrom(STRING)).isFalse();
    assertThat(STRING_ARRAY.isAssignableFrom(STRING_ARRAY)).isTrue();
    assertThat(STRING_ARRAY.isAssignableFrom(BLOB)).isFalse();
    assertThat(STRING_ARRAY.isAssignableFrom(BLOB_ARRAY)).isFalse();
    assertThat(STRING_ARRAY.isAssignableFrom(FILE)).isFalse();
    assertThat(STRING_ARRAY.isAssignableFrom(FILE_ARRAY)).isFalse();
    assertThat(STRING_ARRAY.isAssignableFrom(EMPTY_ARRAY)).isTrue();

    assertThat(BLOB.isAssignableFrom(STRING)).isFalse();
    assertThat(BLOB.isAssignableFrom(STRING_ARRAY)).isFalse();
    assertThat(BLOB.isAssignableFrom(BLOB)).isTrue();
    assertThat(BLOB.isAssignableFrom(BLOB_ARRAY)).isFalse();
    assertThat(BLOB.isAssignableFrom(FILE)).isTrue();
    assertThat(BLOB.isAssignableFrom(FILE_ARRAY)).isFalse();
    assertThat(BLOB.isAssignableFrom(EMPTY_ARRAY)).isFalse();

    assertThat(BLOB_ARRAY.isAssignableFrom(STRING)).isFalse();
    assertThat(BLOB_ARRAY.isAssignableFrom(STRING_ARRAY)).isFalse();
    assertThat(BLOB_ARRAY.isAssignableFrom(BLOB)).isFalse();
    assertThat(BLOB_ARRAY.isAssignableFrom(BLOB_ARRAY)).isTrue();
    assertThat(BLOB_ARRAY.isAssignableFrom(FILE)).isFalse();
    assertThat(BLOB_ARRAY.isAssignableFrom(FILE_ARRAY)).isTrue();
    assertThat(BLOB_ARRAY.isAssignableFrom(EMPTY_ARRAY)).isTrue();

    assertThat(FILE.isAssignableFrom(STRING)).isFalse();
    assertThat(FILE.isAssignableFrom(STRING_ARRAY)).isFalse();
    assertThat(FILE.isAssignableFrom(BLOB)).isFalse();
    assertThat(FILE.isAssignableFrom(BLOB_ARRAY)).isFalse();
    assertThat(FILE.isAssignableFrom(FILE)).isTrue();
    assertThat(FILE.isAssignableFrom(FILE_ARRAY)).isFalse();
    assertThat(FILE.isAssignableFrom(EMPTY_ARRAY)).isFalse();

    assertThat(FILE_ARRAY.isAssignableFrom(STRING)).isFalse();
    assertThat(FILE_ARRAY.isAssignableFrom(STRING_ARRAY)).isFalse();
    assertThat(FILE_ARRAY.isAssignableFrom(BLOB)).isFalse();
    assertThat(FILE_ARRAY.isAssignableFrom(BLOB_ARRAY)).isFalse();
    assertThat(FILE_ARRAY.isAssignableFrom(FILE)).isFalse();
    assertThat(FILE_ARRAY.isAssignableFrom(FILE_ARRAY)).isTrue();
    assertThat(FILE_ARRAY.isAssignableFrom(EMPTY_ARRAY)).isTrue();

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
    assertThat(STRING.superTypes()).isEmpty();
    assertThat(BLOB.superTypes()).isEmpty();
    assertThat(FILE.superTypes()).containsOnly(BLOB);

    assertThat(STRING_ARRAY.superTypes()).isEmpty();
    assertThat(BLOB_ARRAY.superTypes()).isEmpty();
    assertThat(FILE_ARRAY.superTypes()).containsOnly(BLOB_ARRAY);
    assertThat(EMPTY_ARRAY.superTypes()).containsOnly(STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY);
  }

  @Test
  public void equalsAndHashCode() throws Exception {
    assertThat(STRING).isEqualTo(STRING);
    assertThat(STRING).isNotEqualTo(STRING_ARRAY);
    assertThat(STRING).isNotEqualTo(BLOB);
    assertThat(STRING).isNotEqualTo(BLOB_ARRAY);
    assertThat(STRING).isNotEqualTo(FILE);
    assertThat(STRING).isNotEqualTo(FILE_ARRAY);

    assertThat(STRING_ARRAY).isNotEqualTo(STRING);
    assertThat(STRING_ARRAY).isEqualTo(STRING_ARRAY);
    assertThat(STRING_ARRAY).isNotEqualTo(BLOB);
    assertThat(STRING_ARRAY).isNotEqualTo(BLOB_ARRAY);
    assertThat(STRING_ARRAY).isNotEqualTo(FILE);
    assertThat(STRING_ARRAY).isNotEqualTo(FILE_ARRAY);

    assertThat(BLOB).isNotEqualTo(STRING);
    assertThat(BLOB).isNotEqualTo(STRING_ARRAY);
    assertThat(BLOB).isEqualTo(BLOB);
    assertThat(BLOB).isNotEqualTo(BLOB_ARRAY);
    assertThat(BLOB).isNotEqualTo(FILE);
    assertThat(BLOB).isNotEqualTo(FILE_ARRAY);

    assertThat(BLOB_ARRAY).isNotEqualTo(STRING);
    assertThat(BLOB_ARRAY).isNotEqualTo(STRING_ARRAY);
    assertThat(BLOB_ARRAY).isNotEqualTo(BLOB);
    assertThat(BLOB_ARRAY).isEqualTo(BLOB_ARRAY);
    assertThat(BLOB_ARRAY).isNotEqualTo(FILE);
    assertThat(BLOB_ARRAY).isNotEqualTo(FILE_ARRAY);

    assertThat(FILE).isNotEqualTo(STRING);
    assertThat(FILE).isNotEqualTo(STRING_ARRAY);
    assertThat(FILE).isNotEqualTo(BLOB);
    assertThat(FILE).isNotEqualTo(BLOB_ARRAY);
    assertThat(FILE).isEqualTo(FILE);
    assertThat(FILE).isNotEqualTo(FILE_ARRAY);

    assertThat(FILE_ARRAY).isNotEqualTo(STRING);
    assertThat(FILE_ARRAY).isNotEqualTo(STRING_ARRAY);
    assertThat(FILE_ARRAY).isNotEqualTo(BLOB);
    assertThat(FILE_ARRAY).isNotEqualTo(BLOB_ARRAY);
    assertThat(FILE_ARRAY).isNotEqualTo(FILE);
    assertThat(FILE_ARRAY).isEqualTo(FILE_ARRAY);

    assertThat(STRING.hashCode()).isNotEqualTo(STRING_ARRAY.hashCode());
    assertThat(STRING.hashCode()).isNotEqualTo(FILE.hashCode());
    assertThat(STRING.hashCode()).isNotEqualTo(BLOB.hashCode());
    assertThat(STRING.hashCode()).isNotEqualTo(BLOB_ARRAY.hashCode());
    assertThat(STRING.hashCode()).isNotEqualTo(FILE_ARRAY.hashCode());
    assertThat(STRING.hashCode()).isNotEqualTo(EMPTY_ARRAY.hashCode());

    assertThat(STRING_ARRAY.hashCode()).isNotEqualTo(STRING.hashCode());
    assertThat(STRING_ARRAY.hashCode()).isNotEqualTo(FILE.hashCode());
    assertThat(STRING_ARRAY.hashCode()).isNotEqualTo(BLOB.hashCode());
    assertThat(STRING_ARRAY.hashCode()).isNotEqualTo(BLOB_ARRAY.hashCode());
    assertThat(STRING_ARRAY.hashCode()).isNotEqualTo(FILE_ARRAY.hashCode());
    assertThat(STRING_ARRAY.hashCode()).isNotEqualTo(EMPTY_ARRAY.hashCode());

    assertThat(BLOB.hashCode()).isNotEqualTo(STRING.hashCode());
    assertThat(BLOB.hashCode()).isNotEqualTo(STRING_ARRAY.hashCode());
    assertThat(BLOB.hashCode()).isNotEqualTo(BLOB_ARRAY.hashCode());
    assertThat(BLOB.hashCode()).isNotEqualTo(FILE.hashCode());
    assertThat(BLOB.hashCode()).isNotEqualTo(FILE_ARRAY.hashCode());
    assertThat(BLOB.hashCode()).isNotEqualTo(EMPTY_ARRAY.hashCode());

    assertThat(BLOB_ARRAY.hashCode()).isNotEqualTo(STRING.hashCode());
    assertThat(BLOB_ARRAY.hashCode()).isNotEqualTo(STRING_ARRAY.hashCode());
    assertThat(BLOB_ARRAY.hashCode()).isNotEqualTo(BLOB.hashCode());
    assertThat(BLOB_ARRAY.hashCode()).isNotEqualTo(FILE.hashCode());
    assertThat(BLOB_ARRAY.hashCode()).isNotEqualTo(FILE_ARRAY.hashCode());
    assertThat(BLOB_ARRAY.hashCode()).isNotEqualTo(EMPTY_ARRAY.hashCode());

    assertThat(FILE.hashCode()).isNotEqualTo(STRING.hashCode());
    assertThat(FILE.hashCode()).isNotEqualTo(STRING_ARRAY.hashCode());
    assertThat(FILE.hashCode()).isNotEqualTo(BLOB.hashCode());
    assertThat(FILE.hashCode()).isNotEqualTo(BLOB_ARRAY.hashCode());
    assertThat(FILE.hashCode()).isNotEqualTo(FILE_ARRAY.hashCode());
    assertThat(FILE.hashCode()).isNotEqualTo(EMPTY_ARRAY.hashCode());

    assertThat(FILE_ARRAY.hashCode()).isNotEqualTo(STRING.hashCode());
    assertThat(FILE_ARRAY.hashCode()).isNotEqualTo(STRING_ARRAY.hashCode());
    assertThat(FILE_ARRAY.hashCode()).isNotEqualTo(BLOB.hashCode());
    assertThat(FILE_ARRAY.hashCode()).isNotEqualTo(BLOB_ARRAY.hashCode());
    assertThat(FILE_ARRAY.hashCode()).isNotEqualTo(FILE.hashCode());
    assertThat(FILE_ARRAY.hashCode()).isNotEqualTo(EMPTY_ARRAY.hashCode());

    assertThat(EMPTY_ARRAY.hashCode()).isNotEqualTo(STRING.hashCode());
    assertThat(EMPTY_ARRAY.hashCode()).isNotEqualTo(STRING_ARRAY.hashCode());
    assertThat(EMPTY_ARRAY.hashCode()).isNotEqualTo(BLOB.hashCode());
    assertThat(EMPTY_ARRAY.hashCode()).isNotEqualTo(BLOB_ARRAY.hashCode());
    assertThat(EMPTY_ARRAY.hashCode()).isNotEqualTo(FILE.hashCode());
    assertThat(EMPTY_ARRAY.hashCode()).isNotEqualTo(FILE_ARRAY.hashCode());
  }

  @Test
  public void testToString() throws Exception {
    assertThat(STRING.toString()).isEqualTo("'String'");
  }

  @Test
  public void testJavaParamTypetoType() {
    assertThat(javaParamTypetoType(type(StringValue.class))).isEqualTo(STRING);
    assertThat(javaParamTypetoType(type(Blob.class))).isEqualTo(BLOB);
    assertThat(javaParamTypetoType(type(File.class))).isEqualTo(FILE);

    assertThat(javaParamTypetoType(new TypeLiteral<Array<StringValue>>() {})).isEqualTo(STRING_ARRAY);
    assertThat(javaParamTypetoType(new TypeLiteral<Array<Blob>>() {})).isEqualTo(BLOB_ARRAY);
    assertThat(javaParamTypetoType(new TypeLiteral<Array<File>>() {})).isEqualTo(FILE_ARRAY);
  }

  @Test
  public void emptyArrayIsNotValidParamType() throws Exception {
    assertThat(javaParamTypetoType(type(EmptyArray.class))).isNull();
  }

  @Test
  public void testJavaResultTypetoType() {
    assertThat(javaResultTypetoType(type(StringValue.class))).isEqualTo(STRING);
    assertThat(javaResultTypetoType(type(Blob.class))).isEqualTo(BLOB);
    assertThat(javaResultTypetoType(type(File.class))).isEqualTo(FILE);

    assertThat(javaResultTypetoType(new TypeLiteral<Array<StringValue>>() {}))
        .isEqualTo(STRING_ARRAY);
    assertThat(javaResultTypetoType(new TypeLiteral<Array<Blob>>() {})).isEqualTo(BLOB_ARRAY);
    assertThat(javaResultTypetoType(new TypeLiteral<Array<File>>() {})).isEqualTo(FILE_ARRAY);
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
