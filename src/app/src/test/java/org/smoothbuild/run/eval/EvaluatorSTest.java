package org.smoothbuild.run.eval;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nlist;
import static org.smoothbuild.util.fs.base.PathS.path;
import static org.smoothbuild.util.fs.space.Space.PRJ;

import java.math.BigInteger;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compile.fs.lang.define.ExprS;
import org.smoothbuild.compile.fs.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.sb.BytecodeLoader;
import org.smoothbuild.load.FileLoader;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.func.bytecode.ReturnIdFunc;
import org.smoothbuild.util.bindings.ImmutableBindings;
import org.smoothbuild.util.collect.Try;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.IntB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;
import org.smoothbuild.vm.evaluate.task.NativeMethodLoader;

import com.google.common.collect.ImmutableMap;

public class EvaluatorSTest extends TestContext {
  private final FileLoader fileLoader = mock(FileLoader.class);
  private final NativeMethodLoader nativeMethodLoader = mock(NativeMethodLoader.class);
  private final BytecodeLoader bytecodeLoader = mock(BytecodeLoader.class);

  @Nested
  class _evaluation {
    @Nested
    class _constant {
      @Test
      public void blob() throws EvaluatorExcS {
        assertEvaluation(blobS(7), blobB(7));
      }

      @Test
      public void int_() throws EvaluatorExcS {
        assertEvaluation(intS(8), intB(8));
      }

      @Test
      public void string() throws EvaluatorExcS {
        assertEvaluation(stringS("abc"), stringB("abc"));
      }
    }

    @Nested
    class _expression {
      @Nested
      class _call {
        @Test
        public void call_lambda() throws EvaluatorExcS {
          var LambdaS = lambdaS(nlist(), intS(7));
          var callS = callS(instantiateS(LambdaS));
          assertEvaluation(callS, intB(7));
        }

        @Test
        public void call_lambda_returning_enclosing_func_param()
            throws EvaluatorExcS {
          var lambdaS = instantiateS(lambdaS(nlist(), paramRefS(intTS(), "p")));
          var funcS = funcS("myFunc", nlist(itemS(intTS(), "p")), callS(lambdaS));
          var callS = callS(instantiateS(funcS), intS(7));
          assertEvaluation(bindings(funcS), callS, intB(7));
        }

        @Test
        public void call_expression_function() throws EvaluatorExcS {
          var funcS = funcS("n", nlist(), intS(7));
          var callS = callS(instantiateS(funcS));
          assertEvaluation(bindings(funcS), callS, intB(7));
        }

        @Test
        public void call_poly_expression_function() throws EvaluatorExcS {
          var a = varA();
          var orderS = orderS(a, paramRefS(a, "e"));
          var funcS = funcS(arrayTS(a), "n", nlist(itemS(a, "e")), orderS);
          var callS = callS(instantiateS(list(intTS()), funcS), intS(7));
          assertEvaluation(bindings(funcS), callS, arrayB(intTB(), intB(7)));
        }

        @Test
        public void call_constructor() throws EvaluatorExcS {
          var constructorS = constructorS(structTS("MyStruct", nlist(sigS(intTS(), "field"))));
          var callS = callS(instantiateS(constructorS), intS(7));
          assertEvaluation(bindings(constructorS), callS,tupleB(intB(7)));
        }

        @Test
        public void call_native_argless_func() throws Exception {
          var funcS = annotatedFuncS(
              nativeAnnotationS(1, stringS("class binary name")), intTS(), "f", nlist());
          var callS = callS(instantiateS(funcS));
          var jarB = blobB(137);
          when(fileLoader.load(filePath(PRJ, path("myBuild.jar"))))
              .thenReturn(jarB);
          when(nativeMethodLoader.load(any()))
              .thenReturn(Try.result(
                  EvaluatorSTest.class.getMethod("returnInt", NativeApi.class, TupleB.class)));
          assertEvaluation(bindings(funcS), callS, intB(173));
        }

        @Test
        public void call_native_func_with_param() throws Exception {
          var funcS = annotatedFuncS(nativeAnnotationS(1, stringS("class binary name")),
              intTS(), "f", nlist(itemS(intTS(), "p"))
          );
          var callS = callS(instantiateS(funcS), intS(77));
          var jarB = blobB(137);
          when(fileLoader.load(filePath(PRJ, path("myBuild.jar"))))
              .thenReturn(jarB);
          when(nativeMethodLoader.load(any()))
              .thenReturn(Try.result(
                  EvaluatorSTest.class.getMethod("returnIntParam", NativeApi.class, TupleB.class)));
          assertEvaluation(bindings(funcS), callS, intB(77));
        }
      }

      @Nested
      class _combine {
        @Test
        public void combine() throws EvaluatorExcS {
          assertEvaluation(
              combineS(intS(7), stringS("abc")),
              tupleB(intB(7), stringB("abc")));
        }
      }

      @Nested
      class _order {
        @Test
        public void order() throws EvaluatorExcS {
          assertEvaluation(
              orderS(intTS(), intS(7), intS(8)),
              arrayB(intTB(), intB(7), intB(8)));
        }
      }

