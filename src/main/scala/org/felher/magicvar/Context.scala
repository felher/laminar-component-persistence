package org.felher.magicvar

final case class Context(path: List[String]):
  def / (segment: String): Context = Context(path :+ segment)
  def / (index: Int): Context = Context(path :+ index.toString)
