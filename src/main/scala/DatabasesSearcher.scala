import java.io.FileNotFoundException

import akka.actor.SupervisorStrategy.{Escalate, Restart, Stop}
import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, Props, SupervisorStrategy}
import akka.dispatch.sysmsg.Failed
import akka.pattern.{BackoffOpts, BackoffSupervisor}

import scala.concurrent.duration._

class DatabasesSearcher extends Actor {
  import DatabasesSearcher._
  var receivedResponses = 0
  var searchedTitle = ""
  var client: ActorRef = null
  def receive = {
    case Search(title) =>
      searchedTitle = title
      val system = context.system
      val s1 = system.actorOf(DatabaseSearcher.props)
      val s2 = system.actorOf(DatabaseSearcher.props)
      client = sender()
      s1.tell(SearchIn(database1Path, title), self)
      s2.tell(SearchIn(database1Path, title), self)
    case Result(price) =>
      client.tell(PriceOf(searchedTitle, price), null)
      context.stop(self)
    case NotFound =>
      receivedResponses += 1
      if (receivedResponses == 2) {
        client.tell(NotFound(searchedTitle), null)
        context.stop(self)
      }
  }
  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 1 minute) {
      case _: FileNotFoundException    => Stop
      case _: Exception                => Escalate
    }

}

object DatabasesSearcher {
  val database1Path = "database1"
  val database2Path = "database2"

  def props: Props = Props[DatabasesSearcher]

  case class SearchIn(path: String, title: String)
  case class Result(price: Int)
  case class PriceOf(title: String, price: Int)
  case class NotFound(title: String)
  case object NotFound
}
