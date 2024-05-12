package org.felher.magicvar
package demo

import com.raquo.laminar.api.L.*

class TreeComponent(
    val key: String,
    val t: Signal[Tree],
    val onTreeEvent: Observer[TreeEvent]
) extends Component:
  override def render =
    val expanded   = MagicVar(false)
    val numToggled = MagicVar(0)

    div(
      className("tree"),
      img(
        className("expander"),
        src <-- expanded.signal.map(if (_) "collapse.png" else "expand.png"),
        onClick --> (_ =>
          expanded.update(!_)
          numToggled.update(_ + 1)
        ),
        title(summon[Context].path.mkString("/"))
      ),
      input(
        size <-- t.map(_.value.length),
        controlled(
          value <-- t.map(_.value),
          onInput.mapToValue --> (value => onTreeEvent.onNext(TreeEvent.UpdateValue(key, value)))
        )
      ),
      span(className("toggle-info"), child.text <-- numToggled.signal.map(nt => s"[Toggled $nt times]")),
      img(src("trash.png"), onClick.mapTo(TreeEvent.Remove(key)) --> onTreeEvent),
      div(
        className <-- expanded.signal.map(if (_) "expanded" else "collapsed"),
        children <-- Component.split(t.map(_.children))(_.id)((key, sig) => TreeComponent(key, sig, onTreeEvent)),
        div(
          img(
            src("add.png"),
            onClick --> (_ =>
              onTreeEvent.onNext(TreeEvent.AddChild(key, Tree(Math.random().toString, "New Node", Nil)))
            )
          )
        )
      )
    )
