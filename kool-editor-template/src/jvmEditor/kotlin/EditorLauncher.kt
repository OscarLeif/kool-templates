import de.fabmax.kool.KoolApplication
import de.fabmax.kool.KoolConfigJvm
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.ProjectPaths

/**
 * Editor launcher main function. It is recommended to run this by executing the gradle task ":runEditor". However,
 * it is also possible to manually start this from your IDE or the shell (make sure the working directory is set
 * to the .editor directory).
 */
fun main() {
    // by default editor working dir is in $projectDir/.editor -> project root path is the parent directory
    val paths = ProjectPaths("..", gradleRootDir = "..", gradleBuildTask = "jvmMainClasses")
    KoolApplication(
        KoolConfigJvm(
            windowTitle = "Kool Editor",
            assetPath = paths.assetsBasePath,
            showWindowOnStart = false
        )
    ) { ctx ->
        KoolEditor(ctx, paths)
    }
}
