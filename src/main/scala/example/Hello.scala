package example

import Array._
import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.scene._ // Node, Scene
import scalafx.scene.paint.Color._ // SeaGreen, AntiqueWhite
import scalafx.scene.shape.Rectangle
import scalafx.scene.layout.GridPane
import scalafx.scene.text.Text
import scalafx.animation.AnimationTimer

object SnakeFx extends JFXApp3 {
  val (w, h, cellWidth) = (600.0, 600.0, 25.0)
  var snake: Array[Rectangle] = {
    Array(0, 1, 2).map(i => {
      val bodyPart =
        Rectangle(w / 2 + i * cellWidth, h / 2, cellWidth, cellWidth)
      bodyPart.fill = SeaGreen
      bodyPart

    })
  }

  def updateSnake() = {
    println("tick")
    for (i <- 0 until snake.length) {
      snake(i).layoutX = snake(i).layoutX.value - cellWidth
    }
  }

  val board = {
    snake ++
      Array(new Text(30, 60, "Hello again")) ++
      Array(new Text(30, 90, "Hello again!!!!"))

  }

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title.value = "Snake"
      width = w
      height = h
      scene = new Scene {
        fill = AntiqueWhite
        content = snake
      }
    }

    var lastTimestamp = System.nanoTime()
    val frameNumber: Int = 0
    AnimationTimer(t => {
      val dt = (t - lastTimestamp) / 1e9 // change in time in seconds
      if (dt >= 1) {
        updateSnake()
        frameNumber + 1
        lastTimestamp = t
      }
    }).start
  }
}
