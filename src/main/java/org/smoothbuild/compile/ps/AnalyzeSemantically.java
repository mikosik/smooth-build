package org.smoothbuild.compile.ps;

import static java.util.Comparator.comparing;
import static org.smoothbuild.compile.lang.base.ValidNamesS.isVarName;
import static org.smoothbuild.compile.lang.base.ValidNamesS.startsWithLowerCase;
import static org.smoothbuild.compile.lang.base.ValidNamesS.startsWithUpperCase;
import static org.smoothbuild.compile.lang.base.location.Locations.internalLocation;
import static org.smoothbuild.compile.lang.type.AnnotationNames.ANNOTATION_NAMES;
import static org.smoothbuild.compile.lang.type.AnnotationNames.BYTECODE;
import static org.smoothbuild.compile.lang.type.AnnotationNames.NATIVE_IMPURE;
import static org.smoothbuild.compile.lang.type.AnnotationNames.NATIVE_PURE;
import static org.smoothbuild.compile.ps.CompileError.compileError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.smoothbuild.compile.lang.base.Nal;
import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.compile.lang.define.DefsS;
import org.smoothbuild.compile.ps.ast.Ast;
import org.smoothbuild.compile.ps.ast.AstVisitor;
import org.smoothbuild.compile.ps.ast.expr.AnonymousFuncP;
import org.smoothbuild.compile.ps.ast.expr.BlobP;
import org.smoothbuild.compile.ps.ast.expr.IntP;
import org.smoothbuild.compile.ps.ast.expr.ItemP;
import org.smoothbuild.compile.ps.ast.expr.NamedFuncP;
import org.smoothbuild.compile.ps.ast.expr.NamedValueP;
import org.smoothbuild.compile.ps.ast.expr.RefableP;
import org.smoothbuild.compile.ps.ast.expr.StringP;
import org.smoothbuild.compile.ps.ast.expr.StructP;
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
    detectIllegalNames(logBuffer, ast);
    detectIllegalAnnotations(logBuffer, ast);
    detectStructFieldWithDefaultValue(logBuffer, ast);
    detectAnonymousFuncParamWithDefaultValue(logBuffer, ast);
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
          logger.log(compileError(blob, "Illegal Blob literal. " + e.getMessage()));
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
          logger.log(compileError(intP, "Illegal Int literal: `" + intP.literal() + "`."));
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
          logger.log(compileError(string, e.getMessage()));
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
          logger.log(compileError(type.location(), type.q() + " type is undefined."));
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
    nals.addAll(constructorNames(ast));
    nals.addAll(ast.evaluables());
    nals.sort(comparing(n -> n.location().toString()));

    for (Nal nal : nals) {
      logIfDuplicate(logger, imported.tDefs(), nal);
      logIfDuplicate(logger, imported.evaluables(), nal);
    }
    Map<String, Nal> checked = new HashMap<>();
    for (Nal nal : nals) {
      logIfDuplicate(logger, checked, nal);
      checked.put(nal.name(), nal);
    }
  }

  private static List<NamedFuncP> constructorNames(Ast ast) {
    // Return only constructors of structs with legal names (that starts with uppercase).
    // Adding constructors of structs with lowercase names would cause `already defined` error
    // because constructor name would collide with struct name.
    // Lowercase struct names will be detected as illegal by other check in this class.
    return ast.structs()
        .stream()
        .filter(s -> startsWithUpperCase(s.name()))
        .map(StructP::constructor)
        .toList();
  }

  private static void logIfDuplicate(Logger logger, Bindings<? extends Nal> others, Nal nal) {
    Nal other = others.getOrNull(nal.name());
    if (other != null) {
      logger.log(alreadyDefinedError(nal, other.location()));
    }
  }

  private static void logIfDuplicate(Logger logger, Map<String, ? extends Nal> others, Nal nal) {
    String name = nal.name();
    if (others.containsKey(name)) {
      logger.log(alreadyDefinedError(nal, others.get(name).location()));
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
    Map<String, Location> alreadyDefined = new HashMap<>();
    for (Nal named : nodes) {
      String name = named.name();
      if (alreadyDefined.containsKey(name)) {
        logger.log(alreadyDefinedError(named, alreadyDefined.get(name)));
      }
      alreadyDefined.put(name, named.location());
    }
  }

  private static void detectIllegalNames(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitIdentifier(RefableP refable) {
        var name = refable.name();
        if (name.equals("_")) {
          logger.log(compileError(refable.location(), "`" + name + "` is illegal identifier name. "
              + "`_` is reserved for future use."));
        } else if (!startsWithLowerCase(name)) {
          logger.log(compileError(refable.location(), "`" + name + "` is illegal identifier name. "
          + "Identifiers should start with lowercase."));
        }
      }

      @Override
      public void visitStruct(StructP struct) {
        super.visitStruct(struct);
        var name = struct.name();
        if (name.equals("_")) {
          logger.log(compileError(struct.location(), "`" + name + "` is illegal struct name. "
              + "`_` is reserved for future use."));
        } else if (isVarName(name)) {
          logger.log(compileError(struct.location(), "`" + name + "` is illegal struct name."
              + " All-uppercase names are reserved for type variables in generic types."));
        } else if (!startsWithUpperCase(name)) {
          logger.log(compileError(struct.location(), "`" + name + "` is illegal struct name."
              + " Struct name must start with uppercase letter."));
        }
      }
    }.visitAst(ast);
  }

  private static void detectIllegalAnnotations(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitNamedFunc(NamedFuncP namedFuncP) {
        super.visitNamedFunc(namedFuncP);
        if (namedFuncP.annotation().isPresent()) {
          var ann = namedFuncP.annotation().get();
          var annName = ann.name();
          if (ANNOTATION_NAMES.contains(annName)) {
            if (namedFuncP.body().isPresent()) {
              logger.log(compileError(namedFuncP,
                  "Function " + namedFuncP.q() + " with @" + annName + " annotation cannot have body."));
            }
            if (namedFuncP.resT().isEmpty()) {
              logger.log(compileError(namedFuncP, "Function " + namedFuncP.q() + " with @" + annName
                  + " annotation must declare result type."));
            }
          } else {
            logger.log(compileError(ann.location(), "Unknown annotation " + ann.q() + "."));
          }
        } else if (namedFuncP.body().isEmpty()) {
          logger.log(compileError(namedFuncP, "Function body is missing."));
        }
      }

      @Override
      public void visitNamedValue(NamedValueP namedValueP) {
        super.visitNamedValue(namedValueP);
        if (namedValueP.annotation().isPresent()) {
          var ann = namedValueP.annotation().get();
          var annName = ann.name();
          switch (annName) {
            case BYTECODE -> {
              if (namedValueP.body().isPresent()) {
                logger.log(
                    compileError(namedValueP, "Value with @" + annName + " annotation cannot have body."));
              }
              if (namedValueP.type().isEmpty()) {
                logger.log(compileError(namedValueP, "Value " + namedValueP.q() + " with @" + annName
                    + " annotation must declare type."));
              }
            }
            case NATIVE_PURE, NATIVE_IMPURE -> logger.log(
                compileError(
                    namedValueP.annotation().get(),
                    "Value cannot have @" + annName + " annotation."));
            default -> logger.log(compileError(ann, "Unknown annotation " + ann.q() + "."));
          }
        } else if (namedValueP.body().isEmpty()) {
          logger.log(compileError(namedValueP, "Value cannot have empty body."));
        }

      }
    }.visitAst(ast);
  }

  private static void detectStructFieldWithDefaultValue(LogBuffer logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitField(ItemP field) {
        super.visitField(field);
        if (field.defaultValue().isPresent()) {
          logger.log(compileError(field.location(), "Struct field `" + field.name()
              + "` has default value. Only function parameters can have default value."));
        }
      }
    }.visitAst(ast);
  }

  private static void detectAnonymousFuncParamWithDefaultValue(LogBuffer logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitAnonymousFunc(AnonymousFuncP anonymousFuncP) {
        super.visitAnonymousFunc(anonymousFuncP);
        anonymousFuncP.params().forEach(this::logErrorIfDefaultValuePresent);
      }

      private void logErrorIfDefaultValuePresent(ItemP param) {
        if (param.defaultValue().isPresent()) {
          logger.log(compileError(param.location(),
              "Parameter " + param.q() + " of anonymous function cannot have default value."));
        }
      }
    }.visitAst(ast);
  }

  private static Log alreadyDefinedError(Nal nal, Location location) {
    return compileError(nal.location(),
        "`" + nal.name() + "` is already defined at " + location.description() + ".");
  }
}
