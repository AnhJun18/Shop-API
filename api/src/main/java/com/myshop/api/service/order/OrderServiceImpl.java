package com.myshop.api.service.order;

import com.myshop.api.base.CRUDBaseServiceImpl;
import com.myshop.api.payload.request.order.OrderRequest;
import com.myshop.api.payload.response.order.OrderResponse;
import com.myshop.repositories.order.entities.Order;
import com.myshop.repositories.order.entities.OrderDetail;
import com.myshop.repositories.order.repos.OrderDetailRepository;
import com.myshop.repositories.order.repos.OrderRepository;
import com.myshop.repositories.order.repos.StatusRepository;
import com.myshop.repositories.product.entities.ProductDetail;
import com.myshop.repositories.product.repos.ProductDetailRepository;
import com.myshop.repositories.user.entities.UserInfo;
import com.myshop.repositories.user.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


@Transactional
@Service
public class OrderServiceImpl extends CRUDBaseServiceImpl<Order, OrderRequest, OrderResponse, Long> implements OrderService {

    private final OrderRepository orderRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private ProductDetailRepository productDetailRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;



    public OrderServiceImpl(OrderRepository orderRepository) {
        super(Order.class, OrderRequest.class, OrderResponse.class, orderRepository);
        this.orderRepository = orderRepository;
    }

    @Transactional
    @Override
    public OrderResponse order(Long userID, OrderRequest orderRequest) {
        boolean result = false;
        String message = "";
        Order newOrder = null;
        Optional<UserInfo> userInfo = userRepository.findById(userID);
        if (!userInfo.isPresent()) {
            result = false;
            message = "Tài khoản không hoạt động";
        } else {
            newOrder = Order.builder().address(orderRequest.getAddress())
                    .note(orderRequest.getNote())
                    .feeShip(orderRequest.getFeeShip()).status(statusRepository.findById(1L).get())
                    .userInfo(userInfo.get())
                    .build();
            orderRepository.save(newOrder);
            Order finalNewOrder = newOrder;
            orderRequest.getListProduct().forEach((item)->{
             Optional<ProductDetail> productDetail = productDetailRepository.findById(item.getProduct_id());
              orderDetailRepository.save(OrderDetail.builder()
                      .productDetail(productDetail.get())
                      .order(finalNewOrder)
                      .amount(item.getAmount())
                      .prices(item.getPrices())
                      .build());
          });
            result = true;
            message = "Đơn hàng đã được tạo thành công";
        }
        return OrderResponse.builder().message(message).order(newOrder).status(result).build();
    }

    @Override
    public List<Order> getTheOrder(Long userID){

        Iterator<Order> source = orderRepository.findAllByUserInfo_Id(userID).iterator();;
        List<Order> target = new ArrayList<>();
        source.forEachRemaining( (item)->{
            target.add(item.copy());
        });
        return  target;
    }

    @Override
    public List<Order> getTheOrderByStatus(Long userID, String status) {
        Iterator<Order> source = orderRepository.findAllByUserInfo_IdAndStatus_Name(userID,status).iterator();
        List<Order> target = new ArrayList<>();
        source.forEachRemaining( (item)->{
            target.add(item.copy());
        });
        return  target;
    }
}
