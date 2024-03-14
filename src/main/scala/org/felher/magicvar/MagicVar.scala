package org.felher.magicvar

import com.raquo.laminar.api.L.*
import io.circe.*
import org.scalajs.dom

object MagicVar:
  def apply[A](value: A)(using
      componentContext: Context,
      enc: Encoder[A],
      dec: Decoder[A]
  ): Var[A] =
    val varCtx = Runtime.allocateChildContext(componentContext, "var")
    val key    = storageKey(varCtx)
    val v      = dom.window.localStorage.getItem(key) match
      case null => Var(value)
      case s    =>
        io.circe.parser.decode[A](s) match
          case Left(_)  => Var(value)
          case Right(a) => Var(a)

    Runtime.registerVar(v, componentContext, varCtx, enc)
    v

  def storageKey(ctx: Context): String =
    "magic-var-" + io.circe.Encoder[List[String]].apply(ctx.path).noSpaces
