package de.scala_bs.wille.akka.deployByConfigExample


// Messages


case object Run
case class Key(key: String)
case class Value(value: String)


// Actors


import scala.collection.mutable.ListBuffer
import akka.actor.{Actor, Props}
import akka.routing.FromConfig


class Master(timeout: Int = 0) extends Actor {
  
  override def preStart() {
    self ! Run
  }
  
  val results = ListBuffer[String]()
  
  def receive = {
    case Run =>
      val viktor = context.actorOf(FromConfig.props(Props[Lookup]), name = "lookup-viktor")
      val martin = context.actorOf(FromConfig.props(Props[Lookup]), name = "lookup-martin")
      
      Thread sleep timeout
      
      println(s"Send Key(Viktor)")
      viktor ! Key("Viktor")
      
      println(s"Send Key(Martin)")
      martin ! Key("Martin")
    case Value(value) =>
      println(s"Received Value($value)")
      
      results += value
      if(results.length == 2) {
        println(results mkString ", ")
        context.system.shutdown()
      }
  }
}


class Lookup extends Actor {
  
  val data = Map("Martin" -> "Odersky", "Viktor" -> "Klang")
  
  def receive = {
    case Key(key) =>
      println(s"Received Key($key)")
      
      val value = data.getOrElse(key, "")
      println(s"Send Value($value)")
      sender() ! Value(value)
      
      context.stop(self)
  }
}


// Main classes


import com.typesafe.config.ConfigFactory
import akka.actor.{ActorSystem}


// activator "run-main de.scala_bs.wille.akka.deployByConfigExample.Local"
object Local {
  
  def main(args: Array[String]) {
    startMaster()
  }
  
  def startMaster() {
    val system = ActorSystem("Master", ConfigFactory.load("local"))
    system.actorOf(Props(classOf[Master], 0), "master")
  }
}


// activator "run-main de.scala_bs.wille.akka.deployByConfigExample.Remote Worker"
// activator "run-main de.scala_bs.wille.akka.deployByConfigExample.Remote Master"
object Remote {
  
  def main(args: Array[String]) {
    if(args.isEmpty || args.head == "Master" || args.head.head == 'M') startMaster()
    if(args.isEmpty || args.head == "Worker" || args.head.head == 'W') startWorker()
  }
  
  def startMaster() {
    val system = ActorSystem("Master", ConfigFactory.load("remote-master"))
    system.actorOf(Props(classOf[Master], 0), "master")
  }
  
  def startWorker() {
    ActorSystem("Worker", ConfigFactory.load("remote-worker"))
  }
}


import akka.cluster.Cluster


// activator "run-main de.scala_bs.wille.akka.deployByConfigExample.Clustering Worker 13371"
// activator "run-main de.scala_bs.wille.akka.deployByConfigExample.Clustering Worker 13372"
// activator "run-main de.scala_bs.wille.akka.deployByConfigExample.Clustering Master"
object Clustering {
  
  def main(args: Array[String]) {
    if(args.head == "Master" || args.head.head == 'M') startMaster()
    if(args.head == "Worker" || args.head.head == 'W') startWorker(args(1))
  }
  
  def startMaster() {
    val system = ActorSystem("Cluster", ConfigFactory.load("cluster-master"))
    
    Cluster(system) registerOnMemberUp {
      system.actorOf(Props(classOf[Master], 1000), "master")
    }
  }
  
  def startWorker(port: String) {
    val config = ConfigFactory.parseString("akka.remote.netty.tcp.port="+port).withFallback(ConfigFactory.load("cluster-worker"))
    val system = ActorSystem("Cluster", config)
    system.actorOf(Props[Lookup], "lookup")
  }
}