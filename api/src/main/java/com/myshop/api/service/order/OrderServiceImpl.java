package com.myshop.api.service.order;

import com.myshop.api.base.CRUDBaseServiceImpl;
import com.myshop.api.payload.request.order.OrderRequest;
import com.myshop.api.payload.response.order.OrderResponse;
import com.myshop.repositories.order.entities.Order;
import com.myshop.repositories.order.entities.OrderDetail;
import com.myshop.repositories.order.entities.Status;
import com.myshop.repositories.order.repos.OrderDetailRepository;
import com.myshop.repositories.order.repos.OrderRepository;
import com.myshop.repositories.order.repos.StatusRepository;
import com.myshop.repositories.product.entities.ProductDetail;
import com.myshop.repositories.product.repos.ProductDetailRepository;
import com.myshop.repositories.user.entities.UserInfo;
import com.myshop.repositories.user.repos.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
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
                    .userInfo(userInfo.get()).nameReceiver(orderRequest.getNameReceiver())
                    .phoneReceiver(orderRequest.getPhoneReceiver())
                    .build();
            orderRepository.save(newOrder);
            Order finalNewOrder = newOrder;
            orderRequest.getListProduct().forEach((item)->{
             Optional<ProductDetail> productDetail = productDetailRepository.findById(item.getProduct_id());
              orderDetailRepository.save(OrderDetail.builder()
                      .productDetail(productDetail.get())
                      .order(finalNewOrder)
                      .amount(item.getAmount())
                      .prices(productDetail.get().getInfoProduct().getPrice())
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
    public Iterable<Order> getAllOrderByAdmin() {
        return  orderRepository.findAll();
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

    @Override
    public Iterable<Order> getOrderByStatusByAdmin(String status) {
        return orderRepository.findAllByStatus_Name(status);
    }

    @Override
    public OrderResponse confirmOrder(Long userID, Long idOrder) {
        Order order= orderRepository.findOrderById(idOrder);
       if(order == null || order.getId() <= 0 || !order.getStatus().getName().equals("Chờ Xác Nhận")){
           return OrderResponse.builder().status(false).message("Đơn hàng không tồn tại hoặc đã được xác nhận").order(order).build();
       }
        Status nextStatus = statusRepository.findByName("Đang Chuẩn Bị Hàng");
        order.setStatus(nextStatus);
        orderRepository.save(order);
        order.getOrderDetails().forEach(item -> {
            item.getProductDetail().setCurrent_number(item.getProductDetail().getCurrent_number()-item.getAmount());
            item.getProductDetail().getInfoProduct().setSold(item.getProductDetail().getInfoProduct().getSold()+item.getAmount());
        });
        return OrderResponse.builder().status(true).message("Đơn hàng đã được xác nhận").order(order).build();
    }

    @Override
    public Page<Order> searchOrder(Date from,Date  to, String query, String status, Integer page, Integer size)  {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
        return  orderRepository.searchOrder(from.toInstant(),to.toInstant(),query==""?null:query,status,pageable);
    }
}