      @Nested
      class _param_ref {
        @Test
        public void param_ref() throws EvaluatorExcS {
          var funcS = funcS("n", nlist(itemS(intTS(), "p")), paramRefS(intTS(), "p"));
          var callS = callS(instantiateS(funcS), intS(7));
          assertEvaluation(bindings(funcS), callS, intB(7));
        }
      }

      @Nested
      class _select {
        @Test
        public void select() throws EvaluatorExcS {
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
        public void mono_lambda() throws EvaluatorExcS {
          assertEvaluation(
              instantiateS(lambdaS(intS(7))),
              lambdaB(intB(7)));
        }

        @Test
        public void poly_lambda() throws EvaluatorExcS {
          var a = varA();
          var polyLambdaS = lambdaS(nlist(itemS(a, "a")), paramRefS(a, "a"));
          var monoLambdaS = instantiateS(list(intTS()), polyLambdaS);
          assertEvaluation(monoLambdaS, lambdaB(list(intTB()), varB(intTB(), 0)));
        }
      }

      @Nested
      class _named_func {
        @Test
        public void mono_expression_func() throws EvaluatorExcS {
          assertEvaluation(intIdFuncS(), idFuncB());
        }

        @Test
        public void poly_expression_function() throws EvaluatorExcS {
          var a = varA();
          var funcS = funcS("n", nlist(itemS(a, "e")), paramRefS(a, "e"));
          var instantiateS = instantiateS(list(intTS()), funcS);
          assertEvaluation(bindings(funcS), instantiateS, idFuncB());
        }

        @Test
        public void ann_func() throws Exception {
          var jar = blobB(123);
          var className = ReturnIdFunc.class.getCanonicalName();
          when(fileLoader.load(filePath(PRJ, path("myBuild.jar"))))
              .thenReturn(jar);
          var varMap = ImmutableMap.<String, TypeB>of("A", intTB());
          var funcB = ReturnIdFunc.bytecode(bytecodeF(), varMap);
          when(bytecodeLoader.load("myFunc", jar, className, varMap))
              .thenReturn(Try.result(funcB));

          var a = varA();
          var bytecodeFuncS = bytecodeFuncS(className, a, "myFunc", nlist(itemS(a, "p")));
          assertEvaluation(
              bindings(bytecodeFuncS), instantiateS(list(intTS()), bytecodeFuncS), funcB);
        }

        @Test
        public void constructor() throws EvaluatorExcS {
          var constructorS = constructorS(structTS("MyStruct", nlist(sigS(intTS(), "myField"))));
          assertEvaluation(constructorS, lambdaB(list(intTB()), combineB(varB(intTB(), 0))));
        }
      }

      @Nested
      class _named_value {
        @Test
        public void mono_expression_value() throws EvaluatorExcS {
          var valueS = valueS(1, intTS(), "name", intS(7));
          assertEvaluation(bindings(valueS), instantiateS(valueS), intB(7));
        }

        @Test
        public void poly_value() throws EvaluatorExcS {
          var a = varA();
          var polyValue = valueS(1, arrayTS(a), "name", orderS(a));
          var instantiatedValue = instantiateS(list(intTS()), polyValue);
          assertEvaluation(bindings(polyValue), instantiatedValue, arrayB(intTB()));
        }
      }

      @Nested
      class _constructor {
        @Test
        public void constructor() throws EvaluatorExcS {
          assertEvaluation(
              constructorS(structTS("MyStruct", nlist(sigS(intTS(), "field")))),
              lambdaB(funcTB(intTB(), tupleTB(intTB())), combineB(varB(intTB(), 0))));
        }
      }
    }
  }

  private void assertEvaluation(NamedEvaluableS namedEvaluableS, ExprB exprB) throws EvaluatorExcS {
    assertThat(evaluate(bindings(namedEvaluableS), instantiateS(namedEvaluableS)))
        .isEqualTo(exprB);
  }

  private void assertEvaluation(ExprS exprS, ExprB exprB) throws EvaluatorExcS {
    assertEvaluation(bindings(), exprS, exprB);
  }

  private void assertEvaluation(
      ImmutableBindings<NamedEvaluableS> evaluables, ExprS exprS, ExprB exprB)
      throws EvaluatorExcS {
    assertThat(evaluate(evaluables, exprS))
        .isEqualTo(exprB);
  }

  private ExprB evaluate(ImmutableBindings<NamedEvaluableS> evaluables, ExprS exprS)
      throws EvaluatorExcS {
    var resultMap = newEvaluator().evaluate(evaluables, list(exprS)).get();
    assertThat(resultMap.size())
        .isEqualTo(1);
    return resultMap.get(0);
  }

  private EvaluatorS newEvaluator() {
    var sbTranslatorFacade = sbTranslatorFacade(fileLoader, bytecodeLoader);
    var evaluatorB = evaluatorB(nativeMethodLoader);
    return new EvaluatorS(sbTranslatorFacade, (bsMapping) -> evaluatorB, reporter());
  }

  public static IntB returnInt(NativeApi nativeApi, TupleB args) {
    return nativeApi.factory().int_(BigInteger.valueOf(173));
  }

  public static IntB returnIntParam(NativeApi nativeApi, TupleB args) {
    return (IntB) args.get(0);
  }

  public static ArrayB returnArrayParam(NativeApi nativeApi, TupleB args) {
    return (ArrayB) args.get(0);
  }
}
