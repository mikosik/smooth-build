package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.spec.val.NativeLambdaSpec;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class NativeLambdaTest extends TestingContext {
  @Test
  public void creating_lambda_with_less_default_arguments_than_specified_in_its_spec_causes_exception() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    assertCall(() -> nativeLambdaVal(lambdaSpec, strVal(), blobVal(), list()))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void creating_lambda_with_more_default_arguments_than_specified_in_its_spec_causes_exception() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    assertCall(() -> nativeLambdaVal(lambdaSpec, strVal(), blobVal(), list(constExpr(), constExpr())))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void setting_class_binary_name_to_null_throws_exception() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    assertCall(() -> nativeLambdaVal(lambdaSpec, null, blobVal(), list(constExpr())))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void setting_native_jar_to_null_throws_exception() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    assertCall(() -> nativeLambdaVal(lambdaSpec, strVal(), null, list(constExpr())))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void setting_default_arguments_to_null_throws_exception() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    assertCall(() -> nativeLambdaVal(lambdaSpec, strVal(), blobVal(), null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void setting_default_argument_to_null_throws_exception() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    assertCall(() -> nativeLambdaVal(lambdaSpec, strVal(), blobVal(), asList(null)))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void spec_of_native_lambda_is_native_lambda_spec() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    assertThat(nativeLambdaVal(lambdaSpec, strVal(), blobVal(), asList(constExpr())).spec())
        .isEqualTo(lambdaSpec);
  }

  @Test
  public void class_binary_name_contains_object_passed_during_construction() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    Str classBinaryName = strVal("some.Class");
    assertThat(nativeLambdaVal(lambdaSpec, classBinaryName, blobVal(), asList(constExpr()))
        .classBinaryName())
        .isEqualTo(classBinaryName);
  }

  @Test
  public void native_jar_contains_object_passed_during_construction() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    Blob nativeJar = blobVal(ByteString.encodeUtf8("native jar"));
    assertThat(nativeLambdaVal(lambdaSpec, strVal(), nativeJar, asList(constExpr()))
        .nativeJar())
        .isEqualTo(nativeJar);
  }

  @Test
  public void default_arguments_contains_object_passed_as_default_arguments() {
    NativeLambdaSpec spec = nativeLambdaSpec(intSpec(), boolSpec());
    List<Expr> defaultArguments = asList(constExpr(intVal(33)));
    assertThat(nativeLambdaVal(spec, strVal(), blobVal(), defaultArguments).defaultArguments())
        .isEqualTo(defaultArguments);
  }

  @Test
  public void lambdas_with_equal_stuff_are_equal() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    NativeLambda lambda1 = nativeLambdaVal(lambdaSpec, strVal(), blobVal(), asList(constExpr()));
    NativeLambda lambda2 = nativeLambdaVal(lambdaSpec, strVal(), blobVal(), asList(constExpr()));
    assertThat(lambda1)
        .isEqualTo(lambda2);
  }

  @Test
  public void lambdas_with_different_class_binary_name_are_not_equal() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    NativeLambda lambda1 = nativeLambdaVal(
        lambdaSpec, strVal("abc"), blobVal(), asList(constExpr()));
    NativeLambda lambda2 = nativeLambdaVal(
        lambdaSpec, strVal("def"), blobVal(), asList(constExpr()));assertThat(lambda1)
        .isNotEqualTo(lambda2);
  }

  @Test
  public void lambdas_with_different_native_jar_are_not_equal() {
    NativeLambdaSpec spec = nativeLambdaSpec(intSpec(), boolSpec());
    NativeLambda lambda1 = nativeLambdaVal(
        spec, strVal(), blobVal(ByteString.of((byte) 1)), asList(constExpr()));
    NativeLambda lambda2 = nativeLambdaVal(
        spec, strVal(), blobVal(ByteString.of((byte) 2)), asList(constExpr()));
    assertThat(lambda1)
        .isNotEqualTo(lambda2);
  }

  @Test
  public void lambdas_with_different_default_arguments_are_not_equal() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    NativeLambda lambda1 = nativeLambdaVal(
        lambdaSpec, strVal(), blobVal(), asList(constExpr(intVal(1))));
    NativeLambda lambda2 = nativeLambdaVal(
        lambdaSpec, strVal(), blobVal(), asList(constExpr(intVal(2))));
    assertThat(lambda1)
        .isNotEqualTo(lambda2);
  }

  @Test
  public void lambdas_with_equal_stuff_have_equal_hashes() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    NativeLambda lambda1 = nativeLambdaVal(lambdaSpec, strVal(), blobVal(), asList(constExpr()));
    NativeLambda lambda2 = nativeLambdaVal(lambdaSpec, strVal(), blobVal(), asList(constExpr()));
    assertThat(lambda1.hash())
        .isEqualTo(lambda2.hash());
  }

  @Test
  public void lambdas_with_different_class_binary_names_have_not_equal_hashes() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    NativeLambda lambda1 = nativeLambdaVal(
        lambdaSpec, strVal("abc"), blobVal(), asList(constExpr()));
    NativeLambda lambda2 = nativeLambdaVal(
        lambdaSpec, strVal("def"), blobVal(), asList(constExpr()));
    assertThat(lambda1.hash())
        .isNotEqualTo(lambda2.hash());
  }

  @Test
  public void lambdas_with_native_jar_names_have_not_equal_hashes() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    NativeLambda lambda1 = nativeLambdaVal(
        lambdaSpec, strVal(), blobVal(ByteString.of((byte) 1)), asList(constExpr()));
    NativeLambda lambda2 = nativeLambdaVal(
        lambdaSpec, strVal(), blobVal(ByteString.of((byte) 2)), asList(constExpr()));
    assertThat(lambda1.hash())
        .isNotEqualTo(lambda2.hash());
  }

  @Test
  public void lambdas_with_different_default_arguments_have_not_equal_hashes() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    NativeLambda lambda1 = nativeLambdaVal(
        lambdaSpec, strVal(), blobVal(), asList(constExpr(intVal(1))));
    NativeLambda lambda2 = nativeLambdaVal(
        lambdaSpec, strVal(), blobVal(), asList(constExpr(intVal(2))));
    assertThat(lambda1.hash())
        .isNotEqualTo(lambda2.hash());
  }

  @Test
  public void lambdas_with_equal_stuff_have_equal_hash_code() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    NativeLambda lambda1 = nativeLambdaVal(lambdaSpec, strVal(), blobVal(), asList(constExpr()));
    NativeLambda lambda2 = nativeLambdaVal(lambdaSpec, strVal(), blobVal(), asList(constExpr()));
    assertThat(lambda1.hashCode())
        .isEqualTo(lambda2.hashCode());
  }

  @Test
  public void lambdas_with_different_class_binary_names_have_not_equal_hash_code() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    NativeLambda lambda1 = nativeLambdaVal(
        lambdaSpec, strVal("abc"), blobVal(), asList(constExpr()));
    NativeLambda lambda2 = nativeLambdaVal(
        lambdaSpec, strVal("def"), blobVal(), asList(constExpr()));
    assertThat(lambda1.hashCode())
        .isNotEqualTo(lambda2.hashCode());
  }

  @Test
  public void lambdas_with_native_jar_names_have_not_equal_hash_code() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    NativeLambda lambda1 = nativeLambdaVal(
        lambdaSpec, strVal(), blobVal(ByteString.of((byte) 1)), asList(constExpr()));
    NativeLambda lambda2 = nativeLambdaVal(
        lambdaSpec, strVal(), blobVal(ByteString.of((byte) 2)), asList(constExpr()));
    assertThat(lambda1.hashCode())
        .isNotEqualTo(lambda2.hashCode());
  }

  @Test
  public void lambdas_with_different_default_arguments_have_not_equal_hash_code() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    NativeLambda lambda1 = nativeLambdaVal(lambdaSpec, strVal(), blobVal(), asList(constExpr(intVal(1))));
    NativeLambda lambda2 = nativeLambdaVal(lambdaSpec, strVal(), blobVal(), asList(constExpr(intVal(2))));
    assertThat(lambda1.hashCode())
        .isNotEqualTo(lambda2.hashCode());
  }

  @Test
  public void lambdas_can_be_read_by_hash() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    NativeLambda lambda = nativeLambdaVal(lambdaSpec, strVal(), blobVal(), asList(constExpr()));
    assertThat(objectDbOther().get(lambda.hash()))
        .isEqualTo(lambda);
  }

  @Test
  public void lambdas_read_by_hash_have_equal_class_binary_name() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    NativeLambda lambda = nativeLambdaVal(lambdaSpec, strVal(), blobVal(), asList(constExpr()));
    NativeLambda lambdaRead = (NativeLambda) objectDbOther().get(lambda.hash());
    assertThat(lambda.classBinaryName())
        .isEqualTo(lambdaRead.classBinaryName());
  }

  @Test
  public void lambdas_read_by_hash_have_equal_native_jar() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    NativeLambda lambda = nativeLambdaVal(lambdaSpec, strVal(), blobVal(), asList(constExpr()));
    NativeLambda lambdaRead = (NativeLambda) objectDbOther().get(lambda.hash());
    assertThat(lambda.nativeJar())
        .isEqualTo(lambdaRead.nativeJar());
  }

  @Test
  public void lambdas_read_by_hash_have_equal_default_arguments() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    NativeLambda lambda = nativeLambdaVal(lambdaSpec, strVal(), blobVal(), asList(constExpr()));
    NativeLambda lambdaRead = (NativeLambda) objectDbOther().get(lambda.hash());
    assertThat(lambda.defaultArguments())
        .isEqualTo(lambdaRead.defaultArguments());
  }

  @Test
  public void to_string() {
    NativeLambdaSpec lambdaSpec = nativeLambdaSpec(intSpec(), boolSpec());
    NativeLambda lambda = nativeLambdaVal(lambdaSpec, strVal(), blobVal(), asList(constExpr()));
    assertThat(lambda.toString())
        .isEqualTo("NativeLambda(INT(BOOL)):" + lambda.hash());
  }
}