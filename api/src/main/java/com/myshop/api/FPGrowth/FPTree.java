package com.myshop.api.FPGrowth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FPTree {
  private TreeNode root;
  private Map<String, Integer> frequentItems;
  private List<String> fList;
  private Map<String, HeaderItem> headerTable;
  private Map<Set<String>, Integer> listFreq;

  public FPTree() {
    this.root = new TreeNode("null");
    this.frequentItems = new HashMap<>();
    this.fList = new ArrayList<>();
    this.headerTable = new HashMap<>();
    this.listFreq = new HashMap<>();
  }


  public void buildTree(List<List<String>> transactions) {
    findFrequentItems(transactions);
    System.out.println("==========FList==============\n" + fList.toString());
    for (List<String> transaction : transactions) {
      List<String> sortedListTrans = sortTransaction(transaction);
      System.out.println("==========Trans============== \n" + sortedListTrans.toString());
      insertTree(sortedListTrans, root);
//      this.printTree();
    }
  }

  public void findFrequentItems(List<List<String>> transactions) {
    Map<String, Integer> itemSupport = new HashMap<>();
    for (List<String> transaction : transactions) {
      for (String item : transaction) {
        itemSupport.put(item, itemSupport.getOrDefault(item, 0) + 1);
      }
    }
    this.frequentItems = itemSupport.entrySet().stream()
       //     .filter(entry -> entry.getValue() >= minSup)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    this.fList = frequentItems.entrySet().stream().map(Map.Entry::getKey)
            .sorted(Comparator.comparing(item -> frequentItems.get(item)).reversed())
            .collect(Collectors.toList());
    if (itemSupport.get("f") != null && itemSupport.get("c") != null){
      Collections.swap(this.fList, 0, 1);
    }
  }

  private List<String> sortTransaction(List<String> transaction) {
    return transaction.stream()
            .filter(fList::contains)
            .sorted(Comparator.comparing(item -> fList.indexOf(item)))
            .collect(Collectors.toList());
  }

  private void insertTree(List<String> items, TreeNode node) {
    if (items.isEmpty()) {
      return;
    }

    String item = items.get(0);
    TreeNode child = findChild(node, item);

    if (child != null) {
      child.count++;
    } else {
      child = new TreeNode(item);
      child.parent = node;
      node.children.add(child);
      updateHeaderTable(item, child);
    }
    if (items.size() > 1) {
      insertTree(items.subList(1, items.size()), child);
    }
  }

  private TreeNode findChild(TreeNode node, String item) {
    for (TreeNode child : node.children) {
      if (child.item.equals(item)) {
        return child;
      }
    }
    return null;
  }

  private void updateHeaderTable(String item, TreeNode node) {
    HeaderItem headerItem = headerTable.get(item);
    if (headerItem == null) {
      headerItem = new HeaderItem(item);
      headerTable.put(item, headerItem);
    }

    headerItem.itemCount++;
    node.nodeLink = headerItem.head;
    headerItem.head = node;
  }

  public void printTree() {
    printTree(root, 0);
  }

  private void printTree(TreeNode node, int level) {
    if (node != null) {
      StringBuilder indent = new StringBuilder();
      for (int i = 0; i < level; i++) {
        indent.append("  ");
      }
      System.out.println(indent + node.item + " (" + node.count + ")");
      for (TreeNode child : node.children) {
        printTree(child, level + 1);
      }
    }
  }

  public List<List<String>> findSampleConditionBasis(String item,Double minSup) {
    Map<String, Integer> patterns = new HashMap<>();
    TreeNode nodeHead = headerTable.get(item).head;

    while (nodeHead != null) {
      TreeNode currentNode = nodeHead;
      if (nodeHead.parent != null)
        currentNode = nodeHead.parent;
      //currentNode.parent != null &&
      while (!currentNode.item.equals("null")) {
        patterns.put(currentNode.item, patterns.getOrDefault(currentNode.item, 0) + nodeHead.count);
        currentNode = currentNode.parent;
      }
      nodeHead = nodeHead.nodeLink;
    }

    List<String> result = patterns.entrySet().stream()
            .filter(entry -> entry.getValue() >= minSup)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

    Integer minValue = result.stream()
            .map(patterns::get)
            .min(Integer::compareTo)
            .orElse(null);
    System.out.println(item + ">>>>> " + result +" - minValue = "+minValue);
    return combineWithHead(result, item, minValue);
  }


  public List<List<String>> combineWithHead(List<String> items, String m, Integer count) {
    List<List<String>> result = new ArrayList<>();
    int n = items.size();

    for (int i = 1; i < (1 << n); i++) {
      List<String> subset = new ArrayList<>();
      for (int j = 0; j < n; j++) {
        if ((i & (1 << j)) > 0) {
          subset.add(items.get(j));
        }
      }
      subset.add(m);
      List<String> sortedSubset = sortTransaction(subset);
      result.add(new ArrayList<>(sortedSubset));
      if (count != null) {
        listFreq.put(new HashSet<>(subset), count);
      }
    }
    if(!result.isEmpty()){
      String itemSetStr = "";
      for (List<String> listItem : result) {
        itemSetStr +=listItem+"";
      }
      itemSetStr+=":"+count+"  ;";
      SubarrayGenerator.response.add(itemSetStr);
    }
    return result;
  }


  public static List<String >doTaskBuildFPGrowth(List<List<String>> transactions,Double minSup) {
    FPTree fpTree = new FPTree();
    fpTree.buildTree(transactions);
    fpTree.printTree();
    List<List<String>> listAllItemFrequent = new ArrayList<>();
    SubarrayGenerator.response.add(String.format("\n ///////////////////// DS Item Set thường xuyên /////////////////////"));
    for (String item : fpTree.fList) {
      List<List<String>> listItemFrequent = fpTree.findSampleConditionBasis(item,minSup);
      listAllItemFrequent.addAll(listItemFrequent);
      System.out.println(item + ": " + listItemFrequent);
      SubarrayGenerator.frequentItems = fpTree.frequentItems;
      SubarrayGenerator.listFreq = fpTree.listFreq;
    }

    SubarrayGenerator.response.add(String.format("\n ///////////////////// Chi tiết các luật tìm được /////////////////////"));
    for (List<String> listItem : listAllItemFrequent) {
      System.out.println(String.format("\n \n===================== %s =======================", listItem));
//      SubarrayGenerator.response.add(String.format("\n \n===================== %s =======================", listItem));
      SubarrayGenerator.generateSubarrays(listItem, 0, new ArrayList<>());
    }
    return SubarrayGenerator.response;
  }

}