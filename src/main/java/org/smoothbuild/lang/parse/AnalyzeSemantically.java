package org.smoothbuild.lang.parse;

import static java.lang.String.join;
import static java.util.Comparator.comparing;
import static org.smoothbuild.lang.base.type.api.TypeNames.isVarName;
import static org.smoothbuild.lang.parse.ParseError.parseError;
import static org.smoothbuild.lang.parse.ast.FuncTypeN.countFuncVars;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.smoothbuild.cli.console.ImmutableLogs;
import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.LogBuffer;
import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.lang.base.define.DefsS;
import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.lang.parse.ast.ArrayTypeN;
import org.smoothbuild.lang.parse.ast.Ast;
import org.smoothbuild.lang.parse.ast.AstVisitor;
import org.smoothbuild.lang.parse.ast.BlobN;
import org.smoothbuild.lang.parse.ast.EvalN;
import org.smoothbuild.lang.parse.ast.FuncN;
import org.smoothbuild.lang.parse.ast.FuncTypeN;
import org.smoothbuild.lang.parse.ast.IntN;
import org.smoothbuild.lang.parse.ast.ItemN;
import org.smoothbuild.lang.parse.ast.NamedN;
import org.smoothbuild.lang.parse.ast.StringN;
import org.smoothbuild.lang.parse.ast.StructN;
import org.smoothbuild.lang.parse.ast.TypeN;
import org.smoothbuild.lang.parse.ast.ValN;
import org.smoothbuild.util.DecodeHexExc;
import org.smoothbuild.util.UnescapingFailedExc;
import org.smoothbuild.util.collect.CountersMap;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class AnalyzeSemantically {
  public static ImmutableLogs analyzeSemantically(DefsS imported, Ast ast) {
    var logBuffer = new LogBuffer();
    decodeBlobLiterals(logBuffer, ast);
    decodeIntLiterals(logBuffer, ast);
    decodeStringLiterals(logBuffer, ast);
    detectUndefinedTypes(logBuffer, imported, ast);
    detectDuplicateGlobalNames(logBuffer, imported, ast);
    detectDuplicateFieldNames(logBuffer, ast);
    detectDuplicateParamNames(logBuffer, ast);
    detectStructNameWithSingleCapitalLetter(logBuffer, ast);
    detectIllegalPolytypes(logBuffer, ast);
    detectIllegalNatives(logBuffer, ast);
    return logBuffer.toImmutableLogs();
  }

  private static void decodeBlobLiterals(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitBlobLiteral(BlobN blob) {
        super.visitBlobLiteral(blob);
        try {
          blob.decodeByteString();
        } catch (DecodeHexExc e) {
          logger.log(parseError(blob, "Illegal Blob literal. " + e.getMessage()));
        }
      }
    }.visitAst(ast);
  }

  private static void decodeIntLiterals(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitIntLiteral(IntN intN) {
        super.visitIntLiteral(intN);
        try {
          intN.decodeBigInteger();
        } catch (NumberFormatException e) {
          logger.log(parseError(intN, "Illegal Int literal: `" + intN.literal() + "`."));
        }
      }
    }.visitAst(ast);
  }

  private static void decodeStringLiterals(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitStringLiteral(StringN string) {
        super.visitStringLiteral(string);
        try {
          string.calculateUnescaped();
        } catch (UnescapingFailedExc e) {
          logger.log(parseError(string, e.getMessage()));
        }
      }
    }.visitAst(ast);
  }

  private static void detectUndefinedTypes(Logger logger, DefsS imported, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitFunc(FuncN funcN) {
        super.visitFunc(funcN);
        funcN.typeNode().ifPresent(this::assertTypeIsDefined);
      }

      @Override
      public void visitValue(ValN valN) {
        super.visitValue(valN);
        valN.typeNode().ifPresent(this::assertTypeIsDefined);
      }

      @Override
      public void visitParam(int index, ItemN param) {
        param.typeNode().ifPresent(this::assertTypeIsDefined);
      }

      @Override
      public void visitField(ItemN field) {
        field.typeNode().ifPresent(this::assertTypeIsDefined);
      }

      private void assertTypeIsDefined(TypeN type) {
        if (type instanceof ArrayTypeN array) {
          assertTypeIsDefined(array.elemType());
        } else if (type instanceof FuncTypeN func) {
          assertTypeIsDefined(func.resType());
          func.paramTypes().forEach(this::assertTypeIsDefined);
        } else if (!isDefinedType(type)) {
          logger.log(parseError(type.loc(), type.q() + " type is undefined."));
        }
      }

      private boolean isDefinedType(TypeN type) {
        return isVarName(type.name())
            || ast.structs().containsName(type.name())
            || imported.types().containsName(type.name());
      }
    }.visitAst(ast);
  }

  private static void detectDuplicateGlobalNames(Logger logger, DefsS imported, Ast ast) {
    List<Nal> nals = new ArrayList<>();
    nals.addAll(ast.structs());
    nals.addAll(map(ast.structs(), StructN::ctor));
    nals.addAll(ast.topEvals());
    nals.sort(comparing(n -> n.loc().line()));

    for (Nal nal : nals) {
      logIfDuplicate(logger, imported.types(), nal);
      logIfDuplicate(logger, imported.topEvals(), nal);
    }
    Map<String, Nal> checked = new HashMap<>();
    for (Nal nal : nals) {
      logIfDuplicate(logger, checked, nal);
      checked.put(nal.name(), nal);
    }
  }

  private static void logIfDuplicate(Logger logger, NList<? extends Nal> others, Nal nal) {
    String name = nal.name();
    if (others.containsName(name)) {
      Nal otherNal = others.get(name);
      Loc loc = otherNal.loc();
      logger.log(alreadyDefinedError(nal, loc));
    }
  }

  private static void logIfDuplicate(Logger logger, Map<String, ? extends Nal> others, Nal nal) {
    String name = nal.name();
    if (others.containsKey(name)) {
      Nal otherNal = others.get(name);
      Loc loc = otherNal.loc();
      logger.log(alreadyDefinedError(nal, loc));
    }
  }

  private static void detectDuplicateFieldNames(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitFields(List<ItemN> fields) {
        super.visitFields(fields);
        findDuplicateNames(logger, fields);
      }
    }.visitAst(ast);
  }

  private static void detectDuplicateParamNames(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitParams(List<ItemN> params) {
        super.visitParams(params);
        findDuplicateNames(logger, params);
      }
    }.visitAst(ast);
  }

  private static void findDuplicateNames(Logger logger, List<? extends NamedN> nodes) {
    Map<String, Loc> alreadyDefined = new HashMap<>();
    for (NamedN named : nodes) {
      String name = named.name();
      if (alreadyDefined.containsKey(name)) {
        logger.log(alreadyDefinedError(named, alreadyDefined.get(name)));
      }
      alreadyDefined.put(name, named.loc());
    }
  }

  private static void detectStructNameWithSingleCapitalLetter(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitStruct(StructN struct) {
        String name = struct.name();
        if (isVarName(name)) {
          logger.log(parseError(struct.loc(),
              "`" + name + "` is illegal struct name. It must have at least two characters."));
        }
      }
    }.visitAst(ast);
  }

  private static void detectIllegalPolytypes(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitValue(ValN valN) {
        super.visitValue(valN);
        if (valN.typeNode().isPresent()) {
          logErrorIfNeeded(valN, valN.typeNode().get().varsUsedOnce());
        }
      }

      @Override
      public void visitFunc(FuncN funcN) {
        super.visitFunc(funcN);
        if (funcN.typeNode().isPresent()) {
          var counters = new CountersMap<String>();
          countFuncVars(counters, funcN.typeNode().get(),
              map(funcN.params(), itemNode -> itemNode.typeNode().get()));
          logErrorIfNeeded(funcN, counters.keysWithCounter(1));
        }
      }

      @Override
      public void visitStruct(StructN struct) {
        super.visitStruct(struct);
        List<ItemN> fields = struct.fields();
        for (ItemN field : fields) {
          logErrorIfNeeded(field, field.typeNode().get().varsUsedOnce());
        }
      }

      private void logErrorIfNeeded(
          EvalN node, ImmutableList<String> varsUsedOnce) {
        if (!varsUsedOnce.isEmpty()) {
          logError(node, varsUsedOnce);
        }
      }

      private void logError(EvalN node, List<String> varsUsedOnce) {
        logger.log(parseError(node.typeNode().get(), "Type var(s) "
            + join(", ", map(varsUsedOnce, v -> "`" + v + "`"))
            + " are used once in declaration of " + node.q()
            + ". This means each one can be replaced with `Any`."));
      }
    }.visitAst(ast);
  }

  /**
   * Detects:
   *  - func with body and @Native annotation
   *  - func without body nor @Native annotation
   *  - value with @Native annotation
   */
  private static void detectIllegalNatives(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitFunc(FuncN funcN) {
        super.visitFunc(funcN);
        if (funcN.ann().isPresent() && funcN.body().isPresent()) {
          logger.log(parseError(funcN, "Native function cannot have body."));
        }
        if (funcN.ann().isEmpty() && funcN.body().isEmpty()) {
          logger.log(parseError(funcN, "Non native function cannot have empty body."));
        }
      }

      @Override
      public void visitValue(ValN valN) {
        super.visitValue(valN);
        if (valN.ann().isPresent()) {
          logger.log(parseError(valN.ann().get(), "Value cannot have @Native annotation."));
        }
        if (valN.body().isEmpty()) {
          logger.log(parseError(valN, "Value cannot have empty body."));
        }
      }
    }.visitAst(ast);
  }

  private static Log alreadyDefinedError(Nal nal, Loc loc) {
    String atLoc = loc.equals(Loc.internal())
        ? " internally."
        : " at " + loc + ".";
    return parseError(nal.loc(), "`" + nal.name() + "` is already defined" + atLoc);
  }
}
