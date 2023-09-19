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
import scalafx.scene.input._ // KeyEvent, KeyCode

object SnakeFx extends JFXApp3 {

  val (w, h, cellWidth) = (600.0, 600.0, 25.0)
  object SnakeDirection extends Enumeration {
    val Up = (x: Double, y: Double) => (x, y - cellWidth)
    val Down = (x: Double, y: Double) => (x, y + cellWidth)
    val Left = (x: Double, y: Double) => (x - cellWidth, y)
    val Right = (x: Double, y: Double) => (x + cellWidth, y)
  }

  var snake: Array[Rectangle] = {
    Array(0, 1, 2).map(i => {
      val bodyPart =
        Rectangle(w / 2 + i * cellWidth, h / 2, cellWidth, cellWidth)
      bodyPart.fill = SeaGreen
      bodyPart
    })
  }

  def updateSnake(dir: (Double, Double) => (Double, Double)): Unit = {
    // println("tick")
    for (i <- (1 until snake.length).reverse) {
      snake(i).x = snake(i - 1).x()
      snake(i).y = snake(i - 1).y()
    }
    val (newX, newY) = dir(snake(0).x(), snake(0).y())
    snake(0).x = newX
    snake(0).y = newY
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

        var currDirection = SnakeDirection.Left
        onKeyPressed = (e: KeyEvent) => {
          if (e.code == KeyCode.Up)
            currDirection =
              if (currDirection != SnakeDirection.Down) SnakeDirection.Up
              else currDirection
          else if (e.code == KeyCode.Down)
            currDirection =
              if (currDirection != SnakeDirection.Up) SnakeDirection.Down
              else currDirection
          else if (e.code == KeyCode.Left)
            currDirection =
              if (currDirection != SnakeDirection.Right) SnakeDirection.Left
              else currDirection
          else if (e.code == KeyCode.Right)
            currDirection =
              if (currDirection != SnakeDirection.Left) SnakeDirection.Right
              else currDirection
        }

        var lastTimestamp = System.nanoTime()
        val frameNumber: Int = 0
        AnimationTimer(t => {
          val dt = (t - lastTimestamp) / 1e8 // change in time in seconds
          if (dt >= 1) {
            updateSnake(currDirection)
            frameNumber + 1
            lastTimestamp = t
          }
        }).start
      }
    }

  }
}
