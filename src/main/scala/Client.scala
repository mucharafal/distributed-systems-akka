import BookReader.{StreamEnd, StreamLine}
import Client.Start
import DatabasesSearcher.{NotFound, PriceOf}
import akka.actor.{Actor, ActorSystem, Props}

class Client extends Actor {

  def receive = {
    case StreamLine(title, line) =>
      println("Line from book: " + title + ": " + line)
    case StreamEnd(title) =>
      println("End of stream " + title)
    case NotFound(title) =>
      println("Price of " + title + " is not found")
    case PriceOf(title, price) =>
      println("Price of " + title + " is " + price)
    case Start =>
      val parent = this
      val thread = new Thread {
        override def run: Unit = {
          parent.run()
        }
      }
      thread.start
    case something => println(something)
  }

  def run(): Unit = {
    val system = context.system
    val dispatcher = system.actorOf(Dispatcher.props)
    while(true) {
      val input = readLine()
      val processedInput = input.split(" ")
      if(processedInput.length > 1) {
        val title = processedInput(1)
        processedInput(0) match {
          case "search" => dispatcher.tell(Search(title), self)
          case "order" => dispatcher.tell(SaveOrder(title), self)
          case "stream" => dispatcher.tell(Stream(title), self)
          case _ => println("Not recognized")
        }
      } else {
        println("Incorrect input")
      }
    }
  }
}

object Client {
  case object Start
 def props: Props = Props[Client]
}
