package org.smoothbuild.compile.ps;

import static java.util.Comparator.comparing;
import static org.smoothbuild.compile.lang.base.ValidNamesS.isVarName;
import static org.smoothbuild.compile.ps.CompileError.compileError;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.smoothbuild.compile.lang.base.Nal;
import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.compile.lang.define.DefsS;
import org.smoothbuild.compile.ps.ast.Ast;
import org.smoothbuild.compile.ps.ast.AstVisitor;
import org.smoothbuild.compile.ps.ast.expr.BlobP;
import org.smoothbuild.compile.ps.ast.expr.IntP;
import org.smoothbuild.compile.ps.ast.expr.ItemP;
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
    decodeLiterals(logBuffer, ast);
    detectUndefinedTypes(logBuffer, imported, ast);
    detectDuplicateGlobalNames(logBuffer, imported, ast);
    detectDuplicateFieldNames(logBuffer, ast);
    detectDuplicateParamNames(logBuffer, ast);
    return logBuffer.toImmutableLogs();
  }

  private static void decodeLiterals(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitBlob(BlobP blob) {
        super.visitBlob(blob);
        try {
          blob.decodeByteString();
        } catch (DecodeHexExc e) {
          logger.log(compileError(blob, "Illegal Blob literal: " + e.getMessage()));
        }
      }

      @Override
      public void visitInt(IntP intP) {
        super.visitInt(intP);
        try {
          intP.decodeBigInteger();
        } catch (NumberFormatException e) {
          logger.log(compileError(intP, "Illegal Int literal: `" + intP.literal() + "`."));
        }
      }

      @Override
      public void visitString(StringP string) {
        super.visitString(string);
        try {
          string.calculateUnescaped();
        } catch (UnescapingFailedExc e) {
          logger.log(compileError(string, "Illegal String literal: " + e.getMessage()));
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
            || imported.types().contains(type.name());
      }
    }.visitAst(ast);
  }

  private static void detectDuplicateGlobalNames(Logger logger, DefsS imported, Ast ast) {
    List<Nal> nals = new ArrayList<>();
    nals.addAll(ast.structs());
    nals.addAll(map(ast.structs(), StructP::constructor));
    nals.addAll(ast.evaluables());
    nals.sort(comparing(n -> n.location().toString()));

    for (Nal nal : nals) {
      logIfDuplicate(logger, imported.types(), nal);
      logIfDuplicate(logger, imported.evaluables(), nal);
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

  private static Log alreadyDefinedError(Nal nal, Location location) {
    return compileError(nal.location(),
        "`" + nal.name() + "` is already defined at " + location.description() + ".");
  }
}
