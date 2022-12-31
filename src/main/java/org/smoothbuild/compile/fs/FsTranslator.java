package org.smoothbuild.compile.fs;

import static org.smoothbuild.compile.fs.lang.define.LoadInternalMod.loadInternalModule;
import static org.smoothbuild.compile.fs.lang.define.ScopeS.override;
import static org.smoothbuild.compile.fs.ps.PsTranslator.translatePs;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.compile.fs.fp.FpTranslator;
import org.smoothbuild.compile.fs.lang.define.ModuleResources;
import org.smoothbuild.compile.fs.lang.define.ScopeS;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.out.log.MaybeProcessor;

/**
 * F->S Translator.
 * Translates source code F-ile to S-objects.
 */
public class FsTranslator {
  private final FpTranslator fpTranslator;

  @Inject
  public FsTranslator(FpTranslator fpTranslator) {
    this.fpTranslator = fpTranslator;
  }

  public Maybe<ScopeS> translateFs(List<ModuleResources> modules) {
    return new Translator(fpTranslator, modules)
        .process();
  }

  private static class Translator extends MaybeProcessor<ScopeS> {
    private final FpTranslator fpTranslator;
    private final List<ModuleResources> modules;

    public Translator(FpTranslator fpTranslator, List<ModuleResources> modules) {
      this.fpTranslator = fpTranslator;
      this.modules = modules;
    }

    @Override
    protected ScopeS processImpl() throws FailedException {
      var internalModule = loadInternalModule();
      var current = internalModule.members();
      for (ModuleResources moduleResources : modules) {
        var moduleP = addLogsAndGetValue(fpTranslator.translateFp(moduleResources));
        var moduleS = addLogsAndGetValue(translatePs(moduleP, current));
        current = override(moduleS.members(), current);
      }
      return current;
    }
  }
}
