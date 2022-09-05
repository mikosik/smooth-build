package org.smoothbuild.compile.ps;

import static java.util.Comparator.comparing;
import static org.smoothbuild.compile.lang.type.AnnotationNames.ANNOTATION_NAMES;
import static org.smoothbuild.compile.lang.type.AnnotationNames.BYTECODE;
import static org.smoothbuild.compile.lang.type.AnnotationNames.NATIVE_IMPURE;
import static org.smoothbuild.compile.lang.type.AnnotationNames.NATIVE_PURE;
import static org.smoothbuild.compile.lang.type.TNamesS.isVarName;
import static org.smoothbuild.compile.ps.ParseError.parseError;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.Nal;
import org.smoothbuild.compile.lang.define.DefsS;
import org.smoothbuild.compile.ps.ast.Ast;
import org.smoothbuild.compile.ps.ast.AstVisitor;
import org.smoothbuild.compile.ps.ast.StructP;
import org.smoothbuild.compile.ps.ast.expr.BlobP;
import org.smoothbuild.compile.ps.ast.expr.IntP;
import org.smoothbuild.compile.ps.ast.expr.StringP;
import org.smoothbuild.compile.ps.ast.refable.FuncP;
import org.smoothbuild.compile.ps.ast.refable.ItemP;
import org.smoothbuild.compile.ps.ast.refable.ValP;
import org.smoothbuild.compile.ps.ast.type.ArrayTP;
import org.smoothbuild.compile.ps.ast.type.FuncTP;
import org.smoothbuild.compile.ps.ast.type.TypeP;
import org.smoothbuild.out.log.ImmutableLogs;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.util.DecodeHexExc;
import org.smoothbuild.util.UnescapingFailedExc;
import org.smoothbuild.util.bindings.Bindings;

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
      public void visitBlob(BlobP blob) {
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
      public void visitInt(IntP intP) {
        super.visitInt(intP);
        try {
          intP.decodeBigInteger();
        } catch (NumberFormatException e) {
          logger.log(parseError(intP, "Illegal Int literal: `" + intP.literal() + "`."));
        }
      }
    }.visitAst(ast);
  }

  private static void decodeStringLiterals(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitString(StringP string) {
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
      public void visitType(TypeP type) {
        if (type instanceof ArrayTP array) {
          visitType(array.elemT());
        } else if (type instanceof FuncTP func) {
          visitType(func.resT());
          func.paramTs().forEach(this::visitType);
        } else if (!isDefinedType(type)) {
          logger.log(parseError(type.loc(), type.q() + " type is undefined."));
        }
      }

      private boolean isDefinedType(TypeP type) {
        return isVarName(type.name())
            || ast.structs().containsName(type.name())
            || imported.tDefs().contains(type.name());
      }
    }.visitAst(ast);
  }

  private static void detectDuplicateGlobalNames(Logger logger, DefsS imported, Ast ast) {
    List<Nal> nals = new ArrayList<>();
    nals.addAll(ast.structs());
    nals.addAll(map(ast.structs(), StructP::ctor));
    nals.addAll(ast.refables());
    nals.sort(comparing(n -> n.loc().line()));

    for (Nal nal : nals) {
      logIfDuplicate(logger, imported.tDefs(), nal);
      logIfDuplicate(logger, imported.refables(), nal);
    }
    Map<String, Nal> checked = new HashMap<>();
    for (Nal nal : nals) {
      logIfDuplicate(logger, checked, nal);
      checked.put(nal.name(), nal);
    }
  }

  private static void logIfDuplicate(Logger logger, Bindings<? extends Nal> others, Nal nal) {
    Nal other = others.getOrNull(nal.name());
    if (other != null) {
      logger.log(alreadyDefinedError(nal, other.loc()));
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
      public void visitFields(List<ItemP> fields) {
        super.visitFields(fields);
        findDuplicateNames(logger, fields);
      }
    }.visitAst(ast);
  }

  private static void detectDuplicateParamNames(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitParams(List<ItemP> params) {
        super.visitParams(params);
        findDuplicateNames(logger, params);
      }
    }.visitAst(ast);
  }

  private static void findDuplicateNames(Logger logger, List<? extends Nal> nodes) {
    Map<String, Loc> alreadyDefined = new HashMap<>();
    for (Nal named : nodes) {
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
      public void visitStruct(StructP struct) {
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
      public void visitFunc(FuncP funcP) {
        super.visitFunc(funcP);
        if (funcP.ann().isPresent()) {
          var ann = funcP.ann().get();
          var annName = ann.name();
          if (ANNOTATION_NAMES.contains(annName)) {
            if (funcP.body().isPresent()) {
              logger.log(parseError(funcP,
                  "Function " + funcP.q() + " with @" + annName + " annotation cannot have body."));
            }
            if (funcP.resT().isEmpty()) {
              logger.log(parseError(funcP, "Function " + funcP.q() + " with @" + annName
                  + " annotation must declare result type."));
            }
          } else {
            logger.log(parseError(ann.loc(), "Unknown annotation " + ann.q() + "."));
          }
        } else if (funcP.body().isEmpty()) {
          logger.log(parseError(funcP, "Function body is missing."));
        }
      }

      @Override
      public void visitValue(ValP valP) {
        super.visitValue(valP);
        if (valP.ann().isPresent()) {
          var ann = valP.ann().get();
          var annName = ann.name();
          switch (annName) {
            case BYTECODE -> {
              if (valP.body().isPresent()) {
                logger.log(
                    parseError(valP, "Value with @" + annName + " annotation cannot have body."));
              }
              if (valP.type().isEmpty()) {
                logger.log(parseError(valP, "Value " + valP.q() + " with @" + annName
                    + " annotation must declare type."));
              }
            }
            case NATIVE_PURE, NATIVE_IMPURE -> logger.log(
                parseError(valP.ann().get(), "Value cannot have @" + annName + " annotation."));
            default -> logger.log(parseError(ann, "Unknown annotation " + ann.q() + "."));
          }
        } else if (valP.body().isEmpty()) {
          logger.log(parseError(valP, "Value cannot have empty body."));
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
