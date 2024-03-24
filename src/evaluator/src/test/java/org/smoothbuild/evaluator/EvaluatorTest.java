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
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.annotatedFuncS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.arrayTS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.bindings;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.blobS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.bytecodeFuncS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.callS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.combineS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.constructorS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.funcS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.instantiateS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.intIdFuncS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.intS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.intTS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.itemS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.lambdaS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.nativeAnnotationS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.orderS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.paramRefS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.selectS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sigS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.stringS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.structTS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.valueS;
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
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;
import org.smoothbuild.virtualmachine.bytecode.load.BytecodeLoader;
import org.smoothbuild.virtualmachine.bytecode.load.FilePersister;
import org.smoothbuild.virtualmachine.bytecode.load.NativeMethodLoader;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;
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
        assertEvaluation(blobS(7), bBlob(7));
      }

      @Test
      public void int_() throws BytecodeException {
        assertEvaluation(intS(8), bInt(8));
      }

      @Test
      public void string() throws BytecodeException {
        assertEvaluation(stringS("abc"), bString("abc"));
      }
    }

    @Nested
    class _expression {
      @Nested
      class _call {
        @Test
        public void call_lambda() throws BytecodeException {
          var LambdaS = lambdaS(nlist(), intS(7));
          var callS = callS(instantiateS(LambdaS));
          assertEvaluation(callS, bInt(7));
        }

        @Test
        public void call_lambda_returning_enclosing_func_param() throws BytecodeException {
          var lambdaS = instantiateS(lambdaS(nlist(), paramRefS(intTS(), "p")));
          var funcS = funcS("myFunc", nlist(itemS(intTS(), "p")), callS(lambdaS));
          var callS = callS(instantiateS(funcS), intS(7));
          assertEvaluation(bindings(funcS), callS, bInt(7));
        }

        @Test
        public void call_expression_function() throws BytecodeException {
          var funcS = funcS("n", nlist(), intS(7));
          var callS = callS(instantiateS(funcS));
          assertEvaluation(bindings(funcS), callS, bInt(7));
        }

        @Test
        public void call_poly_expression_function() throws BytecodeException {
          var a = varA();
          var orderS = orderS(a, paramRefS(a, "e"));
          var funcS = funcS(arrayTS(a), "n", nlist(itemS(a, "e")), orderS);
          var callS = callS(instantiateS(list(intTS()), funcS), intS(7));
          assertEvaluation(bindings(funcS), callS, bArray(bIntType(), bInt(7)));
        }

        @Test
        public void call_constructor() throws BytecodeException {
          var constructorS = constructorS(structTS("MyStruct", nlist(sigS(intTS(), "field"))));
          var callS = callS(instantiateS(constructorS), intS(7));
          assertEvaluation(bindings(constructorS), callS, bTuple(bInt(7)));
        }

        @Test
        public void call_native_argless_func() throws Exception {
          var funcS = annotatedFuncS(
              nativeAnnotationS(1, stringS("class binary name")), intTS(), "f", nlist());
          var callS = callS(instantiateS(funcS));
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
          var funcS = annotatedFuncS(
              nativeAnnotationS(1, stringS("class binary name")),
              intTS(),
              "f",
              nlist(itemS(intTS(), "p")));
          var callS = callS(instantiateS(funcS), intS(77));
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
          assertEvaluation(combineS(intS(7), stringS("abc")), bTuple(bInt(7), bString("abc")));
        }
      }

      @Nested
      class _order {
        @Test
        public void order() throws BytecodeException {
          assertEvaluation(orderS(intTS(), intS(7), intS(8)), bArray(bIntType(), bInt(7), bInt(8)));
        }
      }

      @Nested
      class _param_ref {
        @Test
        public void param_ref() throws BytecodeException {
          var funcS = funcS("n", nlist(itemS(intTS(), "p")), paramRefS(intTS(), "p"));
          var callS = callS(instantiateS(funcS), intS(7));
          assertEvaluation(bindings(funcS), callS, bInt(7));
        }
      }

      @Nested
      class _select {
        @Test
        public void select() throws BytecodeException {
          var structTS = structTS("MyStruct", nlist(sigS(intTS(), "f")));
          var constructorS = constructorS(structTS);
          var callS = callS(instantiateS(constructorS), intS(7));
          assertEvaluation(bindings(constructorS), selectS(callS, "f"), bInt(7));
        }
      }
    }

    @Nested
    class _instantiate {
      @Nested
      class _lambda {
        @Test
        public void mono_lambda() throws BytecodeException {
          assertEvaluation(instantiateS(lambdaS(intS(7))), bLambda(bInt(7)));
        }

        @Test
        public void poly_lambda() throws BytecodeException {
          var a = varA();
          var polyLambdaS = lambdaS(nlist(itemS(a, "a")), paramRefS(a, "a"));
          var monoLambdaS = instantiateS(list(intTS()), polyLambdaS);
          assertEvaluation(monoLambdaS, bLambda(list(bIntType()), bReference(bIntType(), 0)));
        }
      }

      @Nested
      class _named_func {
        @Test
        public void mono_expression_func() throws BytecodeException {
          assertEvaluation(intIdFuncS(), bIdFunc());
        }

        @Test
        public void poly_expression_function() throws BytecodeException {
          var a = varA();
          var funcS = funcS("n", nlist(itemS(a, "e")), paramRefS(a, "e"));
          var instantiateS = instantiateS(list(intTS()), funcS);
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
          var bytecodeFuncS = bytecodeFuncS(className, a, "myFunc", nlist(itemS(a, "p")));
          assertEvaluation(
              bindings(bytecodeFuncS), instantiateS(list(intTS()), bytecodeFuncS), bFunc);
        }

        @Test
        public void constructor() throws BytecodeException {
          var constructorS = constructorS(structTS("MyStruct", nlist(sigS(intTS(), "myField"))));
          assertEvaluation(
              constructorS, bLambda(list(bIntType()), bCombine(bReference(bIntType(), 0))));
        }
      }

      @Nested
      class _named_value {
        @Test
        public void mono_expression_value() throws BytecodeException {
          var valueS = valueS(1, intTS(), "name", intS(7));
          assertEvaluation(bindings(valueS), instantiateS(valueS), bInt(7));
        }

        @Test
        public void poly_value() throws BytecodeException {
          var a = varA();
          var polyValue = valueS(1, arrayTS(a), "name", orderS(a));
          var instantiatedValue = instantiateS(list(intTS()), polyValue);
          assertEvaluation(bindings(polyValue), instantiatedValue, bArray(bIntType()));
        }
      }

      @Nested
      class _constructor {
        @Test
        public void constructor() throws BytecodeException {
          assertEvaluation(
              constructorS(structTS("MyStruct", nlist(sigS(intTS(), "field")))),
              bLambda(
                  bFuncType(bIntType(), bTupleType(bIntType())),
                  bCombine(bReference(bIntType(), 0))));
        }
      }
    }
  }

  private void assertEvaluation(SNamedEvaluable sNamedEvaluable, BExpr bExpr) {
    assertThat(evaluate(bindings(sNamedEvaluable), instantiateS(sNamedEvaluable)))
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
