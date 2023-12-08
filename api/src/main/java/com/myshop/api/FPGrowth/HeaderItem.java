package com.myshop.api.FPGrowth;

public class HeaderItem {
  String itemName;
  int itemCount;
  TreeNode head;

  public HeaderItem(String itemName) {
    this.itemName = itemName;
    this.itemCount = 0;
    this.head = null;
  }
}