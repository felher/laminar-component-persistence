package org.felher.magicvar

import com.raquo.laminar.api.L.*
import io.circe.*
import org.scalajs.dom

object Runtime:
  private var data: Map[Context, RuntimeData] = Map.empty

  def allocateChildContext(context: Context, name: String): Context =
    val rd             = data.getOrElse(context, RuntimeData.empty)
    val (index, newRd) = rd.reserveIndex(name)
    data = data.updated(context, newRd)
    context / name / index.toString

  def registerVar[A](
      v: Var[A],
      componentCtx: Context,
      varCtx: Context,
      enc: Encoder[A]
  ): Unit =
    val key = MagicVar.storageKey(varCtx)

    val d = data.getOrElse(componentCtx, RuntimeData.empty)
    data = data.updated(
      componentCtx,
      d.addWriteback(
        v.signal.map(v => () => dom.window.localStorage.setItem(key, enc(v).noSpaces))
      )
    )

  def destroyComponent(componentCtx: Context): Unit =
    val d = data.getOrElse(componentCtx, RuntimeData.empty)
    d.allOfName(componentCtx, "var")
      .foreach(ctx =>
        val key = MagicVar.storageKey(ctx)
        dom.window.localStorage.removeItem(key)
      )
    data = data - componentCtx

  def writebacks(ctx: Context): List[Signal[() => Unit]] =
    data.getOrElse(ctx, RuntimeData.empty).writebacks
