import java.io.FileNotFoundException

import akka.NotUsed
import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor.{Actor, OneForOneStrategy, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

import scala.concurrent.duration._

class BookReader extends Actor{
  import BookReader._
  def receive = {
    case Stream(title) =>
      implicit val materializer = ActorMaterializer()
      val file = scala.io.Source.fromFile(title).getLines().toList
      val source: Source[StreamLine, NotUsed] = Source(file).map(x => StreamLine(title, x))
      val slowSource = source.throttle(1, 1.second)
      val sink = Sink.actorRef(sender(), StreamEnd(title))
      slowSource.runWith(sink)(materializer)
  }
}

object BookReader{
  def props: Props = Props[BookReader]
  case class StreamLine(title: String, line: String)
  case class StreamEnd(title: String)
}
