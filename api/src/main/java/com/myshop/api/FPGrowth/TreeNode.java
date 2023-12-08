package com.myshop.api.FPGrowth;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
  String item;
  int count;
  TreeNode parent;
  TreeNode nodeLink;
  List<TreeNode> children;

  public TreeNode(String item) {
    this.item = item;
    this.count = 1;
    this.parent = null;
    this.nodeLink = null;
    this.children = new ArrayList<>();
  }
}
