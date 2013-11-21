package org.smoothbuild.lang.function.def.args;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.type.Type.BLOB;
import static org.smoothbuild.lang.type.Type.BLOB_SET;
import static org.smoothbuild.lang.type.Type.EMPTY_SET;
import static org.smoothbuild.lang.type.Type.FILE;
import static org.smoothbuild.lang.type.Type.FILE_SET;
import static org.smoothbuild.lang.type.Type.STRING;
import static org.smoothbuild.lang.type.Type.STRING_SET;
import static org.smoothbuild.testing.lang.function.base.ParamTester.params;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Param;

import com.google.common.collect.ImmutableMap;

public class ParamsPoolTest {
  Param string = param(STRING, "string1");
  Param blob = param(BLOB, "blob");
  Param file = param(FILE, "file1");

  Param stringSet = param(STRING_SET, "stringSet");
  Param blobSet = param(BLOB_SET, "blobSet");
  Param fileSet = param(FILE_SET, "fileSet");

  Param stringRequired = param(STRING, "stringRequired", true);
  Param blobRequired = param(BLOB, "blobRequired", true);
  Param fileRequired = param(FILE, "fileRequired", true);

  Param stringSetRequired = param(STRING_SET, "stringSetRequired", true);
  Param blobSetRequired = param(BLOB_SET, "blobSetRequired", true);
  Param fileSetRequired = param(FILE_SET, "fileSetRequired", true);

  ImmutableMap<String, Param> params = params(string, stringRequired, blob, blobRequired, file,
      fileRequired, stringSet, stringSetRequired, blobSet, blobSetRequired, fileSet,
      fileSetRequired);
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
  public void string_set_param_is_available_for_string_set_argument() throws Exception {
    paramsPool = new ParamsPool(params);

    assertThat(paramsPool.availableForType(STRING_SET).optionalParams()).containsOnly(stringSet);
    assertThat(paramsPool.availableForType(STRING_SET).requiredParams()).containsOnly(
        stringSetRequired);
  }

  @Test
  public void blob_set_param_is_available_for_blob_set_argument() throws Exception {
    paramsPool = new ParamsPool(params);

    assertThat(paramsPool.availableForType(BLOB_SET).optionalParams()).containsOnly(blobSet);
    assertThat(paramsPool.availableForType(BLOB_SET).requiredParams())
        .containsOnly(blobSetRequired);
  }

  @Test
  public void blob_set_and_file_set_params_are_available_for_file_set_argument() throws Exception {
    paramsPool = new ParamsPool(params);

    assertThat(paramsPool.availableForType(FILE_SET).optionalParams()).containsOnly(blobSet,
        fileSet);
    assertThat(paramsPool.availableForType(FILE_SET).requiredParams()).containsOnly(
        blobSetRequired, fileSetRequired);
  }

  @Test
  public void set_params_are_available_for_empty_set_argument() throws Exception {
    paramsPool = new ParamsPool(params);

    assertThat(paramsPool.availableForType(EMPTY_SET).optionalParams()).containsOnly(stringSet,
        blobSet, fileSet);
    assertThat(paramsPool.availableForType(EMPTY_SET).requiredParams()).containsOnly(
        stringSetRequired, blobSetRequired, fileSetRequired);
  }

  // availableRequiredParams()

  @Test
  public void availableRequiredParams() throws Exception {
    assertThat(paramsPool.availableRequiredParams()).containsOnly(stringRequired, blobRequired,
        fileRequired, stringSetRequired, blobSetRequired, fileSetRequired);
  }

  @Test
  public void available_required_params_does_not_contain_taken_param() throws Exception {
    ImmutableMap<String, Param> params = params(stringRequired, fileRequired);
    ParamsPool paramsPool = new ParamsPool(params);

    paramsPool.take(stringRequired);
    assertThat(paramsPool.availableRequiredParams()).containsOnly(fileRequired);
  }
}
