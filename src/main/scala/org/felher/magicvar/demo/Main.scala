package org.felher.magicvar
package demo

import com.raquo.laminar.api.L.*
import org.scalajs.dom

@main def hello(): Unit =
  render(
    dom.document.getElementById("app"),
    div(
      Main.renderAsRoot
    )
  )

  ()

object Main extends Component:
  override def render =
    val tree = MagicVar(
      Tree(
        "1",
        "root",
        List(
          Tree("2", "child1", Nil),
          Tree("3", "child2", Nil)
        )
      )
    )

    TreeComponent("1", tree.signal, Observer[TreeEvent](te => tree.update(_.handle(te))))
