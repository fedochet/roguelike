import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.entity.Entities
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.settings.GameSettings
import javafx.application.Application
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle


class BasicGameApp : GameApplication() {
    override fun initSettings(settings: GameSettings) {
        settings.width = 600
        settings.height = 600
        settings.title = "Basic Game App"
        settings.version = "0.1"
    }

    lateinit var player: Entity

    override fun initGame() {
        player = Entities.builder()
                .at(300.0, 300.0)
                .viewFromNode(Rectangle(25.0, 25.0, Color.BLUE))
                .buildAndAttach(gameWorld)
    }

    override fun initInput() {
        val input = input
        val yStep = 25.0
        val xStep = 25.0

        input.addAction(
                holdoutUserAction("Move right", { player.translateX(xStep) }),
                KeyCode.RIGHT
        )

        input.addAction(holdoutUserAction("Move Left", { player.translateX(-xStep) }), KeyCode.LEFT)

        input.addAction(holdoutUserAction("Move Up", { player.translateY(-yStep) }), KeyCode.UP)

        input.addAction(holdoutUserAction("Move Down", { player.translateY(yStep) }), KeyCode.DOWN)
    }
}

fun holdoutUserAction(name: String, action: () -> Unit, holdout: Int = 20, step: Int = 5): UserAction {
    return object : UserAction(name) {
        var count = 0
        override fun onActionBegin() {
            action()
        }

        override fun onAction() {
            count += 1
            if (count > holdout && count % step == 0) {
                action()
            }
        }

        override fun onActionEnd() {
            count = 0;
        }
    }
}

fun main(args: Array<String>) {
    Application.launch(BasicGameApp::class.java, *args)
}
