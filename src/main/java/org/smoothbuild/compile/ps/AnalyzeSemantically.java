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
import org.smoothbuild.compile.lang.define.DefinitionsS;
import org.smoothbuild.compile.ps.ast.ModuleVisitorP;
import org.smoothbuild.compile.ps.ast.expr.ItemP;
import org.smoothbuild.compile.ps.ast.expr.ModuleP;
import org.smoothbuild.compile.ps.ast.expr.StructP;
import org.smoothbuild.compile.ps.ast.type.ArrayTP;
import org.smoothbuild.compile.ps.ast.type.FuncTP;
import org.smoothbuild.compile.ps.ast.type.TypeP;
import org.smoothbuild.out.log.ImmutableLogs;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logger;

public class AnalyzeSemantically {
  public static ImmutableLogs analyzeSemantically(DefinitionsS imported, ModuleP moduleP) {
    var logBuffer = new LogBuffer();
    detectUndefinedTypes(logBuffer, imported, moduleP);
    detectDuplicateGlobalNames(moduleP, logBuffer);
    detectDuplicateFieldNames(logBuffer, moduleP);
    detectDuplicateParamNames(logBuffer, moduleP);
    return logBuffer.toImmutableLogs();
  }

  private static void detectUndefinedTypes(Logger logger, DefinitionsS imported, ModuleP moduleP) {
    new ModuleVisitorP() {
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
            || moduleP.structs().containsName(type.name())
            || imported.types().contains(type.name());
      }
    }.visitModule(moduleP);
  }

  private static void detectDuplicateGlobalNames(ModuleP moduleP, Logger logger) {
    List<Nal> nals = new ArrayList<>();
    nals.addAll(moduleP.structs());
    nals.addAll(map(moduleP.structs(), StructP::constructor));
    nals.addAll(moduleP.evaluables());
    nals.sort(comparing(n -> n.location().toString()));

    Map<String, Nal> checked = new HashMap<>();
    for (Nal nal : nals) {
      logIfDuplicate(logger, checked, nal);
      checked.put(nal.name(), nal);
    }
  }

  private static void logIfDuplicate(Logger logger, Map<String, ? extends Nal> others, Nal nal) {
    var name = nal.name();
    if (others.containsKey(name)) {
      logger.log(alreadyDefinedError(nal, others.get(name).location()));
    }
  }

  private static void detectDuplicateFieldNames(Logger logger, ModuleP moduleP) {
    new ModuleVisitorP() {
      @Override
      public void visitFields(List<ItemP> fields) {
        super.visitFields(fields);
        findDuplicateNames(logger, fields);
      }
    }.visitModule(moduleP);
  }

  private static void detectDuplicateParamNames(Logger logger, ModuleP moduleP) {
    new ModuleVisitorP() {
      @Override
      public void visitParams(List<ItemP> params) {
        super.visitParams(params);
        findDuplicateNames(logger, params);
      }
    }.visitModule(moduleP);
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
