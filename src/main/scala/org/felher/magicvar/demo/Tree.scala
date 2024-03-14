package org.felher.magicvar
package demo

final case class Tree(
    id: String,
    value: String,
    children: List[Tree]
) derives io.circe.Codec.AsObject:
  def update(
      target: String,
      modifier: Tree => Option[Tree]
  ): Option[Tree] =
    if id == target then modifier(this)
    else Some(this.copy(children = children.flatMap(_.update(target, modifier))))

  def handle(event: TreeEvent): Tree =
    import TreeEvent.*
    event match
      case TreeEvent.Remove(target)   =>
        update(target, _ => None).getOrElse(this)
      case AddChild(target, child)    =>
        update(target, t => Some(t.copy(children = t.children :+ child))).getOrElse(this)
      case UpdateValue(target, value) =>
        update(target, t => Some(t.copy(value = value))).getOrElse(this)
