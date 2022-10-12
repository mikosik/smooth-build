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
import org.smoothbuild.compile.lang.base.LabeledLoc;
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
        assertConversion(blobS, blobB(37));
      }

      @Test
      public void int_() {
        var intS = intS(1);
        assertConversion(intS, intB(1));
      }

      @Test
      public void string() {
        var stringS = stringS("abc");
        assertConversion(stringS, stringB("abc"));
      }

      @Nested
      class _named_val {
        @Test
        public void def_val() {
          var valS = defValS("myValue", intS(7));
          assertConversion(valS, intB(7));
        }

        @Test
        public void def_val_referencing_other_def_val() {
          var valS = defValS("myValue", defValS("otherValue", intS(7)));
          assertConversion(valS, intB(7));
        }

        @Test
        public void native_val() {
          var filePath = filePath(PRJ, path("my/path"));
          var classBinaryName = "class.binary.name";
          var ann = natAnnS(loc(filePath, 1), stringS(classBinaryName));
          var natValS = annValS(ann, stringTS(), modPath(filePath), "myValue", loc(filePath, 2));

          var jar = blobB(37);
          var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), jar);
          var translator = newTranslator(fileLoader);

          assertCall(() -> translator.translateExpr(natValS))
              .throwsException(new TranslateSbExc("Illegal value annotation: `@Native`."));
        }

        @Test
        public void bytecode_val() throws IOException {
          var clazz = ReturnAbc.class;
          var filePath = filePath(PRJ, path("my/path"));
          var classBinaryName = clazz.getCanonicalName();
          var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
          var byteValS = annValS(ann, stringTS(), modPath(filePath), "myValue", loc(filePath, 2));

          var fileLoader = createFileLoaderMock(
              filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var translator = newTranslator(fileLoader);
          assertThat(translator.translateExpr(byteValS))
              .isEqualTo(stringB("abc"));
        }
      }

      @Nested
      class _func {
        @Test
        public void def_func() {
          var funcS = defFuncS("myFunc", nlist(), intS(7));
          assertConversion(funcS, defFuncB(intB(7)));
        }

        @Test
        public void native_func() {
          var funcTS = funcTS(intTS(), blobTS());
          var filePath = filePath(PRJ, path("my/path"));
          var classBinaryName = "class.binary.name";
          var ann = natAnnS(loc(filePath, 1), stringS(classBinaryName));
          var natFuncS = natFuncS(funcTS, "myFunc", nlist(), ann);

          var funcTB = funcTB(intTB(), blobTB());
          var natFuncB = natFuncB(funcTB, blobB(37), stringB(classBinaryName), boolB(true));

          var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), blobB(37));
          var translator = newTranslator(fileLoader);
          assertThat(translator.translateExpr(natFuncS))
              .isEqualTo(natFuncB);
        }

        @Test
        public void bytecode_func() throws IOException {
          var clazz = ReturnReturnAbcFunc.class;
          var funcTS = funcTS(stringTS());
          var filePath = filePath(PRJ, path("my/path"));
          var classBinaryName = clazz.getCanonicalName();
          var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
          var byteFuncS = annFuncS(ann, funcTS, modPath(filePath), "myFunc",
              nlist(itemS(blobTS(), "p")), loc(filePath, 2));

          var fileLoader = createFileLoaderMock(
              filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var translator = newTranslator(fileLoader);
          assertThat(translator.translateExpr(byteFuncS))
              .isEqualTo(returnAbcFuncB());
        }
      }
    }

    @Nested
    class _operator {
      @Test
      public void call() {
        var defFunc = defFuncS("myFunc", nlist(), stringS("abc"));
        var call = callS(defFunc);
        assertConversion(call, callB(defFuncB(stringB("abc"))));
      }

      @Test
      public void order() {
        var order = orderS(intTS(), intS(3), intS(7));
        assertConversion(order, orderB(intB(3), intB(7)));
      }

      @Test
      public void ref() {
        var func = defFuncS("f", nlist(itemS(intTS(), "p")), refS(intTS(), "p"));
        assertConversion(func, idFuncB());
      }

      @Test
      public void select() {
        var structTS = structTS("MyStruct", nlist(sigS(stringTS(), "field")));
        var syntCtorS = syntCtorS(structTS);
        var callS = callS(syntCtorS, stringS("abc"));
        var selectS = selectS(callS, "field");

        var ctorB = defFuncB(list(stringTB()), combineB(refB(stringTB(), 0)));
        var callB = callB(ctorB, stringB("abc"));
        assertConversion(selectS, selectB(callB, intB(0)));
      }

      @Nested
      class _monoize {
        @Nested
        class _val {
          @Test
          public void defined() {
            var emptyArrayVal = emptyArrayValS();
            var monoizeS = monoizeS(aToIntVarMapS(), emptyArrayVal);
            var arrayB = orderB(intTB());
            assertConversion(monoizeS, arrayB);
          }

          @Test
          public void defined_monoized_with_type_parameter_of_enclosing_val_type_param() {
            var a = varA();
            var b = varB();

            var emptyArrayValS = emptyArrayValS();
            var bEmptyArrayMonoValS = monoizeS(ImmutableMap.of(a, b), emptyArrayValS);

            var referencingValS = polyDefValS("referencing", bEmptyArrayMonoValS);
            var referencingMonoValS = monoizeS(ImmutableMap.of(b, intTS()), referencingValS);

            var referencingInstB = orderB(intTB());
            assertConversion(referencingMonoValS, referencingInstB);
          }

          @Test
          public void bytecode() throws IOException {
            var clazz = ReturnIdFunc.class;
            var a = varA();
            var funcTS = funcTS(a, a);
            var filePath = smoothFilePath();
            var classBinaryName = clazz.getCanonicalName();
            var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
            var byteValS = polyByteValS(2, ann, funcTS, "myFunc");

            var fileLoader = createFileLoaderMock(
                filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
            var translator = newTranslator(fileLoader);
            var monoizeS = monoizeS(aToIntVarMapS(), byteValS);
            assertThat(translator.translateExpr(monoizeS))
                .isEqualTo(idFuncB());
          }
        }

        @Nested
        class _func {
          @Test
          public void defined() {
            var identity = idFuncS();
            var monoizeS = monoizeS(aToIntVarMapS(), identity);
            var funcB = defFuncB(funcTB(intTB(), intTB()), refB(intTB(), 0));
            assertConversion(monoizeS, funcB);
          }

          @Test
          public void defined_monoized_with_type_parameter_of_enclosing_func_type_param() {
            var a = varA();
            var b = varB();

            var idFuncS = idFuncS();
            var bIdMonoFuncS = monoizeS(ImmutableMap.of(a, b), idFuncS);

            var bodyS = callS(bIdMonoFuncS, refS(b, "p"));
            var wrapFuncS = polyDefFuncS(b, "wrap", nlist(itemS(b, "p")), bodyS);
            var wrapMonoFuncS = monoizeS(ImmutableMap.of(b, intTS()), wrapFuncS);

            var idFuncB = defFuncB(funcTB(intTB(), intTB()), refB(intTB(), 0));
            var wrapFuncB = defFuncB(funcTB(intTB(), intTB()),
                callB(idFuncB, refB(intTB(), 0)));
            assertConversion(wrapMonoFuncS, wrapFuncB);
          }

          @Test
          public void native_() {
            var a = varA();
            var funcTS = funcTS(a, a);
            var filePath = filePath(PRJ, path("my/path"));
            var classBinaryName = "class.binary.name";
            var ann = natAnnS(loc(filePath, 1), stringS(classBinaryName));
            var natFuncS = polyNatFuncS(funcTS, "myIdentity", nlist(itemS(a, "param")), ann);

            var funcTB = funcTB(intTB(), intTB());
            var natFuncB = natFuncB(funcTB, blobB(37), stringB(classBinaryName), boolB(true));

            var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), blobB(37));
            var translator = newTranslator(fileLoader);
            var monoizeS = monoizeS(ImmutableMap.of(a, intTS()), natFuncS);
            assertThat(translator.translateExpr(monoizeS))
                .isEqualTo(natFuncB);
          }

          @Test
          public void bytecode() throws IOException {
            var clazz = ReturnIdFunc.class;
            var a = varA();
            var funcTS = funcTS(a, a);
            var filePath = smoothFilePath();
            var classBinaryName = clazz.getCanonicalName();
            var ann = bytecodeS(classBinaryName, loc(filePath, 1));
            var byteFuncS = polyByteFuncS(ann, 1, funcTS.res(), "myFunc", nlist(itemS(a, "p")));

            var fileLoader = createFileLoaderMock(
                filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
            var translator = newTranslator(fileLoader);
            var monoizeS = monoizeS(ImmutableMap.of(a, intTS()), byteFuncS);
            assertThat(translator.translateExpr(monoizeS))
                .isEqualTo(idFuncB());
          }
        }
      }
    }
  }

  @Nested
  class _trace {
    @Nested
    class _inst {
      @Test
      public void blob() {
        var blobS = blobS(7, 0x37);
        assertLabeled(blobS, labeledLoc("0x37", 7));
      }

      @Test
      public void int_() {
        var intS = intS(7, 37);
        assertLabeled(intS, labeledLoc("37", 7));
      }

      @Test
      public void string() {
        var stringS = stringS(7, "abc");
        assertLabeled(stringS, labeledLoc("\"abc\"", 7));
      }

      @Nested
      class _named_val {
        @Test
        public void def_val() {
          var valS = defValS(3, "myValue", intS(7, 37));
          assertLabeled(valS, labeledLoc("37", 7));
        }

        @Test
        public void def_val_referencing_other_def_val() {
          var valS = defValS(5, "myValue", defValS(6, "otherValue", intS(7, 37)));
          assertLabeled(valS, labeledLoc("37", 7));
        }

        @Test
        public void bytecode_val() throws IOException {
          var clazz = ReturnAbc.class;
          var filePath = filePath();
          var classBinaryName = clazz.getCanonicalName();
          var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 7));
          var byteValS = annValS(ann, stringTS(), modPath(filePath), "myValue", loc(filePath, 8));

          var fileLoader = createFileLoaderMock(
              filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var sbTranslator = newTranslator(fileLoader);
          var exprB = sbTranslator.translateExpr(byteValS);
          assertLabeled(sbTranslator, exprB, labeledLoc("myValue", 8));
        }
      }

      @Nested
      class _func {
        @Test
        public void def_func() {
          var funcS = defFuncS(7, "myFunc", nlist(), intS(37));
          assertLabeled(funcS, labeledLoc("myFunc", 7));
        }

        @Test
        public void expr_inside_def_func_body() {
          var funcS = defFuncS(7, "myFunc", nlist(), intS(8, 37));
          var sbTranslator = newTranslator();
          var funcB = (DefFuncB) sbTranslator.translateExpr(funcS);
          var body = funcB.body();
          assertLabeled(sbTranslator, body, labeledLoc("37", 8));
        }

        @Test
        public void native_func() {
          var funcTS = funcTS(intTS(), blobTS());
          var filePath = filePath();
          var classBinaryName = "class.binary.name";
          var ann = natAnnS(loc(filePath, 1), stringS(classBinaryName));
          var natFuncS = natFuncS(2, funcTS, "myFunc", nlist(), ann);

          var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), blobB(37));
          var sbTranslator = newTranslator(fileLoader);
          assertLabeled(sbTranslator, natFuncS, labeledLoc("myFunc", 2));
        }

        @Test
        public void bytecode_func() throws IOException {
          var clazz = ReturnReturnAbcFunc.class;
          var funcTS = funcTS(stringTS());
          var filePath = filePath();
          var classBinaryName = clazz.getCanonicalName();
          var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
          var byteFuncS = annFuncS(ann, funcTS, modPath(filePath), "myFunc",
              nlist(itemS(blobTS(), "p")), loc(filePath, 2));

          var fileLoader = createFileLoaderMock(
              filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var sbTranslator = newTranslator(fileLoader);
          assertLabeled(sbTranslator, byteFuncS, labeledLoc("myFunc", 2));
        }
      }
    }

    @Nested
    class _operator {
      @Test
      public void call() {
        var defFunc = defFuncS(7, "myFunc", nlist(), stringS("abc"));
        var call = callS(8, defFunc);
        assertLabeled(call, labeledLoc("()", 8));
      }

      @Test
      public void order() {
        var order = orderS(3, intTS(), intS(6), intS(7));
        assertLabeled(order, labeledLoc("[]", 3));
      }

      @Test
      public void ref() {
        var func = defFuncS(4, "myFunc", nlist(itemS(intTS(), "p")), refS(5, intTS(), "p"));
        var sbTranslator = newTranslator();
        var funcB = (DefFuncB) sbTranslator.translateExpr(func);
        var refB = funcB.body();
        assertLabeled(sbTranslator, refB, labeledLoc("(p)", 5));
      }

      @Test
      public void select() {
        var structTS = structTS("MyStruct", nlist(sigS(stringTS(), "field")));
        var syntCtorS = syntCtorS(structTS);
        var callS = callS(syntCtorS, stringS("abc"));
        var selectS = selectS(4, callS, "field");
        assertLabeled(selectS, labeledLoc(".field", 4));
      }

      @Nested
      class _monoize {
        @Test
        public void def_val() {
          var emptyArrayVal = emptyArrayValS();
          var monoizeS = monoizeS(4, aToIntVarMapS(), emptyArrayVal);
          assertLabeled(monoizeS, labeledLoc("[]", 1));
        }

        @Test
        public void def_func() {
          var identity = idFuncS();
          var monoizeS = monoizeS(aToIntVarMapS(), identity);
          assertLabeled(monoizeS, labeledLoc("myId", 1));
        }
      }
    }
  }

  @Nested
  class _caching {
    @Test
    public void def_val_conversion_result() {
      assertConversionIsCached(defValS("myVal", stringS("abcdefghi")));
    }

    @Test
    public void bytecode_val_conversion_result() throws IOException {
      var clazz = ReturnAbc.class;
      var filePath = filePath(PRJ, path("my/path"));
      var classBinaryName = clazz.getCanonicalName();
      var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
      var bytecodeValS = annValS(ann, stringTS(), modPath(filePath), "myFunc", loc(filePath, 2));
      var fileLoader = createFileLoaderMock(
          filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));

      assertConversionIsCached(bytecodeValS, newTranslator(fileLoader));
    }

    @Test
    public void def_func_conversion_result() {
      assertConversionIsCached(defFuncS("myFunc", nlist(), stringS("abcdefghi")));
    }

    @Test
    public void nat_func_conversion_result() {
      assertConversionIsCached(natFuncS(funcTS(stringTS()), "myFunc", nlist()));
    }

    @Test
    public void bytecode_func_conversion_result() throws IOException {
      var clazz = ReturnReturnAbcFunc.class;
      var funcTS = funcTS(stringTS());
      var filePath = filePath(PRJ, path("my/path"));
      var classBinaryName = clazz.getCanonicalName();
      var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
      var bytecodeFuncS = annFuncS(ann, funcTS, modPath(filePath), "myFunc", nlist(), loc(filePath, 2));
      var fileLoader = createFileLoaderMock(
          filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));

      assertConversionIsCached(bytecodeFuncS, newTranslator(fileLoader));
    }

    @Test
    public void synt_ctor_conversion_result() {
      assertConversionIsCached(syntCtorS(structTS("MyStruct", nlist(sigS(stringTS(), "name")))));
    }

    @Test
    public void monoized_poly_func_conversion_result() {
      var monoizeS = monoizeS(aToIntVarMapS(), idFuncS());
      assertConversionIsCached(monoizeS);
    }

    private void assertConversionIsCached(ExprS exprS) {
      var translator = newTranslator();
      assertConversionIsCached(exprS, translator);
    }

    private void assertConversionIsCached(ExprS exprS, SbTranslator sbTranslator) {
      assertThat(sbTranslator.translateExpr(exprS))
          .isSameInstanceAs(sbTranslator.translateExpr(exprS));
    }
  }

  private void assertConversion(ExprS exprS, ExprB expected) {
    assertThat(newTranslator().translateExpr(exprS))
        .isEqualTo(expected);
  }

  private void assertLabeled(ExprS exprS, LabeledLoc expected) {
    assertLabeled(newTranslator(), exprS, expected);
  }

  private static void assertLabeled(SbTranslator sbTranslator, ExprS exprS, LabeledLoc expected) {
    var exprB = sbTranslator.translateExpr(exprS);
    assertLabeled(sbTranslator, exprB, expected);
  }

  private static void assertLabeled(SbTranslator sbTranslator, ExprB exprB, LabeledLoc expected) {
    var actual = sbTranslator.labels().get(exprB);
    assertThat(actual.label())
        .isEqualTo(expected.label());
    assertThat(actual.loc())
        .isEqualTo(expected.loc());
  }

  private SbTranslator newTranslator() {
    try {
      FileLoader mock = mock(FileLoader.class);
      when(mock.load(any())).thenReturn(blobB(1));
      return newTranslator(mock);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private SbTranslator newTranslator(FileLoader fileLoader) {
    return sbTranslatorProv(fileLoader).get();
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
