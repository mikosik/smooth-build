package org.smoothbuild.parse;

import static java.util.Comparator.comparing;
import static org.smoothbuild.lang.type.api.AnnotationNames.ANNOTATION_NAMES;
import static org.smoothbuild.lang.type.api.AnnotationNames.BYTECODE;
import static org.smoothbuild.lang.type.api.AnnotationNames.NATIVE_IMPURE;
import static org.smoothbuild.lang.type.api.AnnotationNames.NATIVE_PURE;
import static org.smoothbuild.lang.type.impl.TNamesS.isVarName;
import static org.smoothbuild.parse.ParseError.parseError;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.define.Nal;
import org.smoothbuild.out.log.ImmutableLogs;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.parse.ast.ArrayTN;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.AstVisitor;
import org.smoothbuild.parse.ast.BlobN;
import org.smoothbuild.parse.ast.FuncN;
import org.smoothbuild.parse.ast.FuncTN;
import org.smoothbuild.parse.ast.IntN;
import org.smoothbuild.parse.ast.ItemN;
import org.smoothbuild.parse.ast.NamedN;
import org.smoothbuild.parse.ast.StringN;
import org.smoothbuild.parse.ast.StructN;
import org.smoothbuild.parse.ast.TypeN;
import org.smoothbuild.parse.ast.ValN;
import org.smoothbuild.util.DecodeHexExc;
import org.smoothbuild.util.UnescapingFailedExc;
import org.smoothbuild.util.collect.NList;

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
    detectIllegalAnnotations(logBuffer, ast);
    return logBuffer.toImmutableLogs();
  }

  private static void decodeBlobLiterals(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitBlob(BlobN blob) {
        super.visitBlob(blob);
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
      public void visitInt(IntN intN) {
        super.visitInt(intN);
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
      public void visitString(StringN string) {
        super.visitString(string);
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
        funcN.evalT().ifPresent(this::assertTypeIsDefined);
      }

      @Override
      public void visitValue(ValN valN) {
        super.visitValue(valN);
        valN.evalT().ifPresent(this::assertTypeIsDefined);
      }

      @Override
      public void visitParam(int index, ItemN param) {
        param.evalT().ifPresent(this::assertTypeIsDefined);
      }

      @Override
      public void visitField(ItemN field) {
        field.evalT().ifPresent(this::assertTypeIsDefined);
      }

      private void assertTypeIsDefined(TypeN type) {
        if (type instanceof ArrayTN array) {
          assertTypeIsDefined(array.elemT());
        } else if (type instanceof FuncTN func) {
          assertTypeIsDefined(func.resT());
          func.paramTs().forEach(this::assertTypeIsDefined);
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

  private static void detectIllegalAnnotations(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitFunc(FuncN funcN) {
        super.visitFunc(funcN);
        if (funcN.ann().isPresent()) {
          var ann = funcN.ann().get();
          var annName = ann.name();
          if (ANNOTATION_NAMES.contains(annName)) {
            if (funcN.body().isPresent()) {
              logger.log(parseError(funcN,
                  "Function " + funcN.q() + " with @" + annName + " annotation cannot have body."));
            }
            if (funcN.evalT().isEmpty()) {
              logger.log(parseError(funcN, "Function " + funcN.q() + " with @" + annName
                  + " annotation must declare result type."));
            }
          } else {
            logger.log(parseError(ann, "Unknown annotation " + ann.q() + "."));
          }
        } else if (funcN.body().isEmpty()) {
          logger.log(parseError(funcN, "Function body is missing."));
        }
      }

      @Override
      public void visitValue(ValN valN) {
        super.visitValue(valN);
        if (valN.ann().isPresent()) {
          var ann = valN.ann().get();
          var annName = ann.name();
          switch (annName) {
            case BYTECODE -> {
              if (valN.body().isPresent()) {
                logger.log(
                    parseError(valN, "Value with @" + annName + " annotation cannot have body."));
              }
              if (valN.evalT().isEmpty()) {
                logger.log(parseError(valN, "Value " + valN.q() + " with @" + annName
                    + " annotation must declare type."));
              }
            }
            case NATIVE_PURE, NATIVE_IMPURE -> logger.log(
                parseError(valN.ann().get(), "Value cannot have @" + annName + " annotation."));
            default -> logger.log(parseError(ann, "Unknown annotation " + ann.q() + "."));
          }
        } else if (valN.body().isEmpty()) {
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
