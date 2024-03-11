package org.smoothbuild.app.run.eval;

import static com.google.common.truth.Truth.assertThat;
import static com.google.inject.Guice.createInjector;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.backendcompile.testing.TestingBsMapping.bsMapping;
import static org.smoothbuild.common.collect.Either.right;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.common.filesystem.base.FullPath.fullPath;
import static org.smoothbuild.common.filesystem.base.Path.path;
import static org.smoothbuild.common.log.Level.INFO;
import static org.smoothbuild.common.step.Step.maybeStep;
import static org.smoothbuild.common.step.Step.tryStep;
import static org.smoothbuild.common.tuple.Tuples.tuple;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.PROJECT_SPACE;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.annotatedFuncS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.arrayTS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.bindings;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.blobS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.bytecodeFuncS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.callS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.combineS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.constructorS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.funcS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.instantiateS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.intIdFuncS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.intS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.intTS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.itemS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.lambdaS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.nativeAnnotationS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.orderS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.paramRefS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.selectS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.sigS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.stringS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.structTS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.valueS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.varA;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import java.io.PrintWriter;
import java.math.BigInteger;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.app.report.PrintWriterReporter;
import org.smoothbuild.app.run.eval.report.BsTranslator;
import org.smoothbuild.app.run.eval.report.TaskReporterImpl;
import org.smoothbuild.common.bindings.ImmutableBindings;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.Reporter;
import org.smoothbuild.common.step.StepExecutor;
import org.smoothbuild.compilerbackend.BackendCompile;
import org.smoothbuild.compilerfrontend.lang.define.ExprS;
import org.smoothbuild.compilerfrontend.lang.define.NamedEvaluableS;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.IntB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.load.BytecodeLoader;
import org.smoothbuild.virtualmachine.bytecode.load.FilePersister;
import org.smoothbuild.virtualmachine.bytecode.load.NativeMethodLoader;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;
import org.smoothbuild.virtualmachine.evaluate.EvaluatorB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;
import org.smoothbuild.virtualmachine.testing.func.bytecode.ReturnIdFunc;

public class EvaluatorSTest extends TestingVirtualMachine {
  private final FilePersister filePersister = mock(FilePersister.class);
  private final NativeMethodLoader nativeMethodLoader = mock(NativeMethodLoader.class);
  private final BytecodeLoader bytecodeLoader = mock(BytecodeLoader.class);

  @Nested
  class _evaluation {
    @Nested
    class _constant {
      @Test
      public void blob() throws BytecodeException {
        assertEvaluation(blobS(7), blobB(7));
      }

      @Test
      public void int_() throws BytecodeException {
        assertEvaluation(intS(8), intB(8));
      }

      @Test
      public void string() throws BytecodeException {
        assertEvaluation(stringS("abc"), stringB("abc"));
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
          assertEvaluation(callS, intB(7));
        }

        @Test
        public void call_lambda_returning_enclosing_func_param() throws BytecodeException {
          var lambdaS = instantiateS(lambdaS(nlist(), paramRefS(intTS(), "p")));
          var funcS = funcS("myFunc", nlist(itemS(intTS(), "p")), callS(lambdaS));
          var callS = callS(instantiateS(funcS), intS(7));
          assertEvaluation(bindings(funcS), callS, intB(7));
        }

        @Test
        public void call_expression_function() throws BytecodeException {
          var funcS = funcS("n", nlist(), intS(7));
          var callS = callS(instantiateS(funcS));
          assertEvaluation(bindings(funcS), callS, intB(7));
        }

        @Test
        public void call_poly_expression_function() throws BytecodeException {
          var a = varA();
          var orderS = orderS(a, paramRefS(a, "e"));
          var funcS = funcS(arrayTS(a), "n", nlist(itemS(a, "e")), orderS);
          var callS = callS(instantiateS(list(intTS()), funcS), intS(7));
          assertEvaluation(bindings(funcS), callS, arrayB(intTB(), intB(7)));
        }

        @Test
        public void call_constructor() throws BytecodeException {
          var constructorS = constructorS(structTS("MyStruct", nlist(sigS(intTS(), "field"))));
          var callS = callS(instantiateS(constructorS), intS(7));
          assertEvaluation(bindings(constructorS), callS, tupleB(intB(7)));
        }

