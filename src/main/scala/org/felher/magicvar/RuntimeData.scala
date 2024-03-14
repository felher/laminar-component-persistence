package org.felher.magicvar

import com.raquo.laminar.api.L.*

final case class RuntimeData(
    nextIndicies: Map[String, Int],
    writebacks: List[Signal[() => Unit]]
):
  def reserveIndex(name: String): (Int, RuntimeData) =
    val nextIndex = nextIndicies.getOrElse(name, 0)
    (nextIndex, copy(nextIndicies = nextIndicies.updated(name, nextIndex + 1)))

  def nextVarIndex: Int = writebacks.length

  def addWriteback(wb: Signal[() => Unit]): RuntimeData =
    copy(writebacks = wb :: writebacks)

  def allOfName(base: Context, name: String): List[Context] =
    nextIndicies
      .get(name)
      .map(n => (0 until n).toList.map(i => base / name / i))
      .getOrElse(Nil)

object RuntimeData:
  val empty = RuntimeData(Map.empty, Nil)
