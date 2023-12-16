package com.myshop.api.service.fpgrowth;

import com.myshop.api.FPGrowth.FPTree;
import com.myshop.api.FPGrowth.SubarrayGenerator;
import com.myshop.api.payload.request.fpgrowth.FPRequest;
import com.myshop.api.payload.response.fp_growth.FPResponse;
import com.myshop.api.service.product.ProductService;
import com.myshop.common.http.ApiResponse;
import com.myshop.repositories.order.repos.OrderRepository;
import com.myshop.repositories.user.entities.Customer;
import com.myshop.repositories.user.repos.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FPGrowthService {
  @Autowired
  OrderRepository orderRepository;
  @Autowired
  CustomerRepository customerRepository;

  @Autowired
  ProductService productService;

  public static Map<String,List<String>> hashCache = new HashMap<>();
  public Map<String, Object> infoInit(Boolean isTest) {
    return isTest ? orderRepository.getInfoInitTest() : orderRepository.getInfoInit();
  }
  public FPResponse trainFPGrowth( FPRequest fpRequest) {
    long startTime = System.currentTimeMillis();
    List<String> listSelected = fpRequest.getItemSelected().stream()
            .map(String::trim).collect(Collectors.toList());
    List<Map<String, Object>> listTransaction = fpRequest.getIsTest() ? orderRepository.getTransactionTest(fpRequest.getMinSup()) : orderRepository.getTransaction(fpRequest.getMinSup());
    List<String> listHeader = listTransaction.get(0).keySet().stream().map(String::trim).collect(Collectors.toList());
    listHeader.remove("SODH");
    listHeader.add(0,"SODH");
    String header="|";
    SubarrayGenerator.responseTransaction.clear();
    FPGrowthService.hashCache.clear();
    for(String key: listHeader){
      header +=String.format("%7s|",key);
    }
//    System.out.println(header);
    SubarrayGenerator.responseTransaction.add(header);
    for(Map<String,Object> mapItem: listTransaction){
      String newList = "|";
      for(String key: listHeader){
        newList +=String.format("%7s|",mapItem.get(key));
      }
      SubarrayGenerator.responseTransaction.add(newList);
//      System.out.println(newList);
    }
    Double minSup = listTransaction.size() * fpRequest.getMinSup() * 1.0 / (100);
    List<List<String>> transactions = new ArrayList<>();
    for (Map<String, Object> mapItem : listTransaction) {
      List<String> trans = new ArrayList<>();
      for (String maMH : mapItem.keySet()) {
        if (!maMH.equals("SODH") && mapItem.get(maMH) instanceof Number && Integer.parseInt(mapItem.get(maMH).toString()) == 1) {
          if (fpRequest.getItemSelected().contains("-1") || listSelected.contains(maMH))
            trans.add(maMH);
        }
      }
      transactions.add(trans);
    }
    SubarrayGenerator.minConf = fpRequest.getMinConf() * 1.0 / 100;
    SubarrayGenerator.response.clear();
    List<String> result = FPTree.doTaskBuildFPGrowth(transactions, minSup);
    result.add(0,String.format("Tổng thời gian thực hiện : %d ms",System.currentTimeMillis()-startTime));
    return FPResponse.builder().data(result).mapTrans(SubarrayGenerator.responseTransaction).totalTime(System.currentTimeMillis()-startTime).build();
  }


  public ApiResponse<?> getOptionItem(Boolean isTest) {
    return ApiResponse.of(isTest ? orderRepository.getOptsItemTest() : orderRepository.getOptsItem());
  }

  public ApiResponse<?> getListSuggest(String email) {
    if(StringUtils.isEmpty(email)){
      return ApiResponse.of(new ArrayList<>());
    }
    Customer customer = customerRepository.findByEmail(email).get();
    Set<String> listSuggest = new HashSet<>();
    List<Long> listProductBought= orderRepository.getListProductBought(customer.getId());
    for (Long product:listProductBought) {
      String productCodeGet = "_"+product+"_";
      for (String cacheKey:hashCache.keySet()) {
          if(cacheKey.contains(productCodeGet)){
            listSuggest.addAll(hashCache.get(cacheKey));
          }
      }
    }
    String infoProduct = String.join(",",listSuggest);
    List<Map<String, Object>> resMap =productService.getSuggest(infoProduct);
    return ApiResponse.of(resMap);
  }
}