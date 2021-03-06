package cn.mtjsoft.www.plugin


import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.google.common.collect.Sets
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

import java.text.SimpleDateFormat
import java.util.regex.Pattern

class MethodHookTransform extends Transform {
    Project project
    boolean islib
    MethodHookConfig mtc

    MethodHookTransform(Project project, boolean islib) {
        this.project = project
        this.islib = islib
    }

    @Override
    String getName() {
        return "MethodHook"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        if (islib) {
            Sets.immutableEnumSet(
                    QualifiedContent.Scope.PROJECT,
            )
        } else {
            TransformManager.SCOPE_FULL_PROJECT
        }
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider,
                   boolean isIncremental) throws IOException, TransformException, InterruptedException {
        println '┌------------------------┐'
        println '|      Method Hook       |'
        println '└------------------------┘'
        mtc = project.methodhook
        println("[Config]:" + mtc.toString())
        if (!mtc.enable) {
            return
        }
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINESE)
        println("startTime: " + mSimpleDateFormat.format(new Date()))
        MethodHookInjectImpl impl = new MethodHookInjectImpl(project)
        impl.setConfig(mtc)

        inputs.each { TransformInput input ->
            eachClass(input, impl, outputProvider)
            eachJar(context, input, impl, outputProvider)
        }
        println("endTime: " + mSimpleDateFormat.format(new Date()))
        println '┌------------------------┐'
        println '|      Method Hook  √    |'
        println '└------------------------┘'
    }

    private List eachJar(Context context, TransformInput input, MethodHookInjectImpl impl, outputProvider) {
        input.jarInputs.each { JarInput jarInput ->
            def jarName = jarInput.name

            def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
            def file = jarInput.file

            if (impl.config.impl != null && "" != impl.config.impl && jarName.contains(":pluginlib")) {
                file = impl.injectSelfJar(jarInput.file, context.getTemporaryDir())
            } else {
                mtc.jarRegexs.each { def regexStr ->
                    def isM = Pattern.matches(regexStr, jarName)
                    if (isM) {
                        if (mtc.log) {
                            println("[jar][regex]:$regexStr $jarName is injected.")
                        }
                        file = impl.injectJar(jarInput.file, context.getTemporaryDir())
                        if (mtc.replaceJar && file != null) {
                            jarInput.file.delete()
                            FileUtils.copyFile(file, jarInput.file)
                        }
                    } else if (mtc.log) {
                        println("[jar][regex]:$regexStr $jarName not mc.")
                    }
                }
            }

            if (jarName.endsWith(".jar")) {
                jarName = jarName.substring(0, jarName.length() - 4)
            }
            def dest = outputProvider.getContentLocation(jarName + md5Name,
                    jarInput.contentTypes, jarInput.scopes, Format.JAR)

            FileUtils.copyFile(file, dest)
        }
    }

    private List eachClass(TransformInput input, MethodHookInjectImpl impl, outputProvider) {
        input.directoryInputs.each { DirectoryInput directoryInput ->

            if (directoryInput.file.isDirectory()) {
                directoryInput.file.eachFileRecurse { File file ->

                    if (impl.isInject(file)) {

                        byte[] code = impl.injectClass(file.bytes)

                        FileOutputStream fos = new FileOutputStream(
                                file.parentFile.absolutePath + File.separator + file.name)
                        fos.write(code)
                        fos.close()

//                        println "[class]" + file.name + ' is injected.'
                    }
                }
            } else {
                println "[directory]" + directoryInput.file.name + ' not inject.'
            }


            def dest = outputProvider.getContentLocation(directoryInput.name,
                    directoryInput.contentTypes, directoryInput.scopes,
                    Format.DIRECTORY)
            FileUtils.copyDirectory(directoryInput.file, dest)
        }
    }
}