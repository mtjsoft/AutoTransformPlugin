package cn.mtjsoft.www.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class BuryPointPlugin implements Plugin<Project> {

    static final String mappingSavePath = "/outputs/mapping/methodHook_mapping.txt"

    @Override
    void apply(Project project) {

        project.getTasks().getByName("preBuild").doFirst {
            deleteMMaping(project)
        }

        project.extensions.create("methodhook", MethodHookConfig)

        def android = project.extensions.android
        boolean islib = true
        if (android instanceof AppExtension) {
            islib = false
        }

        android.registerTransform(new MethodHookTransform(project, islib))
    }

    static void deleteMMaping(Project project) {
        createMappingPrinter(project).delete()
    }


    static MappingPrinter createMappingPrinter(Project project){
        return new MappingPrinter(new File(project.getBuildDir(), mappingSavePath))
    }
}