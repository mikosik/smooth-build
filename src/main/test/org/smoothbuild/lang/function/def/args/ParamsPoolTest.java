package org.smoothbuild.lang.function.def.args;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.NIL;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.smoothbuild.lang.function.base.Param.param;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.base.Params;

import com.google.common.collect.ImmutableMap;

public class ParamsPoolTest {
  Param string = param(STRING, "string1");
  Param blob = param(BLOB, "blob");
  Param file = param(FILE, "file1");

  Param stringArray = param(STRING_ARRAY, "stringArray");
  Param blobArray = param(BLOB_ARRAY, "blobArray");
  Param fileArray = param(FILE_ARRAY, "fileArray");

  Param stringRequired = param(STRING, "stringRequired", true);
  Param blobRequired = param(BLOB, "blobRequired", true);
  Param fileRequired = param(FILE, "fileRequired", true);

  Param stringArrayRequired = param(STRING_ARRAY, "stringArrayRequired", true);
  Param blobArrayRequired = param(BLOB_ARRAY, "blobArrayRequired", true);
  Param fileArrayRequired = param(FILE_ARRAY, "fileArrayRequired", true);

  ImmutableMap<String, Param> params = Params.map(string, stringRequired, blob, blobRequired, file,
      fileRequired, stringArray, stringArrayRequired, blobArray, blobArrayRequired, fileArray,
      fileArrayRequired);
  ParamsPool paramsPool = new ParamsPool(params);

  @Test
  public void paramCanBeTaken() {
    assertThat(paramsPool.take(string)).isSameAs(string);
  }

  @Test
  public void takingUnknownParamThrowsException() {
    try {
      paramsPool.take(param(STRING, "someName"));
      fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void paramCannotBeTakenTwice() {
    paramsPool.take(string);
    try {
      paramsPool.take(string);
      fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void paramCanBeTakenByName() {
    assertThat(paramsPool.takeByName(string.name())).isSameAs(string);
  }

  @Test
  public void takingUnknownParamByNameThrowsException() {
    try {
      paramsPool.takeByName("someName");
      fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void paramCannotBeTakenByNameTwice() {
    paramsPool.takeByName(string.name());
    try {
      paramsPool.takeByName(string.name());
      fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  // availableForType()

  @Test
  public void string_param_is_available_for_string_argument() throws Exception {
    paramsPool = new ParamsPool(params);

    assertThat(paramsPool.availableForType(STRING).optionalParams()).containsOnly(string);
    assertThat(paramsPool.availableForType(STRING).requiredParams()).containsOnly(stringRequired);
  }

  @Test
  public void blob_param_is_available_for_blob_argument() throws Exception {
    paramsPool = new ParamsPool(params);

    assertThat(paramsPool.availableForType(BLOB).optionalParams()).containsOnly(blob);
    assertThat(paramsPool.availableForType(BLOB).requiredParams()).containsOnly(blobRequired);
  }

  @Test
  public void blob_and_file_params_are_available_for_file_argument() throws Exception {
    paramsPool = new ParamsPool(params);

    assertThat(paramsPool.availableForType(FILE).optionalParams()).containsOnly(blob, file);
    assertThat(paramsPool.availableForType(FILE).requiredParams()).containsOnly(blobRequired,
        fileRequired);
  }

  @Test
  public void string_array_param_is_available_for_string_array_argument() throws Exception {
    paramsPool = new ParamsPool(params);

    assertThat(paramsPool.availableForType(STRING_ARRAY).optionalParams())
        .containsOnly(stringArray);
    assertThat(paramsPool.availableForType(STRING_ARRAY).requiredParams()).containsOnly(
        stringArrayRequired);
  }

  @Test
  public void blob_array_param_is_available_for_blob_array_argument() throws Exception {
    paramsPool = new ParamsPool(params);

    assertThat(paramsPool.availableForType(BLOB_ARRAY).optionalParams()).containsOnly(blobArray);
    assertThat(paramsPool.availableForType(BLOB_ARRAY).requiredParams()).containsOnly(
        blobArrayRequired);
  }

  @Test
  public void blob_array_and_file_array_params_are_available_for_file_array_argument()
      throws Exception {
    paramsPool = new ParamsPool(params);

    assertThat(paramsPool.availableForType(FILE_ARRAY).optionalParams()).containsOnly(blobArray,
        fileArray);
    assertThat(paramsPool.availableForType(FILE_ARRAY).requiredParams()).containsOnly(
        blobArrayRequired, fileArrayRequired);
  }

  @Test
  public void array_params_are_available_for_nil_argument() throws Exception {
    paramsPool = new ParamsPool(params);

    assertThat(paramsPool.availableForType(NIL).optionalParams()).containsOnly(stringArray,
        blobArray, fileArray);
    assertThat(paramsPool.availableForType(NIL).requiredParams()).containsOnly(stringArrayRequired,
        blobArrayRequired, fileArrayRequired);
  }

  // availableRequiredParams()

  @Test
  public void availableRequiredParams() throws Exception {
    assertThat(paramsPool.availableRequiredParams()).containsOnly(stringRequired, blobRequired,
        fileRequired, stringArrayRequired, blobArrayRequired, fileArrayRequired);
  }

  @Test
  public void available_required_params_does_not_contain_taken_param() throws Exception {
    ImmutableMap<String, Param> params = Params.map(stringRequired, fileRequired);
    ParamsPool paramsPool = new ParamsPool(params);

    paramsPool.take(stringRequired);
    assertThat(paramsPool.availableRequiredParams()).containsOnly(fileRequired);
  }
}
