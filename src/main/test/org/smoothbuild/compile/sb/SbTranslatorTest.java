package org.smoothbuild.compile.sb;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.compile.lang.type.VarSetS.varSetS;
import static org.smoothbuild.fs.base.PathS.path;
import static org.smoothbuild.fs.space.Space.PRJ;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nlist;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.expr.value.BlobB;
import org.smoothbuild.bytecode.expr.value.ExprFuncB;
import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.define.ExprS;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.load.FileLoader;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.func.bytecode.ReturnAbc;
import org.smoothbuild.testing.func.bytecode.ReturnIdFunc;
import org.smoothbuild.testing.func.bytecode.ReturnReturnAbcFunc;

import com.google.common.collect.ImmutableMap;

public class SbTranslatorTest extends TestContext {
  @Nested
  class _translate {
    @Nested
    class _named {
      @Nested
      class _named_value {
        @Test
        public void mono_expression_value() {
          var valS = valueS("myValue", intS(7));
          assertTranslation(monoizeS(valS), callB(exprFuncB(intB(7))));
        }

        @Test
        public void poly_expression_value() {
          var emptyArrayVal = emptyArrayValueS();
          var monoized = monoizeS(aToIntVarMapS(), emptyArrayVal);
          var orderB = orderB(intTB());
          assertTranslation(monoized, callB(exprFuncB(orderB)));
        }

        @Test
        public void mono_expression_value_referencing_other_expression_value() {
          var otherValue = valueS("otherValue", intS(7));
          var myValue = valueS("myValue", monoizeS(otherValue));
          assertTranslation(monoizeS(myValue), callB(exprFuncB(callB(exprFuncB(intB(7))))));
        }

        @Test
        public void poly_expression_value_monoized_with_type_param_of_enclosing_value_type_param() {
          var a = varA();
          var b = varB();

          var emptyArrayValS = emptyArrayValueS(a);
          var bEmptyArrayMonoValS = monoizeS(ImmutableMap.of(a, b), emptyArrayValS);

          var referencingValS = valueS("referencing", bEmptyArrayMonoValS);
          var referencingMonoValS = monoizeS(ImmutableMap.of(b, intTS()), referencingValS);

          var orderB = orderB(intTB());
          assertTranslation(referencingMonoValS, callB(exprFuncB(callB(exprFuncB(orderB)))));
        }

        @Test
        public void mono_native_value() {
          var filePath = filePath(PRJ, path("my/path"));
          var classBinaryName = "class.binary.name";
          var ann = nativeAnnotationS(loc(filePath, 1), stringS(classBinaryName));
          var natValS = annValS(ann, stringTS(), "myValue", loc(filePath, 2));

          var jar = blobB(37);
          var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), jar);
          var translator = sbTranslator(fileLoader);

          assertCall(() -> translator.translateExpr(monoizeS(natValS)))
              .throwsException(new SbTranslatorExc("Illegal value annotation: `@Native`."));
        }

