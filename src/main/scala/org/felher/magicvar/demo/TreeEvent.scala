package org.felher.magicvar.demo

enum TreeEvent derives CanEqual:
  case Remove(id: String)
  case AddChild(id: String, child: Tree)
  case UpdateValue(id: String, value: String)
