package io.github.ujihisa.AkkaCalculator

import akka.actor.{ActorSystem, ActorRef}
import akka.routing.ConsistentHashingRouter.ConsistentHashMapping
import akka.routing.ConsistentHashingRouter

object AkkaCalculator {
  // Actor Messages
  case class Eval(e: Expr, inst: Instruction)

  sealed trait Instruction
  case object PrintInst extends Instruction
  case class WaitPairInst(label: String, next: Instruction) extends Instruction

  sealed trait Expr
  case class ImmediateExpr(a: Long) extends Expr
  case class PlusExpr(a: Expr, b: Expr) extends Expr
  case class MinusExpr(a: Expr, b: Expr) extends Expr

  private def hashMapping: ConsistentHashMapping = {
    case Eval(ImmediateExpr(_), WaitPairInst(label, _)) => label
    case _ => util.Random.nextInt
  }
  def main(args: Array[String]) {
    val system = ActorSystem("akka-calculator")

    try {
      val calculatorRouter =
        system.actorOf(Calculator.props().withRouter(ConsistentHashingRouter(nrOfInstances = 10, hashMapping = hashMapping)))

      calculatorRouter ! Eval(
        PlusExpr(
          PlusExpr(ImmediateExpr(1), ImmediateExpr(2)),
          MinusExpr(ImmediateExpr(3), PlusExpr(ImmediateExpr(4), ImmediateExpr(5)))),
        PrintInst)

    } finally {
      Thread.sleep(1000) // ugh
      system.shutdown
      println('ok)
    }
  }
}
