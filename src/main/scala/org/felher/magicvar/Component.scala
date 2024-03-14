package org.felher.magicvar

import com.raquo.laminar.api.L.*

trait Component:
  def render: Context ?=> HtmlElement

  def renderAsRoot: HtmlElement =
    given ctx: Context = Context(Nil)
    this

  def name: String = this.getClass.getName

object Component:
  implicit given (using ctx: Context): Conversion[Component, HtmlElement] =
    (c: Component) =>
      val childCtx = Runtime.allocateChildContext(ctx, c.name)
      c.render(using childCtx)
        .amend(
          Runtime
            .writebacks(childCtx)
            .map(wb => wb --> (wb => wb()))
        )
        .amend(
          onUnmountCallback(_ => Runtime.destroyComponent(childCtx))
        )

  def split[A](as: Signal[List[A]])(key: A => String)(f: (String, Signal[A]) => Component)(using
      ctx: Context
  ): Signal[List[HtmlElement]] =
    val splitCtx = Runtime.allocateChildContext(ctx, "split")
    as.split(key)((key, _, sig) =>
      given ctx: Context = Runtime.allocateChildContext(splitCtx, key)
      f(key, sig): HtmlElement
    )
