package template

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolContext
import de.fabmax.kool.loadTexture2d
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.gltf.GltfLoadConfig
import de.fabmax.kool.modules.gltf.GltfMaterialConfig
import de.fabmax.kool.modules.gltf.loadGltfModel
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.pipeline.ao.AoPipeline
import de.fabmax.kool.scene.addTextureMesh
import de.fabmax.kool.scene.defaultOrbitCamera
import de.fabmax.kool.scene.scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.FrontendScope
import de.fabmax.kool.util.SimpleShadowMap
import de.fabmax.kool.util.Time
import kotlinx.coroutines.launch

/**
 * Main application entry. This demo creates a small example scene, which you probably want to replace by your actual
 * game / application content.
 */
fun launchApp(ctx: KoolContext) = FrontendScope.launch {
    ctx.scenes += scene {
        defaultOrbitCamera()

        // Light setup
        lighting.singleSpotLight {
            setup(Vec3f(5f, 6.25f, 7.5f), Vec3f(-1f, -1.25f, -1.5f), 45f.deg)
            setColor(Color.WHITE, 300f)
        }
        val shadowMap = SimpleShadowMap(this, lighting.lights[0])
        val aoPipeline = AoPipeline.createForward(this)

        // Add a textured ground plane
        val texture = Assets.loadTexture2d("kool-test-tex.png").getOrThrow()
        addTextureMesh {
            generate {
                grid { }
            }
            shader = KslPbrShader {
                color { textureColor(texture) }
                lighting { addShadowMap(shadowMap) }
                enableSsao(aoPipeline.aoMap)
            }
        }

        // Load a glTF 2.0 model
        val materialCfg = GltfMaterialConfig(
            shadowMaps = listOf(shadowMap),
            scrSpcAmbientOcclusionMap = aoPipeline.aoMap,

        )
        val modelCfg = GltfLoadConfig(materialConfig = materialCfg)
        val model = Assets.loadGltfModel("BoxAnimated.gltf", modelCfg).getOrThrow()

        model.transform.translate(0f, 0.5f, 0f)
        if (model.animations.isNotEmpty()) {
            model.enableAnimation(0)
            model.onUpdate {
                model.applyAnimation(Time.deltaT)
            }
        }

        // Add loaded model to scene
        addNode(model)
    }
}