package cn.mtjsoft.www.plugin.utils;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Random;
import java.util.UUID;

import cn.mtjsoft.www.plugin.MethodHookConfig;
import kotlin.Pair;

public class RandomFieldAndMethodUtils {

    private ClassVisitor cv;
    private String className;
    private MethodHookConfig config;

    private final Random random = new Random();

    private final byte[] bytes = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x11, 0x12};

    private int maxRandomNum = 1;

    public RandomFieldAndMethodUtils(ClassVisitor classVisitor, String className, MethodHookConfig config) {
        this.cv = classVisitor;
        this.className = className;
        this.config = config;
    }

    public void randomFieldAndMethod() {
        if (config.getRandomCount() > 0) {
            maxRandomNum = config.getRandomCount();
        }
        for (int i = 0; i < maxRandomNum; i++) {
            Pair<String, Object> pair = randomPair();
            addFieldAndMethod(i, randomName(), pair.getFirst(), pair.getSecond());
        }
    }

    private void addFieldAndMethod(int number, String fieldName, String fieldType, Object value) {
        FieldVisitor fv = cv.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL, fieldName + number, fieldType, null, null);
        fv.visitEnd();
        // public get
        MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "get" + fieldName + number, "()" + fieldType, null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, className, fieldName + number, fieldType);
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
//        mv = cv.visitMethod(Opcodes.ACC_PUBLIC,  fieldName + "_" + number, "()V", null, null);
//        mv.visitEnd();
    }

    private Pair<String, Object> randomPair() {
        Pair<String, Object> pair;
        int index = random.nextInt(7);
        switch (index) {
            case 0:
                pair = new Pair<>("Ljava/lang/Object;", randomName());
                break;
            case 1:
                pair = new Pair<>("Ljava/lang/Boolean;", random.nextBoolean());
                break;
            case 2:
                pair = new Pair<>("Ljava/lang/Byte;", bytes[random.nextInt(bytes.length)]);
                break;
            case 3:
                pair = new Pair<>("Ljava/lang/Long;", random.nextLong());
                break;
            case 4:
                pair = new Pair<>("Ljava/lang/Float;", random.nextFloat());
                break;
            case 5:
                pair = new Pair<>("Ljava/lang/Double;", random.nextDouble());
                break;
            default:
                pair = new Pair<>("Ljava/lang/String;", randomName());
                break;
        }
        return pair;
    }

    private String randomName() {
        return "df_" + UUID.randomUUID().toString().replace("-", "_").trim();
    }
}