        @Test
        public void mono_bytecode_value() throws IOException {
          var clazz = ReturnAbc.class;
          var filePath = filePath(PRJ, path("my/path"));
          var classBinaryName = clazz.getCanonicalName();
          var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
          var byteValS = annValS(ann, stringTS(), "myValue", loc(filePath, 2));

          var fileLoader = createFileLoaderMock(
              filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var translator = sbTranslator(fileLoader);
          assertTranslation(translator, monoizeS(byteValS), stringB("abc"));
        }

        @Test
        public void poly_bytecode_value() throws IOException {
          var clazz = ReturnIdFunc.class;
          var a = varA();
          var funcTS = funcTS(a, a);
          var filePath = smoothFilePath();
          var classBinaryName = clazz.getCanonicalName();
          var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
          var byteValS = annValS(2, ann, funcTS, "myFunc");

          var fileLoader = createFileLoaderMock(
              filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var translator = sbTranslator(fileLoader);
          var monoized = monoizeS(aToIntVarMapS(), byteValS);
          assertTranslation(translator, monoized, idFuncB());
        }
      }

      @Nested
      class _named_func {
        @Test
        public void mono_expression_function() {
          var funcS = funcS("myFunc", nlist(), intS(7));
          assertTranslation(monoizeS(funcS), exprFuncB(intB(7)));
        }

        @Test
        public void poly_expression_function() {
          var identity = idFuncS();
          var monoized = monoizeS(aToIntVarMapS(), identity);
          var funcB = exprFuncB(funcTB(intTB(), intTB()), refB(intTB(), 0));
          assertTranslation(monoized, funcB);
        }

        @Test
        public void poly_expression_func_monoized_with_type_param_of_enclosing_func_type_param() {
          var a = varA();
          var b = varB();

          var idFuncS = idFuncS();
          var monoIdFuncS = monoizeS(ImmutableMap.of(a, b), idFuncS);

          var bodyS = callS(monoIdFuncS, paramRefS(b, "p"));
          var wrapFuncS = funcS(b, "wrap", nlist(itemS(b, "p")), bodyS);
          var wrapMonoFuncS = monoizeS(ImmutableMap.of(b, intTS()), wrapFuncS);

          var idFuncB = exprFuncB(funcTB(intTB(), intTB()), refB(intTB(), 0));
          var wrapFuncB = exprFuncB(funcTB(intTB(), intTB()),
              callB(idFuncB, refB(intTB(), 0)));
          assertTranslation(wrapMonoFuncS, wrapFuncB);
        }

        @Test
        public void mono_native_function() {
          var filePath = filePath(PRJ, path("my/path"));
          var classBinaryName = "class.binary.name";
          var annotationS = nativeAnnotationS(loc(filePath, 1), stringS(classBinaryName));
          var nativeFuncS = annFuncS(annotationS, intTS(), "myFunc", nlist(itemS(blobTS())));

          var funcTB = funcTB(blobTB(), intTB());
          var nativeFuncB = nativeFuncB(funcTB, blobB(37), stringB(classBinaryName), boolB(true));

          var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), blobB(37));
          assertTranslation(sbTranslator(fileLoader), monoizeS(nativeFuncS), nativeFuncB);
        }

