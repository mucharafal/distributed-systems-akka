import DatabasesSearcher.{NotFound, Result, SearchIn}
import akka.actor.{Actor, Props}

import scala.io.Source

class DatabaseSearcher extends Actor {
 def receive = {
   case SearchIn(path, title) =>
     for (line <- Source.fromFile(path).getLines().map(x => x.split(" "))) {
       if (line(0).equals(title)) {
         sender().tell(Result(line(1).toInt), self)
         context.stop(self)
       }
     }
     sender().tell(NotFound, self)
     context.stop(self)
 }
}

object DatabaseSearcher {
  def props: Props = Props[DatabaseSearcher]
}
