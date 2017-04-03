package helpers

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.specs2.mutable.After

abstract class AkkaSpec extends TestKit(ActorSystem()) with ImplicitSender with After {
  override def after: Any = system.terminate()
}