        @Test
        public void call_native_argless_func() throws Exception {
          var funcS = annotatedFuncS(
              nativeAnnotationS(1, stringS("class binary name")), intTS(), "f", nlist());
          var callS = callS(instantiateS(funcS));
          var jarB = blobB(137);
          when(filePersister.persist(fullPath(PROJECT_SPACE, path("build.jar"))))
              .thenReturn(jarB);
          when(nativeMethodLoader.load(any()))
              .thenReturn(right(
                  EvaluatorSTest.class.getMethod("returnInt", NativeApi.class, TupleB.class)));
          assertEvaluation(bindings(funcS), callS, intB(173));
        }

        @Test
        public void call_native_func_with_param() throws Exception {
          var funcS = annotatedFuncS(
              nativeAnnotationS(1, stringS("class binary name")),
              intTS(),
              "f",
              nlist(itemS(intTS(), "p")));
          var callS = callS(instantiateS(funcS), intS(77));
          var jarB = blobB(137);
          when(filePersister.persist(fullPath(PROJECT_SPACE, path("build.jar"))))
              .thenReturn(jarB);
          when(nativeMethodLoader.load(any()))
              .thenReturn(right(
                  EvaluatorSTest.class.getMethod("returnIntParam", NativeApi.class, TupleB.class)));
          assertEvaluation(bindings(funcS), callS, intB(77));
        }
      }

      @Nested
      class _combine {
        @Test
        public void combine() throws BytecodeException {
          assertEvaluation(combineS(intS(7), stringS("abc")), tupleB(intB(7), stringB("abc")));
        }
      }

      @Nested
      class _order {
        @Test
        public void order() throws BytecodeException {
          assertEvaluation(orderS(intTS(), intS(7), intS(8)), arrayB(intTB(), intB(7), intB(8)));
        }
      }

      @Nested
      class _param_ref {
        @Test
        public void param_ref() throws BytecodeException {
          var funcS = funcS("n", nlist(itemS(intTS(), "p")), paramRefS(intTS(), "p"));
          var callS = callS(instantiateS(funcS), intS(7));
          assertEvaluation(bindings(funcS), callS, intB(7));
        }
      }

      @Nested
      class _select {
        @Test
        public void select() throws BytecodeException {
          var structTS = structTS("MyStruct", nlist(sigS(intTS(), "f")));
          var constructorS = constructorS(structTS);
          var callS = callS(instantiateS(constructorS), intS(7));
          assertEvaluation(bindings(constructorS), selectS(callS, "f"), intB(7));
        }
      }
    }

    @Nested
    class _instantiate {
      @Nested
      class _lambda {
        @Test
        public void mono_lambda() throws BytecodeException {
          assertEvaluation(instantiateS(lambdaS(intS(7))), lambdaB(intB(7)));
        }

        @Test
        public void poly_lambda() throws BytecodeException {
          var a = varA();
          var polyLambdaS = lambdaS(nlist(itemS(a, "a")), paramRefS(a, "a"));
          var monoLambdaS = instantiateS(list(intTS()), polyLambdaS);
          assertEvaluation(monoLambdaS, lambdaB(list(intTB()), referenceB(intTB(), 0)));
        }
      }

      @Nested
      class _named_func {
        @Test
        public void mono_expression_func() throws BytecodeException {
          assertEvaluation(intIdFuncS(), idFuncB());
        }

        @Test
        public void poly_expression_function() throws BytecodeException {
          var a = varA();
          var funcS = funcS("n", nlist(itemS(a, "e")), paramRefS(a, "e"));
          var instantiateS = instantiateS(list(intTS()), funcS);
          assertEvaluation(bindings(funcS), instantiateS, idFuncB());
        }

