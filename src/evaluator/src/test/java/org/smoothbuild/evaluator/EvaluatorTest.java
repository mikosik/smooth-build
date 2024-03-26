package org.smoothbuild.evaluator;

import static com.google.common.truth.Truth.assertThat;
import static com.google.inject.Guice.createInjector;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.backendcompile.testing.TestingBsMapping.bsMapping;
import static org.smoothbuild.common.bucket.base.FullPath.fullPath;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.collect.Either.right;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.common.dag.Dag.apply2;
import static org.smoothbuild.common.dag.Dag.applyMaybeFunction;
import static org.smoothbuild.common.dag.Dag.value;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.PROJECT_BUCKET_ID;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.bindings;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.intIdSFunc;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sArrayType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sCombine;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sIntType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sSelect;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sSig;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sStructType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.varA;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import java.math.BigInteger;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.bindings.ImmutableBindings;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.dag.DagEvaluator;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.testing.MemoryReporter;
import org.smoothbuild.compilerbackend.BackendCompile;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SNamedEvaluable;
import org.smoothbuild.compilerfrontend.testing.TestingSExpression;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;
import org.smoothbuild.virtualmachine.bytecode.load.BytecodeLoader;
import org.smoothbuild.virtualmachine.bytecode.load.FilePersister;
import org.smoothbuild.virtualmachine.bytecode.load.NativeMethodLoader;
import org.smoothbuild.virtualmachine.evaluate.BEvaluator;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ReturnIdFunc;

public class EvaluatorTest extends TestingVirtualMachine {
  private final FilePersister filePersister = mock(FilePersister.class);
  private final NativeMethodLoader nativeMethodLoader = mock(NativeMethodLoader.class);
  private final BytecodeLoader bytecodeLoader = mock(BytecodeLoader.class);

  @Nested
  class _evaluation {
    @Nested
    class _constant {
      @Test
      public void blob() throws BytecodeException {
        assertEvaluation(TestingSExpression.sBlob(7), bBlob(7));
      }

      @Test
      public void int_() throws BytecodeException {
        assertEvaluation(TestingSExpression.sInt(8), bInt(8));
      }

      @Test
      public void string() throws BytecodeException {
        assertEvaluation(TestingSExpression.sString("abc"), bString("abc"));
      }
    }

    @Nested
    class _expression {
      @Nested
      class _call {
        @Test
        public void call_lambda() throws BytecodeException {
          var LambdaS = TestingSExpression.sLambda(nlist(), TestingSExpression.sInt(7));
          var callS = TestingSExpression.sCall(TestingSExpression.sInstantiate(LambdaS));
          assertEvaluation(callS, bInt(7));
        }

        @Test
        public void call_lambda_returning_enclosing_func_param() throws BytecodeException {
          var lambdaS = TestingSExpression.sInstantiate(
              TestingSExpression.sLambda(nlist(), TestingSExpression.sParamRef(sIntType(), "p")));
          var funcS = TestingSExpression.sFunc(
              "myFunc",
              nlist(TestingSExpression.sItem(sIntType(), "p")),
              TestingSExpression.sCall(lambdaS));
          var callS = TestingSExpression.sCall(
              TestingSExpression.sInstantiate(funcS), TestingSExpression.sInt(7));
          assertEvaluation(bindings(funcS), callS, bInt(7));
        }

        @Test
        public void call_expression_function() throws BytecodeException {
          var funcS = TestingSExpression.sFunc("n", nlist(), TestingSExpression.sInt(7));
          var callS = TestingSExpression.sCall(TestingSExpression.sInstantiate(funcS));
          assertEvaluation(bindings(funcS), callS, bInt(7));
        }

        @Test
        public void call_poly_expression_function() throws BytecodeException {
          var a = varA();
          var orderS = TestingSExpression.sOrder(a, TestingSExpression.sParamRef(a, "e"));
          var funcS = TestingSExpression.sFunc(
              sArrayType(a), "n", nlist(TestingSExpression.sItem(a, "e")), orderS);
          var callS = TestingSExpression.sCall(
              TestingSExpression.sInstantiate(list(sIntType()), funcS), TestingSExpression.sInt(7));
          assertEvaluation(bindings(funcS), callS, bArray(bIntType(), bInt(7)));
        }

        @Test
        public void call_constructor() throws BytecodeException {
          var constructorS = TestingSExpression.sConstructor(
              sStructType("MyStruct", nlist(sSig(sIntType(), "field"))));
          var callS = TestingSExpression.sCall(
              TestingSExpression.sInstantiate(constructorS), TestingSExpression.sInt(7));
          assertEvaluation(bindings(constructorS), callS, bTuple(bInt(7)));
        }

