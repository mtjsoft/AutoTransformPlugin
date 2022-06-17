package cn.mtjsoft.www.plugin;

import org.gradle.api.Project;
import org.objectweb.asm.*;

import java.util.regex.Pattern;

import cn.mtjsoft.www.plugin.utils.RandomFieldAndMethodUtils;

public class MethodHookVisitor extends ClassVisitor {

    private String className = null;

    private MethodHookConfig config;
    private Project project;

    private MappingPrinter mappingPrinter;
    private boolean isIgnoreMethodHook = true;

    MethodHookVisitor(ClassVisitor classVisitor, MethodHookConfig config, Project project) {
        super(Opcodes.ASM5, classVisitor);
        this.config = config;
        this.project = project;
        if (config.isMapping()) {
            mappingPrinter = BuryPointPlugin.createMappingPrinter(project);
        }
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        className = name;
        if (config.getClassRegexs() == null || config.getClassRegexs().isEmpty()) {
            isIgnoreMethodHook = true;
        } else {
            for (String methodRegex : config.getClassRegexs()) {
                // 有一个正则命中，就不忽略
                boolean isM = Pattern.matches(methodRegex, name);
                if (isM) {
                    isIgnoreMethodHook = false;
                }
            }
        }
//        isIgnoreMethodHook =  !(className.equals("com/daofeng/peiwan/wxapi/QQEntryActivity") || className.equals("com/daofeng/peiwan/mvp/accusation/ui/AccusationHomePbjyActivity"));
        if (config.isMapping() && !isIgnoreMethodHook) {
            mappingPrinter.log("[CLASSNAME]" + className);
        }
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    public void visitEnd() {
        if (!isIgnoreMethodHook) {
            new RandomFieldAndMethodUtils(cv, className, config).randomFieldAndMethod();
        }
        super.visitEnd();
    }
}