        @Test
        public void poly_native_function() {
          var a = varA();
          var filePath = filePath(PRJ, path("my/path"));
          var classBinaryName = "class.binary.name";
          var annotationS = nativeAnnotationS(loc(filePath, 1), stringS(classBinaryName));
          var nativeFuncS = annFuncS(annotationS, a, "myIdentity", nlist(itemS(a, "param")));

          var funcTB = funcTB(intTB(), intTB());
          var nativeFuncB = nativeFuncB(funcTB, blobB(37), stringB(classBinaryName), boolB(true));

          var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), blobB(37));
          var translator = sbTranslator(fileLoader);
          var monoized = monoizeS(ImmutableMap.of(a, intTS()), nativeFuncS);
          assertTranslation(translator, monoized, nativeFuncB);
        }

        @Test
        public void mono_bytecode_function() throws IOException {
          var clazz = ReturnReturnAbcFunc.class;
          var filePath = filePath(PRJ, path("my/path"));
          var classBinaryName = clazz.getCanonicalName();
          var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
          var byteFuncS = annFuncS(ann, stringTS(), "myFunc", nlist(), loc(filePath, 2));

          var fileLoader = createFileLoaderMock(
              filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          assertTranslation(sbTranslator(fileLoader), monoizeS(byteFuncS), returnAbcFuncB());
        }

        @Test
        public void poly_bytecode_function() throws IOException {
          var clazz = ReturnIdFunc.class;
          var a = varA();
          var funcTS = funcTS(a, a);
          var filePath = smoothFilePath();
          var classBinaryName = clazz.getCanonicalName();
          var ann = bytecodeS(classBinaryName, loc(filePath, 1));
          var byteFuncS = annFuncS(1, ann, funcTS.res(), "myFunc", nlist(itemS(a, "p")));

          var fileLoader = createFileLoaderMock(
              filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var translator = sbTranslator(fileLoader);
          var monoized = monoizeS(ImmutableMap.of(a, intTS()), byteFuncS);
          assertTranslation(translator, monoized, idFuncB());
        }

      }
    }

    @Nested
    class _expr {
      @Test
      public void blob() {
        var blobS = blobS(37);
        assertTranslation(blobS, blobB(37));
      }

      @Test
      public void int_() {
        var intS = intS(1);
        assertTranslation(intS, intB(1));
      }

      @Test
      public void string() {
        var stringS = stringS("abc");
        assertTranslation(stringS, stringB("abc"));
      }

      @Test
      public void anon_func() {
        var anonFuncS = anonFuncS(
            varSetS(varA()), nlist(itemS(varA(), "p")), paramRefS(varA(), "p"));
        var monoAnonFuncS = monoizeS(varMap(varA(), intTS()), anonFuncS);
        assertTranslation(monoAnonFuncS, closurizeB(list(intTB()), refB(intTB(), 0)));
      }

      @Test
      public void anon_func_referencing_param_of_enclosing_func() {
        var monoAnonFuncS = monoizeS(anonFuncS(paramRefS(intTS(), "p")));
        var monoFuncS = monoizeS(funcS("myFunc", nlist(itemS(intTS(), "p")), monoAnonFuncS));

        var bodyB = closurizeB(refB(intTB(), 0));
        var funcB = exprFuncB(funcTB(intTB(), funcTB(intTB())), bodyB);

        assertTranslation(monoFuncS, funcB);
      }

      @Test
      public void anon_func_with_param_referencing_param_of_enclosing_func() {
        // myFunc(Int p) = (Blob a) -> p;
        var monoAnonFuncS = monoizeS(anonFuncS(
            nlist(itemS(blobTS(), "a")), paramRefS(intTS(), "p")));
        var monoFuncS = monoizeS(funcS("myFunc", nlist(itemS(intTS(), "p")), monoAnonFuncS));

        var bodyB = closurizeB(list(blobTB()), refB(intTB(), 1));
        var funcB = exprFuncB(list(intTB()), bodyB);

        assertTranslation(monoFuncS, funcB);
      }

      @Test
      public void call() {
        var funcS = funcS("myFunc", nlist(), stringS("abc"));
        var call = callS(monoizeS(funcS));
        assertTranslation(call, callB(exprFuncB(stringB("abc"))));
      }

      @Test
      public void order() {
        var order = orderS(intTS(), intS(3), intS(7));
        assertTranslation(order, orderB(intB(3), intB(7)));
      }

      @Test
      public void param_ref() {
        var func = funcS("f", nlist(itemS(intTS(), "p")), paramRefS(intTS(), "p"));
        assertTranslation(monoizeS(func), idFuncB());
      }

      @Test
      public void param_ref_to_unknown_param_causes_exception() {
        var func = funcS("f", nlist(itemS(intTS(), "p")), paramRefS(intTS(), "p2"));
        var monoFunc = monoizeS(func);
        assertCall(() -> newTranslator().translateExpr(monoFunc))
            .throwsException(
                new SbTranslatorExc("Reference to unknown parameter `p2` at myBuild.smooth:1."));
      }

      @Test
      public void select() {
        var structTS = structTS("MyStruct", nlist(sigS(stringTS(), "field")));
        var constructorS = constructorS(structTS);
        var callS = callS(monoizeS(constructorS), stringS("abc"));
        var selectS = selectS(callS, "field");

        var ctorB = exprFuncB(list(stringTB()), combineB(refB(stringTB(), 0)));
        var callB = callB(ctorB, stringB("abc"));
        assertTranslation(selectS, selectB(callB, intB(0)));
      }

      @Test
      public void monoized_poly_expr_twice_with_outer_monoize_actually_setting_its_var() {
        // regression test
        var monoAnonFuncS = monoizeS(anonFuncS(varSetS(), paramRefS(varA(), "a")));
        var monoFuncS = monoizeS(
            varMap(varA(), intTS()),
            funcS("myFunc", nlist(itemS(varA(), "a")), monoAnonFuncS));

        var bodyB = closurizeB(refB(intTB(), 0));
        var funcB = exprFuncB(funcTB(intTB(), funcTB(intTB())), bodyB);

        assertTranslation(monoFuncS, funcB);
      }
    }
  }

  @Nested
  class _bs_mapping {
    @Nested
    class _named {
      @Nested
      class _named_value {
        @Test
        public void expression_value() {
          var valS = valueS(3, "myValue", intS(7, 37));
          assertValNalMapping(monoizeS(9, valS), loc(9), "myValue", loc(3));
        }

        @Test
        public void expression_value_referencing_other_expression_value() {
          var otherValue = valueS(6, "otherValue", intS(7, 37));
          var valS = valueS(5, "myValue", monoizeS(otherValue));
          assertValNalMapping(monoizeS(9, valS), loc(9), "myValue", loc(5));
        }

        @Test
        public void bytecode_value() throws IOException {
          var clazz = ReturnAbc.class;
          var filePath = filePath();
          var classBinaryName = clazz.getCanonicalName();
          var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 7));
          var byteValS = annValS(ann, stringTS(), "myValue", loc(filePath, 8));

          var fileLoader = createFileLoaderMock(
              filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var sbTranslator = sbTranslator(fileLoader);
          var exprB = sbTranslator.translateExpr(monoizeS(byteValS));
          assertNalMapping(sbTranslator, exprB, "myValue", loc(8));
        }
      }

      @Nested
      class _named_func {
        @Test
        public void expression_function() {
          var funcS = funcS(7, "myFunc", nlist(), intS(37));
          assertNalMapping(monoizeS(funcS), "myFunc", loc(7));
        }

        @Test
        public void expression_inside_expression_function_body() {
          var funcS = funcS(7, "myFunc", nlist(), intS(8, 37));
          var sbTranslator = newTranslator();
          var funcB = (ExprFuncB) sbTranslator.translateExpr(monoizeS(funcS));
          var body = funcB.body();
          assertNalMapping(sbTranslator, body, null, loc(8));
        }

        @Test
        public void native_func() {
          var filePath = filePath();
          var classBinaryName = "class.binary.name";
          var annotationS = nativeAnnotationS(loc(filePath, 1), stringS(classBinaryName));
          var nativeFuncS = annFuncS(2, annotationS, intTS(), "myFunc", nlist(itemS(blobTS())));

          var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), blobB(37));
          var sbTranslator = sbTranslator(fileLoader);
          assertNalMapping(sbTranslator, monoizeS(nativeFuncS), "myFunc", loc(2));
        }

        @Test
        public void bytecode_func() throws IOException {
          var clazz = ReturnReturnAbcFunc.class;
          var filePath = filePath();
          var classBinaryName = clazz.getCanonicalName();
          var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
          var byteFuncS = annFuncS(ann, stringTS(), "myFunc", nlist(), loc(filePath, 2));

          var fileLoader = createFileLoaderMock(
              filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var sbTranslator = sbTranslator(fileLoader);
          assertNalMapping(sbTranslator, monoizeS(byteFuncS), "myFunc", loc(2));
        }
      }
    }

    @Nested
    class _expr {
      @Test
      public void blob() {
        var blobS = blobS(7, 0x37);
        assertNalMapping(blobS, null, loc(7));
      }

      @Test
      public void int_() {
        var intS = intS(7, 37);
        assertNalMapping(intS, null, loc(7));
      }

      @Test
      public void string() {
        var stringS = stringS(7, "abc");
        assertNalMapping(stringS, null, loc(7));
      }

      @Test
      public void anonFunc() {
        var monoAnonFuncS = monoizeS(anonFuncS(7, nlist(), stringS("abc")));
        assertNalMapping(monoAnonFuncS, "<anonymous>", loc(7));
      }

      @Test
      public void call() {
        var funcS = funcS(7, "myFunc", nlist(), stringS("abc"));
        var call = callS(8, monoizeS(funcS));
        assertNalMapping(call, null, loc(8));
      }

      @Test
      public void order() {
        var order = orderS(3, intTS(), intS(6), intS(7));
        assertNalMapping(order, null, loc(3));
      }

      @Test
      public void param_ref() {
        var func = funcS(4, "myFunc", nlist(itemS(intTS(), "p")), paramRefS(5, intTS(), "p"));
        var sbTranslator = newTranslator();
        var funcB = (ExprFuncB) sbTranslator.translateExpr(monoizeS(func));
        var refB = funcB.body();
        assertNalMapping(sbTranslator, refB, null, loc(5));
      }

      @Test
      public void select() {
        var structTS = structTS("MyStruct", nlist(sigS(stringTS(), "field")));
        var constructorS = constructorS(structTS);
        var callS = callS(monoizeS(constructorS), stringS("abc"));
        var selectS = selectS(4, callS, "field");
        assertNalMapping(selectS, null, loc(4));
      }

      @Nested
      class _monoize {
        @Test
        public void expression_value() {
          var emptyArrayVal = valueS(7, "emptyArray", orderS(varA()));
          var monoized = monoizeS(4, aToIntVarMapS(), emptyArrayVal);
          assertNalMapping(monoized, null, loc(4));
        }

        @Test
        public void expression_function() {
          var identity = idFuncS();
          var monoized = monoizeS(aToIntVarMapS(), identity);
          assertNalMapping(monoized, "myId", loc(1));
        }
      }
    }
  }

  @Nested
  class _caching {
    @Test
    public void expression_value_translation_result() {
      var myValue = valueS("myVal", stringS("abcdefghi"));
      assertTranslationIsCached(monoizeS(myValue));
    }

    @Test
    public void bytecode_value_translation_result() throws IOException {
      var clazz = ReturnAbc.class;
      var filePath = filePath(PRJ, path("my/path"));
      var classBinaryName = clazz.getCanonicalName();
      var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
      var bytecodeValS = annValS(ann, stringTS(), "myFunc", loc(filePath, 2));
      var fileLoader = createFileLoaderMock(
          filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));

      assertTranslationIsCached(sbTranslator(fileLoader), monoizeS(bytecodeValS));
    }

    @Test
    public void expression_function_translation_result() {
      var myFunc = funcS("myFunc", nlist(), stringS("abcdefghi"));
      assertTranslationIsCached(monoizeS(myFunc));
    }

    @Test
    public void native_function_translation_result() {
      var myFunc = nativeFuncS(stringTS(), "myFunc", nlist());
      assertTranslationIsCached(monoizeS(myFunc));
    }

    @Test
    public void bytecode_function_translation_result() throws IOException {
      var clazz = ReturnReturnAbcFunc.class;
      var filePath = filePath(PRJ, path("my/path"));
      var classBinaryName = clazz.getCanonicalName();
      var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
      var bytecodeFuncS = annFuncS(ann, stringTS(), "myFunc", nlist(), loc(filePath, 2));
      var fileLoader = createFileLoaderMock(
          filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));

      assertTranslationIsCached(sbTranslator(fileLoader), monoizeS(bytecodeFuncS));
    }

    @Test
    public void constructor_translation_result() {
      var myStruct = structTS("MyStruct", nlist(sigS(stringTS(), "name")));
      assertTranslationIsCached(
          monoizeS(constructorS(myStruct)));
    }

    @Test
    public void monoized_poly_function_translation_result() {
      var monoized = monoizeS(aToIntVarMapS(), idFuncS());
      assertTranslationIsCached(monoized);
    }

    private void assertTranslationIsCached(ExprS exprS) {
      assertTranslationIsCached(newTranslator(), exprS);
    }

    private void assertTranslationIsCached(SbTranslator sbTranslator, ExprS exprS) {
      assertThat(sbTranslator.translateExpr(exprS))
          .isSameInstanceAs(sbTranslator.translateExpr(exprS));
    }
  }

  private void assertTranslation(ExprS exprS, ExprB expected) {
    assertTranslation(newTranslator(), exprS, expected);
  }

  private void assertTranslation(SbTranslator sbTranslator, ExprS exprS, ExprB expected) {
    assertThat(sbTranslator.translateExpr(exprS))
        .isEqualTo(expected);
  }

  private void assertValNalMapping(ExprS exprS, Loc expectedCallLoc, String expectedName,
      Loc expectedLoc) {
    var sbTranslator = newTranslator();
    var call = ((CallB) sbTranslator.translateExpr(exprS));
    assertNalMapping(sbTranslator, call, null, expectedCallLoc);
    var called = call.dataSeq().get(0);
    assertNalMapping(sbTranslator, called, expectedName, expectedLoc);
  }

  private void assertNalMapping(ExprS exprS, String expectedName, Loc expectedLoc) {
    assertNalMapping(newTranslator(), exprS, expectedName, expectedLoc);
  }

  private void assertNalMapping(
      SbTranslator sbTranslator, ExprS exprS, String expectedName, Loc expectedLoc) {
    var exprB = sbTranslator.translateExpr(exprS);
    assertNalMapping(sbTranslator, exprB, expectedName, expectedLoc);
  }

  private static void assertNalMapping(
      SbTranslator sbTranslator, ExprB exprB, String expectedName, Loc expectedLoc) {
    var bsMapping = sbTranslator.bsMapping();
    assertThat(bsMapping.nameMapping().get(exprB.hash()))
        .isEqualTo(expectedName);
    assertThat(bsMapping.locMapping().get(exprB.hash()))
        .isEqualTo(expectedLoc);
  }

  private SbTranslator newTranslator() {
    try {
      FileLoader mock = mock(FileLoader.class);
      when(mock.load(any())).thenReturn(blobB(1));
      return sbTranslator(mock);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private FileLoader createFileLoaderMock(FilePath filePath, BlobB value) {
    try {
      FileLoader mock = mock(FileLoader.class);
      when(mock.load(filePath)).thenReturn(value);
      return mock;
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