        @Test
        public void call_native_argless_func() throws Exception {
          var funcS = TestingSExpression.sAnnotatedFunc(
              TestingSExpression.sNativeAnnotation(
                  1, TestingSExpression.sString("class binary name")),
              sIntType(),
              "f",
              nlist());
          var callS = TestingSExpression.sCall(TestingSExpression.sInstantiate(funcS));
          var jarB = bBlob(137);
          when(filePersister.persist(fullPath(PROJECT_BUCKET_ID, path("build.jar"))))
              .thenReturn(jarB);
          when(nativeMethodLoader.load(any()))
              .thenReturn(
                  right(EvaluatorTest.class.getMethod("returnInt", NativeApi.class, BTuple.class)));
          assertEvaluation(bindings(funcS), callS, bInt(173));
        }

        @Test
        public void call_native_func_with_param() throws Exception {
          var funcS = TestingSExpression.sAnnotatedFunc(
              TestingSExpression.sNativeAnnotation(
                  1, TestingSExpression.sString("class binary name")),
              sIntType(),
              "f",
              nlist(TestingSExpression.sItem(sIntType(), "p")));
          var callS = TestingSExpression.sCall(
              TestingSExpression.sInstantiate(funcS), TestingSExpression.sInt(77));
          var jarB = bBlob(137);
          when(filePersister.persist(fullPath(PROJECT_BUCKET_ID, path("build.jar"))))
              .thenReturn(jarB);
          when(nativeMethodLoader.load(any()))
              .thenReturn(right(
                  EvaluatorTest.class.getMethod("returnIntParam", NativeApi.class, BTuple.class)));
          assertEvaluation(bindings(funcS), callS, bInt(77));
        }
      }

      @Nested
      class _combine {
        @Test
        public void combine() throws BytecodeException {
          assertEvaluation(
              sCombine(TestingSExpression.sInt(7), TestingSExpression.sString("abc")),
              bTuple(bInt(7), bString("abc")));
        }
      }

      @Nested
      class _order {
        @Test
        public void order() throws BytecodeException {
          assertEvaluation(
              TestingSExpression.sOrder(
                  sIntType(), TestingSExpression.sInt(7), TestingSExpression.sInt(8)),
              bArray(bIntType(), bInt(7), bInt(8)));
        }
      }

      @Nested
      class _param_ref {
        @Test
        public void param_ref() throws BytecodeException {
          var funcS = TestingSExpression.sFunc(
              "n",
              nlist(TestingSExpression.sItem(sIntType(), "p")),
              TestingSExpression.sParamRef(sIntType(), "p"));
          var callS = TestingSExpression.sCall(
              TestingSExpression.sInstantiate(funcS), TestingSExpression.sInt(7));
          assertEvaluation(bindings(funcS), callS, bInt(7));
        }
      }

      @Nested
      class _select {
        @Test
        public void select() throws BytecodeException {
          var structTS = sStructType("MyStruct", nlist(sSig(sIntType(), "f")));
          var constructorS = TestingSExpression.sConstructor(structTS);
          var callS = TestingSExpression.sCall(
              TestingSExpression.sInstantiate(constructorS), TestingSExpression.sInt(7));
          assertEvaluation(bindings(constructorS), sSelect(callS, "f"), bInt(7));
        }
      }
    }

    @Nested
    class _instantiate {
      @Nested
      class _lambda {
        @Test
        public void mono_lambda() throws BytecodeException {
          assertEvaluation(
              TestingSExpression.sInstantiate(
                  TestingSExpression.sLambda(TestingSExpression.sInt(7))),
              bLambda(bInt(7)));
        }

        @Test
        public void poly_lambda() throws BytecodeException {
          var a = varA();
          var polyLambdaS = TestingSExpression.sLambda(
              nlist(TestingSExpression.sItem(a, "a")), TestingSExpression.sParamRef(a, "a"));
          var monoLambdaS = TestingSExpression.sInstantiate(list(sIntType()), polyLambdaS);
          assertEvaluation(monoLambdaS, bLambda(list(bIntType()), bReference(bIntType(), 0)));
        }
      }

      @Nested
      class _named_func {
        @Test
        public void mono_expression_func() throws BytecodeException {
          assertEvaluation(intIdSFunc(), bIdFunc());
        }

        @Test
        public void poly_expression_function() throws BytecodeException {
          var a = varA();
          var funcS = TestingSExpression.sFunc(
              "n", nlist(TestingSExpression.sItem(a, "e")), TestingSExpression.sParamRef(a, "e"));
          var instantiateS = TestingSExpression.sInstantiate(list(sIntType()), funcS);
          assertEvaluation(bindings(funcS), instantiateS, bIdFunc());
        }