        @Test
        public void ann_func() throws Exception {
          var jar = blobB(123);
          var className = ReturnIdFunc.class.getCanonicalName();
          when(filePersister.persist(fullPath(PROJECT_SPACE, path("build.jar"))))
              .thenReturn(jar);
          var varMap = ImmutableMap.<String, TypeB>of("A", intTB());
          var funcB = ReturnIdFunc.bytecode(bytecodeF(), varMap);
          when(bytecodeLoader.load("myFunc", jar, className, varMap)).thenReturn(right(funcB));

          var a = varA();
          var bytecodeFuncS = bytecodeFuncS(className, a, "myFunc", nlist(itemS(a, "p")));
          assertEvaluation(
              bindings(bytecodeFuncS), instantiateS(list(intTS()), bytecodeFuncS), funcB);
        }

        @Test
        public void constructor() throws BytecodeException {
          var constructorS = constructorS(structTS("MyStruct", nlist(sigS(intTS(), "myField"))));
          assertEvaluation(constructorS, lambdaB(list(intTB()), combineB(referenceB(intTB(), 0))));
        }
      }

      @Nested
      class _named_value {
        @Test
        public void mono_expression_value() throws BytecodeException {
          var valueS = valueS(1, intTS(), "name", intS(7));
          assertEvaluation(bindings(valueS), instantiateS(valueS), intB(7));
        }

        @Test
        public void poly_value() throws BytecodeException {
          var a = varA();
          var polyValue = valueS(1, arrayTS(a), "name", orderS(a));
          var instantiatedValue = instantiateS(list(intTS()), polyValue);
          assertEvaluation(bindings(polyValue), instantiatedValue, arrayB(intTB()));
        }
      }

      @Nested
      class _constructor {
        @Test
        public void constructor() throws BytecodeException {
          assertEvaluation(
              constructorS(structTS("MyStruct", nlist(sigS(intTS(), "field")))),
              lambdaB(funcTB(intTB(), tupleTB(intTB())), combineB(referenceB(intTB(), 0))));
        }
      }
    }
  }

  private void assertEvaluation(NamedEvaluableS namedEvaluableS, ExprB exprB) {
    assertThat(evaluate(bindings(namedEvaluableS), instantiateS(namedEvaluableS)))
        .isEqualTo(exprB);
  }

  private void assertEvaluation(ExprS exprS, ExprB exprB) {
    assertEvaluation(bindings(), exprS, exprB);
  }

  private void assertEvaluation(
      ImmutableBindings<NamedEvaluableS> evaluables, ExprS exprS, ExprB exprB) {
    assertThat(evaluate(evaluables, exprS)).isEqualTo(exprB);
  }

  private ExprB evaluate(ImmutableBindings<NamedEvaluableS> evaluables, ExprS exprS) {
    var resultMap = evaluate(evaluables, list(exprS)).get();
    assertThat(resultMap.size()).isEqualTo(1);
    return resultMap.get(0);
  }

  private Maybe<List<ValueB>> evaluate(
      ImmutableBindings<NamedEvaluableS> evaluables, List<ExprS> exprs) {
    var backendCompile = backendCompile(filePersister, bytecodeLoader);
    var evaluatorB = evaluatorB(nativeMethodLoader);
    var printWriter = new PrintWriter(inMemorySystemOut(), true);
    var reporter = new PrintWriterReporter(printWriter, INFO);
    var taskReporter = new TaskReporterImpl(reporter, new BsTranslator(bsMapping()));

    var injector = createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(EvaluatorB.class).toInstance(evaluatorB);
        bind(Reporter.class).toInstance(reporter);
        bind(TaskReporterImpl.class).toInstance(taskReporter);
      }
    });
    var step = tryStep(backendCompile).then(maybeStep(EvaluatorBFacade.class));
    var argument = tuple(exprs, evaluables);

    return new StepExecutor(injector).execute(step, argument, reporter);
  }

  public static IntB returnInt(NativeApi nativeApi, TupleB args) throws BytecodeException {
    return nativeApi.factory().int_(BigInteger.valueOf(173));
  }

  public static IntB returnIntParam(NativeApi nativeApi, TupleB args) throws BytecodeException {
    return (IntB) args.get(0);
  }

  public static ArrayB returnArrayParam(NativeApi nativeApi, TupleB args) throws BytecodeException {
    return (ArrayB) args.get(0);
  }

  private BackendCompile backendCompile(
      FilePersister filePersister, BytecodeLoader bytecodeLoader) {
    return new BackendCompile(bytecodeF(), filePersister, bytecodeLoader);
  }
}
