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
import scalafx.scene.text.Font
import scalafx.scene.text.FontWeight
import scalafx.scene.text.FontPosture

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

  val fruit: Array[Rectangle] = {
    val (x, y) = randomCoordsInScene()
    val fruit = Rectangle(x, y, cellWidth, cellWidth)
    fruit.fill = IndianRed
    Array(fruit)
  }

  def randomCoordsInScene(): (Double, Double) = {
    val x = (math.random() * w / cellWidth).toInt * cellWidth
    val y = (math.random() * h / cellWidth).toInt * cellWidth
    (x, y)
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

  def extendSnakeTail(dir: (Double, Double) => (Double, Double)): Unit = {
    val newBodyPart =
      Rectangle(
        if (dir == SnakeDirection.Left) snake.last.x() + cellWidth
        else snake.last.x() - cellWidth,
        snake.last.y(),
        cellWidth,
        cellWidth
      )
    newBodyPart.fill = SeaGreen
    snake :+= newBodyPart
  }

  def checkCollisions(
      dir: (Double, Double) => (Double, Double)
  ): Int = {

    if (
      snake(0).x() < 0 || snake(0).x() >= w || snake(0).y() < 0 || snake(0)
        .y() >= h
    ) {
      println("wall collision")
      return -1
    }

    if (
      snake
        .slice(1, snake.length)
        .exists(bodyPart =>
          bodyPart.x() == snake(0).x() && bodyPart.y() == snake(0).y()
        )
    ) {
      println("self collision")
      return -1
    }

    if (
      snake(0).x() == fruit(0).x() &&
      snake(0).y() == fruit(0).y()
    ) {
      println("Eating fruit")
      extendSnakeTail(dir)

      val (x, y) = randomCoordsInScene()
      fruit(0).x = x
      fruit(0).y = y

      return 1
    }
    return 0
  }

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title.value = "Snake"
      width = w
      height = h
      scene = new Scene {
        fill = AntiqueWhite
        var gameOver = false
        var score = 0
        val gameOverModal = new Text {
          font = Font("Arial", FontWeight.ExtraBold, FontPosture.Regular, 48)
          text = "Game Over"
          fill = DimGray
          x = 70
          y = 70
          visible = false
        }
        val scoreModal = new Text {
          font = Font("Arial", FontWeight.ExtraBold, FontPosture.Regular, 20)
          text = "Score: " + score
          fill = SteelBlue
          x = 70
          y = h - 70
          visible = true
        }

        content = snake ++ fruit ++ Array(gameOverModal) ++ Array(scoreModal)

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
        val animator = AnimationTimer(t => {

          if (gameOver) {
            gameOverModal.visible = true
          } else {
            val dt = (t - lastTimestamp) / 1e8 // change in time in seconds
            if (dt >= 1) {
              updateSnake(currDirection)
              val result = checkCollisions(currDirection)
              if (result < 0) gameOver = true
              score += result
              frameNumber + 1
              lastTimestamp = t
            }
          }

          scoreModal.text = "Score: " + score
          content = snake ++ fruit ++ Array(gameOverModal) ++ Array(scoreModal)
        }).start
      }
    }

  }
}
