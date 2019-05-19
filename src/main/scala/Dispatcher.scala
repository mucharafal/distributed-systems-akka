import java.io.FileNotFoundException

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume}
import akka.actor.{Actor, ActorSystem, OneForOneStrategy, Props}

import scala.concurrent.duration._

class Dispatcher extends Actor {
  val orderSaver = context.actorOf(OrderSaver.props)
  val reader = context.actorOf(BookReader.props)
  def receive = {
    case Search(title) =>
      val databasesSearcher = context.actorOf(DatabasesSearcher.props)
      databasesSearcher.tell(Search(title), sender())
    case SaveOrder(title) =>
      orderSaver.tell(SaveOrder(title), sender())
    case Stream(title) =>
      reader.tell(Stream(title), sender())
  }

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 1 minute) {
      case _: FileNotFoundException    => Restart
      case _: Exception                => Resume
    }
}

object Dispatcher {
  def props: Props = Props[Dispatcher]
}
