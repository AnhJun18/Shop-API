package com.myshop.api.service.order;

import com.myshop.api.base.CRUDBaseServiceImpl;
import com.myshop.api.payload.request.order.OrderDetailRequest;
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
import com.myshop.repositories.shopping_cart.entities.ShoppingCart;
import com.myshop.repositories.shopping_cart.repos.ShoppingCartRepository;
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
import org.springframework.transaction.interceptor.TransactionAspectSupport;

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
    private ShoppingCartRepository shoppingCartRepository;
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
            message = "T??i kho???n kh??ng ho???t ?????ng";
        } else {
            try {
                newOrder = Order.builder().address(orderRequest.getAddress())
                        .note(orderRequest.getNote())
                        .feeShip(orderRequest.getFeeShip()).status(statusRepository.findById(1L).get())
                        .userInfo(userInfo.get()).nameReceiver(orderRequest.getNameReceiver())
                        .phoneReceiver(orderRequest.getPhoneReceiver())
                        .build();
                orderRepository.save(newOrder);
                Order finalNewOrder = newOrder;
                for (OrderDetailRequest item:orderRequest.getListProduct()) {
                    Optional<ProductDetail> productDetail = productDetailRepository.findById(item.getProduct_id());
                    if(productDetail.get().getCurrent_number() < item.getAmount())
                        throw new Exception("V???n ????? t???n kho s??? l?????ng s???n ph???m "+productDetail.get().getInfoProduct().getName() +" c???a ch??ng t??i kh??ng ?????!");
                    ShoppingCart checkCart= shoppingCartRepository.findShoppingCartByUserInfo_IdAndProductDetail_Id(userID,item.getProduct_id());
                    if(checkCart != null && checkCart.getId()>0)
                        shoppingCartRepository.delete(checkCart);
                    orderDetailRepository.save(OrderDetail.builder()
                            .productDetail(productDetail.get())
                            .order(finalNewOrder)
                            .amount(item.getAmount())
                            .prices(productDetail.get().getInfoProduct().getPrice())
                            .build());
                }
                result = true;
                message = "????n h??ng ???? ???????c t???o th??nh c??ng";
            } catch (Exception e) {
                result = false;
                message = "L???i t???o ?????n h??ng "+e.getMessage();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }

        }
        return OrderResponse.builder().message(message).order(newOrder).status(result).build();
    }

    @Override
    public List<Order> getTheOrder(Long userID) {

        Iterator<Order> source = orderRepository.findAllByUserInfo_Id(userID).iterator();
        List<Order> target = new ArrayList<>();
        source.forEachRemaining((item) -> {
            target.add(item.copy());
        });
        return target;
    }

    @Override
    public Iterable<Order> getAllOrderByAdmin() {
        return orderRepository.findAll();
    }

    @Override
    public Iterable<Order> getTheOrderByStatus(Long userID, String status) {

        return orderRepository.findAllByUserInfo_IdAndStatus_Name(userID, status);
    }

    @Override
    public Iterable<Order> getOrderByStatusByAdmin(String status) {
        return orderRepository.findAllByStatus_Name(status);
    }

    @Override
    public OrderResponse confirmOrder(Long userID, Long idOrder) {
        Order order = orderRepository.findOrderById(idOrder);
        if (order == null || order.getId() <= 0 || !order.getStatus().getName().equals("Ch??? X??c Nh???n")) {
            return OrderResponse.builder().status(false).message("????n h??ng kh??ng t???n t???i ho???c ???? ???????c x??c nh???n").order(order).build();
        }
        for (OrderDetail item:order.getOrderDetails()) {
            if(item.getAmount()> item.getProductDetail().getCurrent_number())
                return OrderResponse.builder().status(false).message("S???n ph???m ???? h???t h??ng").order(order).build();
        }
        Status nextStatus = statusRepository.findByName("??ang Chu???n B??? H??ng");
        order.setStatus(nextStatus);
        orderRepository.save(order);
        order.getOrderDetails().forEach(item -> {
            item.getProductDetail().setCurrent_number(item.getProductDetail().getCurrent_number() - item.getAmount());
            item.getProductDetail().getInfoProduct().setSold(item.getProductDetail().getInfoProduct().getSold() + item.getAmount());
        });
        return OrderResponse.builder().status(true).message("????n h??ng ???? ???????c x??c nh???n").order(order).build();
    }

    @Override
    public OrderResponse deliveryOrder(Long idOrder) {
        Order order = orderRepository.findOrderById(idOrder);
        if (order == null || order.getId() <= 0 || !order.getStatus().getName().equals("??ang Chu???n B??? H??ng")) {
            return OrderResponse.builder().status(false).message("C?? l???i! Vui l??ng th??? l???i").order(order).build();
        }
        Status nextStatus = statusRepository.findByName("??ang V???n Chuy???n");
        order.setStatus(nextStatus);
        orderRepository.save(order);

        return OrderResponse.builder().status(true).message("????n h??ng ???? ???????c v???n chuy???n").order(order).build();
    }

    @Override
    public OrderResponse confirmPaidOrder(Long idOrder) {
        Order order = orderRepository.findOrderById(idOrder);
        if (order == null || order.getId() <= 0 || !order.getStatus().getName().equals("??ang V???n Chuy???n")) {
            return OrderResponse.builder().status(false).message("C?? l???i! Vui l??ng th??? l???i").order(order).build();
        }
        Status nextStatus = statusRepository.findByName("???? Thanh To??n");
        order.setStatus(nextStatus);
        orderRepository.save(order);

        return OrderResponse.builder().status(true).message("????n h??ng ???? thanh to??n th??nh c??ng!").order(order).build();
    }

    @Override
    public OrderResponse confirmCancelOrder(Long idOrder) {
        Order order = orderRepository.findOrderById(idOrder);
        if (order == null || order.getId() <= 0 || order.getStatus().getName().equals("???? Thanh To??n")) {
            return OrderResponse.builder().status(false).message("????n h??ng kh??ng t???n t???i ho???c ???? ???????c thanh to??n!").order(order).build();
        }
        Status nextStatus = statusRepository.findByName("???? H???y");
        order.setStatus(nextStatus);
        orderRepository.save(order);

        return OrderResponse.builder().status(true).message("????n h??ng ???? b??? h???y").order(order).build();
    }

    @Override
    public OrderResponse cancelOrderByUser(Long idOrder) {
        Order order = orderRepository.findOrderById(idOrder);
        if (order == null || order.getId() <= 0) {
            return OrderResponse.builder().status(false).message("????n h??ng kh??ng t???n t???i").order(order).build();
        }
        if(!order.getStatus().getName().equals("Ch??? X??c Nh???n") && !order.getStatus().getName().equals("??ang Chu???n B??? H??ng")){
            // Kh??ch h??ng ch??? ??u???c huy ????n khi ????n ??ang tr???ng th??i ch??? x??c nh??n ho???c ??ang chu???n b??? h??ng
            return OrderResponse.builder().status(false).message("????n h??ng "+order.getStatus().getName()+" n??n kh??ng th??? h???y").order(order).build();
        }

        Status nextStatus = statusRepository.findByName("???? H???y");
        order.setStatus(nextStatus);
        if(!order.getStatus().getName().equals("Ch??? X??c Nh???n")){
            order.getOrderDetails().forEach(item->{
                item.getProductDetail().setCurrent_number(item.getProductDetail().getCurrent_number()+item.getAmount());
            });
        }
        orderRepository.save(order);

        return OrderResponse.builder().status(true).message("????n h??ng ???? b??? h???y").order(order).build();
    }

    @Override
    public Page<Order> searchOrder(Date from, Date to, String query, String status, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
        return orderRepository.searchOrder(from.toInstant(), to.toInstant(), query == "" ? null : query, status == "" ? null : status, pageable);
    }
}