        @Test
        public void ann_func() throws Exception {
          var jar = bBlob(123);
          var className = ReturnIdFunc.class.getCanonicalName();
          when(filePersister.persist(fullPath(PROJECT_BUCKET_ID, path("build.jar"))))
              .thenReturn(jar);
          var varMap = ImmutableMap.<String, BType>of("A", bIntType());
          var bFunc = ReturnIdFunc.bytecode(bytecodeF(), varMap);
          when(bytecodeLoader.load("myFunc", jar, className, varMap)).thenReturn(right(bFunc));

          var a = varA();
          var bytecodeFuncS = TestingSExpression.sBytecodeFunc(
              className, a, "myFunc", nlist(TestingSExpression.sItem(a, "p")));
          assertEvaluation(
              bindings(bytecodeFuncS),
              TestingSExpression.sInstantiate(list(sIntType()), bytecodeFuncS),
              bFunc);
        }

        @Test
        public void constructor() throws BytecodeException {
          var constructorS = TestingSExpression.sConstructor(
              sStructType("MyStruct", nlist(sSig(sIntType(), "myField"))));
          assertEvaluation(
              constructorS, bLambda(list(bIntType()), bCombine(bReference(bIntType(), 0))));
        }
      }

      @Nested
      class _named_value {
        @Test
        public void mono_expression_value() throws BytecodeException {
          var valueS = TestingSExpression.sValue(1, sIntType(), "name", TestingSExpression.sInt(7));
          assertEvaluation(bindings(valueS), TestingSExpression.sInstantiate(valueS), bInt(7));
        }

        @Test
        public void poly_value() throws BytecodeException {
          var a = varA();
          var polyValue =
              TestingSExpression.sValue(1, sArrayType(a), "name", TestingSExpression.sOrder(a));
          var instantiatedValue = TestingSExpression.sInstantiate(list(sIntType()), polyValue);
          assertEvaluation(bindings(polyValue), instantiatedValue, bArray(bIntType()));
        }
      }

      @Nested
      class _constructor {
        @Test
        public void constructor() throws BytecodeException {
          assertEvaluation(
              TestingSExpression.sConstructor(
                  sStructType("MyStruct", nlist(sSig(sIntType(), "field")))),
              bLambda(
                  bFuncType(bIntType(), bTupleType(bIntType())),
                  bCombine(bReference(bIntType(), 0))));
        }
      }
    }
  }

  private void assertEvaluation(SNamedEvaluable sNamedEvaluable, BExpr bExpr) {
    assertThat(
            evaluate(bindings(sNamedEvaluable), TestingSExpression.sInstantiate(sNamedEvaluable)))
        .isEqualTo(bExpr);
  }

  private void assertEvaluation(SExpr sExpr, BExpr bExpr) {
    assertEvaluation(bindings(), sExpr, bExpr);
  }

  private void assertEvaluation(
      ImmutableBindings<SNamedEvaluable> evaluables, SExpr sExpr, BExpr bExpr) {
    assertThat(evaluate(evaluables, sExpr)).isEqualTo(bExpr);
  }

  private BExpr evaluate(ImmutableBindings<SNamedEvaluable> evaluables, SExpr sExpr) {
    var bValues = evaluate(evaluables, list(sExpr)).get().bValues();
    assertThat(bValues.size()).isEqualTo(1);
    return bValues.get(0);
  }

  private Maybe<EvaluatedExprs> evaluate(
      ImmutableBindings<SNamedEvaluable> evaluables, List<SExpr> exprs) {
    var backendCompile = backendCompile(filePersister, bytecodeLoader);
    var bEvaluator = bEvaluator(nativeMethodLoader);
    var reporter = new MemoryReporter();
    var taskReporter = new TaskReporterImpl(reporter, new BsTranslator(bsMapping()));

    var injector = createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(BEvaluator.class).toInstance(bEvaluator);
        bind(Reporter.class).toInstance(reporter);
        bind(TaskReporterImpl.class).toInstance(taskReporter);
      }
    });
    var compilationResult = apply2(backendCompile, value(exprs), value(evaluables));
    var evaluationDag = applyMaybeFunction(BEvaluatorFacade.class, compilationResult);
    return new DagEvaluator(injector).evaluate(evaluationDag, reporter);
  }

  public static BInt returnInt(NativeApi nativeApi, BTuple args) throws BytecodeException {
    return nativeApi.factory().int_(BigInteger.valueOf(173));
  }

  public static BInt returnIntParam(NativeApi nativeApi, BTuple args) throws BytecodeException {
    return (BInt) args.get(0);
  }

  public static BArray returnArrayParam(NativeApi nativeApi, BTuple args) throws BytecodeException {
    return (BArray) args.get(0);
  }

  private BackendCompile backendCompile(
      FilePersister filePersister, BytecodeLoader bytecodeLoader) {
    return new BackendCompile(bytecodeF(), filePersister, bytecodeLoader);
  }
}
