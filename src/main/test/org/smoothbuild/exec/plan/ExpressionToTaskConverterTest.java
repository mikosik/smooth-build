package org.smoothbuild.exec.plan;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.smoothbuild.lang.TestingLang.call;
import static org.smoothbuild.lang.TestingLang.function;
import static org.smoothbuild.lang.TestingLang.parameter;
import static org.smoothbuild.lang.TestingLang.parameterRef;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.testing.common.TestingLocation.loc;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.nativ.LoadingNativeImplException;
import org.smoothbuild.exec.nativ.Native;
import org.smoothbuild.exec.nativ.NativeImplLoader;
import org.smoothbuild.install.FullPathResolver;
import org.smoothbuild.lang.TestingLang;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.Function;
import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.ExpressionVisitorException;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.Scope;

import com.google.common.collect.ImmutableList;

public class ExpressionToTaskConverterTest extends TestingContext {
  private NativeImplLoader nativeImplLoader;
  private FullPathResolver fullPathResolver;
  private ExpressionToTaskConverter converter;

  @BeforeEach
  public void beforeEach() {
    nativeImplLoader = mock(NativeImplLoader.class);
    fullPathResolver = mock(FullPathResolver.class);
    converter = new ExpressionToTaskConverter(
        Definitions.empty(), objectFactory(), nativeImplLoader, fullPathResolver);
  }

  @Test
  public void task_for_unused_arguments_are_not_created() throws ExpressionVisitorException {
    Function nativeFunction = function(BLOB, "nativeFunction");
    CallExpression nativeCall = call(BLOB, nativeFunction);
    Function function = function(BLOB, "myFunction", TestingLang.blob(33), parameter(BLOB, "p"));
    CallExpression call = new CallExpression(BLOB, function, ImmutableList.of(nativeCall), loc());

    converter.visit(new Scope<>(Map.of()), call);

    verifyNoInteractions(nativeImplLoader);
  }

  @Test
  public void only_one_task_is_created_for_argument_assigned_to_parameter_that_is_used_twice()
      throws ExpressionVisitorException, LoadingNativeImplException {
    Function nativeFunction = function(BLOB, "nativeFunction");
    Function twoBlobsEater = function(
        BLOB, "twoBlobsEater", parameter(BLOB, "param"), parameter(BLOB, "param2"));

    CallExpression twoBlobsEaterCall = call(
        BLOB, twoBlobsEater, parameterRef(BLOB, "param"), parameterRef(BLOB, "param"));
    Function myFunction = function(BLOB, "myFunction", twoBlobsEaterCall, parameter(BLOB, "param"));
    CallExpression nativeCall = call(BLOB, nativeFunction);
    CallExpression myFunctionCall = call(BLOB, myFunction, nativeCall);

    when(nativeImplLoader.loadNative(nativeFunction, anyString(), Mockito.any(Hash.class)))
        .thenReturn(new Native(null, null));
    when(nativeImplLoader.loadNative(twoBlobsEater, anyString(), Mockito.any(Hash.class)))
        .thenReturn(new Native(null, null));

    converter.visit(new Scope<>(Map.of()), myFunctionCall);

    verify(nativeImplLoader, times(1))
        .loadNative(twoBlobsEater, anyString(), Mockito.any(Hash.class));
    verify(nativeImplLoader, times(1))
        .loadNative(nativeFunction, anyString(), Mockito.any(Hash.class));
  }
}
