import java.io.FileNotFoundException

import akka.actor.SupervisorStrategy.{Restart, Resume, Stop}
import akka.actor.{Actor, OneForOneStrategy, Props}

import scala.concurrent.duration._
import Dispatcher._

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
      case _: FileNotFoundException    => Stop
      case _: Exception                => Resume
    }
}

object Dispatcher {
  def props: Props = Props[Dispatcher]  //.withDeploy(Deploy(scope = remote.RemoteScope(Address("akka.tcp", Main.bookstoreSystemName, "host", 3552))))

  final case class Search(title: String)
  final case class SaveOrder(title: String)
  final case class Stream(title: String)
}
