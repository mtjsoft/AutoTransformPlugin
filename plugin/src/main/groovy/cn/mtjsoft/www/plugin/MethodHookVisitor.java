package cn.mtjsoft.www.plugin;

import org.gradle.api.Project;
import org.objectweb.asm.*;

public class MethodHookVisitor extends ClassVisitor {

    private String className = null;

    private MethodHookConfig config;
    private Project project;

    private MappingPrinter mappingPrinter;
    private boolean isIgnoreMethodHook = false;

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
        className = name.replace("/", ".");
        isIgnoreMethodHook = className.contains("cn.mtjsoft.www.plugin");
        if (config.isMapping() && !isIgnoreMethodHook) {
            mappingPrinter.log("[CLASSNAME]" + className + name);
        }
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    public void visitEnd() {
        if (!isIgnoreMethodHook) {
            // 插入一个字符串变量
            FieldVisitor fv = cv.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL, "phone", "Ljava/lang/String;", null, "155");
            fv.visitEnd();
        }
        super.visitEnd();
    }
}
