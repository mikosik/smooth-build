package org.smoothbuild.compile.sb;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
import org.smoothbuild.bytecode.expr.inst.BlobB;
import org.smoothbuild.bytecode.expr.inst.DefFuncB;
import org.smoothbuild.bytecode.expr.oper.CallB;
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
    class _inst {
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

      @Nested
      class _named_val {
        @Test
        public void def_val() {
          var valS = defValS("myValue", intS(7));
          assertTranslation(monoizeS(valS), callB(defFuncB(intB(7))));
        }

        @Test
        public void def_val_referencing_other_def_val() {
          var otherValue = defValS("otherValue", intS(7));
          var myValue = defValS("myValue", monoizeS(otherValue));
          assertTranslation(monoizeS(myValue), callB(defFuncB(callB(defFuncB(intB(7))))));
        }

        @Test
        public void native_val() {
          var filePath = filePath(PRJ, path("my/path"));
          var classBinaryName = "class.binary.name";
          var ann = natAnnS(loc(filePath, 1), stringS(classBinaryName));
          var natValS = annValS(ann, stringTS(), "myValue", loc(filePath, 2));

          var jar = blobB(37);
          var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), jar);
          var translator = sbTranslator(fileLoader);

          assertCall(() -> translator.translateExpr(monoizeS(natValS)))
              .throwsException(new SbTranslatorExc("Illegal value annotation: `@Native`."));
        }

        @Test
        public void bytecode_val() throws IOException {
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
      }

      @Nested
      class _func {
        @Test
        public void def_func() {
          var funcS = defFuncS("myFunc", nlist(), intS(7));
          assertTranslation(monoizeS(funcS), defFuncB(intB(7)));
        }

        @Test
        public void native_func() {
          var filePath = filePath(PRJ, path("my/path"));
          var classBinaryName = "class.binary.name";
          var ann = natAnnS(loc(filePath, 1), stringS(classBinaryName));
          var natFuncS = annFuncS(ann, intTS(), "myFunc", nlist(itemS(blobTS())));

          var funcTB = funcTB(intTB(), blobTB());
          var natFuncB = natFuncB(funcTB, blobB(37), stringB(classBinaryName), boolB(true));

          var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), blobB(37));
          assertTranslation(sbTranslator(fileLoader), monoizeS(natFuncS), natFuncB);
        }

        @Test
        public void bytecode_func() throws IOException {
          var clazz = ReturnReturnAbcFunc.class;
          var filePath = filePath(PRJ, path("my/path"));
          var classBinaryName = clazz.getCanonicalName();
          var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
          var byteFuncS = annFuncS(ann, stringTS(), "myFunc", nlist(), loc(filePath, 2));

          var fileLoader = createFileLoaderMock(
              filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          assertTranslation(sbTranslator(fileLoader), monoizeS(byteFuncS), returnAbcFuncB());
        }
      }
    }

    @Nested
    class _operator {
      @Test
      public void call() {
        var defFunc = defFuncS("myFunc", nlist(), stringS("abc"));
        var call = callS(monoizeS(defFunc));
        assertTranslation(call, callB(defFuncB(stringB("abc"))));
      }

      @Test
      public void order() {
        var order = orderS(intTS(), intS(3), intS(7));
        assertTranslation(order, orderB(intB(3), intB(7)));
      }

      @Test
      public void param_ref() {
        var func = defFuncS("f", nlist(itemS(intTS(), "p")), paramRefS(intTS(), "p"));
        assertTranslation(monoizeS(func), idFuncB());
      }

      @Test
      public void select() {
        var structTS = structTS("MyStruct", nlist(sigS(stringTS(), "field")));
        var syntCtorS = syntCtorS(structTS);
        var callS = callS(monoizeS(syntCtorS), stringS("abc"));
        var selectS = selectS(callS, "field");

        var ctorB = defFuncB(list(stringTB()), combineB(refB(stringTB(), 0)));
        var callB = callB(ctorB, stringB("abc"));
        assertTranslation(selectS, selectB(callB, intB(0)));
      }

      @Nested
      class _poly_ref {
        @Nested
        class _val {
          @Test
          public void defined() {
            var emptyArrayVal = emptyArrayValS();
            var polyRefS = monoizeS(aToIntVarMapS(), emptyArrayVal);
            var orderB = orderB(intTB());
            assertTranslation(polyRefS, callB(defFuncB(orderB)));
          }

          @Test
          public void defined_monoized_with_type_parameter_of_enclosing_val_type_param() {
            var a = varA();
            var b = varB();

            var emptyArrayValS = emptyArrayValS(a);
            var bEmptyArrayMonoValS = monoizeS(ImmutableMap.of(a, b), emptyArrayValS);

            var referencingValS = defValS("referencing", bEmptyArrayMonoValS);
            var referencingMonoValS = monoizeS(ImmutableMap.of(b, intTS()), referencingValS);

            var orderB = orderB(intTB());
            assertTranslation(referencingMonoValS, callB(defFuncB(callB(defFuncB(orderB)))));
          }

          @Test
          public void bytecode() throws IOException {
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
            var polyRefS = monoizeS(aToIntVarMapS(), byteValS);
            assertTranslation(translator, polyRefS, idFuncB());
          }
        }

        @Nested
        class _func {
          @Test
          public void defined() {
            var identity = idFuncS();
            var polyRefS = monoizeS(aToIntVarMapS(), identity);
            var funcB = defFuncB(funcTB(intTB(), intTB()), refB(intTB(), 0));
            assertTranslation(polyRefS, funcB);
          }

          @Test
          public void defined_monoized_with_type_parameter_of_enclosing_func_type_param() {
            var a = varA();
            var b = varB();

            var idFuncS = idFuncS();
            var bIdMonoFuncS = monoizeS(ImmutableMap.of(a, b), idFuncS);

            var bodyS = callS(bIdMonoFuncS, paramRefS(b, "p"));
            var wrapFuncS = defFuncS(b, "wrap", nlist(itemS(b, "p")), bodyS);
            var wrapMonoFuncS = monoizeS(ImmutableMap.of(b, intTS()), wrapFuncS);

            var idFuncB = defFuncB(funcTB(intTB(), intTB()), refB(intTB(), 0));
            var wrapFuncB = defFuncB(funcTB(intTB(), intTB()),
                callB(idFuncB, refB(intTB(), 0)));
            assertTranslation(wrapMonoFuncS, wrapFuncB);
          }

          @Test
          public void native_() {
            var a = varA();
            var filePath = filePath(PRJ, path("my/path"));
            var classBinaryName = "class.binary.name";
            var ann = natAnnS(loc(filePath, 1), stringS(classBinaryName));
            var natFuncS = annFuncS(ann, a, "myIdentity", nlist(itemS(a, "param")));

            var funcTB = funcTB(intTB(), intTB());
            var natFuncB = natFuncB(funcTB, blobB(37), stringB(classBinaryName), boolB(true));

            var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), blobB(37));
            var translator = sbTranslator(fileLoader);
            var polyRefS = monoizeS(ImmutableMap.of(a, intTS()), natFuncS);
            assertTranslation(translator, polyRefS, natFuncB);
          }

          @Test
          public void bytecode() throws IOException {
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
            var polyRefS = monoizeS(ImmutableMap.of(a, intTS()), byteFuncS);
            assertTranslation(translator, polyRefS, idFuncB());
          }
        }
      }
    }
  }

  @Nested
  class _bs_mapping {
    @Nested
    class _inst {
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

      @Nested
      class _named_val {
        @Test
        public void def_val() {
          var valS = defValS(3, "myValue", intS(7, 37));
          assertValNalMapping(monoizeS(9, valS), loc(9), "myValue", loc(3));
        }

        @Test
        public void def_val_referencing_other_def_val() {
          var otherValue = defValS(6, "otherValue", intS(7, 37));
          var valS = defValS(5, "myValue", monoizeS(otherValue));
          assertValNalMapping(monoizeS(9, valS), loc(9), "myValue", loc(5));
        }

        @Test
        public void bytecode_val() throws IOException {
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
      class _func {
        @Test
        public void def_func() {
          var funcS = defFuncS(7, "myFunc", nlist(), intS(37));
          assertNalMapping(monoizeS(funcS), "myFunc", loc(7));
        }

        @Test
        public void expr_inside_def_func_body() {
          var funcS = defFuncS(7, "myFunc", nlist(), intS(8, 37));
          var sbTranslator = newTranslator();
          var funcB = (DefFuncB) sbTranslator.translateExpr(monoizeS(funcS));
          var body = funcB.body();
          assertNalMapping(sbTranslator, body, null, loc(8));
        }

        @Test
        public void native_func() {
          var filePath = filePath();
          var classBinaryName = "class.binary.name";
          var ann = natAnnS(loc(filePath, 1), stringS(classBinaryName));
          var natFuncS = annFuncS(2, ann, intTS(), "myFunc", nlist(itemS(blobTS())));

          var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), blobB(37));
          var sbTranslator = sbTranslator(fileLoader);
          assertNalMapping(sbTranslator, monoizeS(natFuncS), "myFunc", loc(2));
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
    class _operator {
      @Test
      public void call() {
        var defFunc = defFuncS(7, "myFunc", nlist(), stringS("abc"));
        var call = callS(8, monoizeS(defFunc));
        assertNalMapping(call, null, loc(8));
      }

      @Test
      public void order() {
        var order = orderS(3, intTS(), intS(6), intS(7));
        assertNalMapping(order, null, loc(3));
      }

      @Test
      public void param_ref() {
        var func = defFuncS(4, "myFunc", nlist(itemS(intTS(), "p")), paramRefS(5, intTS(), "p"));
        var sbTranslator = newTranslator();
        var funcB = (DefFuncB) sbTranslator.translateExpr(monoizeS(func));
        var refB = funcB.body();
        assertNalMapping(sbTranslator, refB, null, loc(5));
      }

      @Test
      public void select() {
        var structTS = structTS("MyStruct", nlist(sigS(stringTS(), "field")));
        var syntCtorS = syntCtorS(structTS);
        var callS = callS(monoizeS(syntCtorS), stringS("abc"));
        var selectS = selectS(4, callS, "field");
        assertNalMapping(selectS, null, loc(4));
      }

      @Nested
      class _poly_ref {
        @Test
        public void def_val() {
          var emptyArrayVal = defValS(7, "emptyArray", orderS(varA()));
          var polyRefS = monoizeS(4, aToIntVarMapS(), emptyArrayVal);
          assertNalMapping(polyRefS, null, loc(4));
        }

        @Test
        public void def_func() {
          var identity = idFuncS();
          var polyRefS = monoizeS(aToIntVarMapS(), identity);
          assertNalMapping(polyRefS, "myId", loc(1));
        }
      }
    }
  }

  @Nested
  class _caching {
    @Test
    public void def_val_translation_result() {
      var myValue = defValS("myVal", stringS("abcdefghi"));
      assertTranslationIsCached(monoizeS(myValue));
    }

    @Test
    public void bytecode_val_translation_result() throws IOException {
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
    public void def_func_translation_result() {
      var myFunc = defFuncS("myFunc", nlist(), stringS("abcdefghi"));
      assertTranslationIsCached(monoizeS(myFunc));
    }

    @Test
    public void nat_func_translation_result() {
      var myFunc = natFuncS(stringTS(), "myFunc", nlist());
      assertTranslationIsCached(monoizeS(myFunc));
    }

    @Test
    public void bytecode_func_translation_result() throws IOException {
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
    public void synt_ctor_translation_result() {
      var myStruct = structTS("MyStruct", nlist(sigS(stringTS(), "name")));
      assertTranslationIsCached(
          monoizeS(syntCtorS(myStruct)));
    }

    @Test
    public void monoized_poly_func_translation_result() {
      var polyRefS = monoizeS(aToIntVarMapS(), idFuncS());
      assertTranslationIsCached(polyRefS);
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

  private void assertNalMapping(SbTranslator sbTranslator, ExprS exprS, String expectedName,
      Loc expectedLoc) {
    var exprB = sbTranslator.translateExpr(exprS);
    assertNalMapping(sbTranslator, exprB, expectedName, expectedLoc);
  }

  private static void assertNalMapping(SbTranslator sbTranslator, ExprB exprB,
      String expectedName, Loc expectedLoc) {
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
