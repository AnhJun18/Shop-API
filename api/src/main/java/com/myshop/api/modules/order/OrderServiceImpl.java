package com.myshop.api.modules.order;

import com.myshop.api.base.CRUDBaseServiceImpl;
import com.myshop.api.payload.request.order.OrderDetailRequest;
import com.myshop.api.payload.request.order.OrderRequest;
import com.myshop.api.payload.response.order.OrderResponse;
import com.myshop.api.modules.shipment.GHTKService;
import com.myshop.repositories.order.entities.Order;
import com.myshop.repositories.order.entities.OrderDetail;
import com.myshop.repositories.order.entities.Status;
import com.myshop.repositories.order.repos.OrderDetailRepository;
import com.myshop.repositories.order.repos.OrderRepository;
import com.myshop.repositories.order.repos.StatusRepository;
import com.myshop.repositories.stocks.entities.StocksDetail;
import com.myshop.repositories.stocks.repos.StocksDetailRepository;
import com.myshop.repositories.shipment.entities.Shipment;
import com.myshop.repositories.shipment.repos.ShipmentRepository;
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

import java.io.IOException;
import java.util.*;

@Slf4j
@Transactional
@Service
public class OrderServiceImpl extends CRUDBaseServiceImpl<Order, OrderRequest, OrderResponse, Long> implements OrderService {

    private final OrderRepository orderRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private StocksDetailRepository stocksDetailRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private ShipmentRepository shipmentRepository;
    @Autowired
    private GHTKService ghtkService;
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
        System.out.println("csss");
        Optional<UserInfo> userInfo = userRepository.findById(userID);
        if (!userInfo.isPresent()) {
            result = false;
            message = "Tài khoản không hoạt động";
        } else {
            try {
                newOrder = Order.builder()
                        .note(orderRequest.getNote())
                        .feeShip(orderRequest.getFeeShip())
                        .status(statusRepository.findByName("Chờ Xác Nhận"))
                        .userInfo(userInfo.get())
                        .build();
                System.out.println(newOrder.toString());
                orderRepository.save(newOrder);

                Shipment shipment = Shipment.builder()
                        .address(orderRequest.getAddress())
                        .nameReceiver(orderRequest.getNameReceiver())
                        .phoneReceiver(orderRequest.getPhoneReceiver())
                        .province(orderRequest.getProvince())
                        .district(orderRequest.getDistrict())
                        .ward(orderRequest.getWard())
                        .order(newOrder)
                        .build();
                shipmentRepository.save(shipment);
                Order finalNewOrder = newOrder;
                for (OrderDetailRequest item:orderRequest.getListProduct()) {
                    Optional<StocksDetail> productDetail = stocksDetailRepository.findById(item.getProduct_id());
                    if(productDetail.get().getCurrent_number() < item.getAmount())
                        throw new Exception("Vấn đề tồn kho số lượng sản phẩm "+productDetail.get().getInfoProduct().getName() +" của chúng tôi không đủ!");
                    ShoppingCart checkCart= shoppingCartRepository.findShoppingCartByUserInfo_IdAndStocksDetail_Id(userID,item.getProduct_id());
                    if(checkCart != null && checkCart.getId()>0)
                        shoppingCartRepository.delete(checkCart);
                    orderDetailRepository.save(OrderDetail.builder()
                            .stocksDetail(productDetail.get())
                            .order(finalNewOrder)
                            .amount(item.getAmount())
                            .prices(item.getPrice_after())
                            .build());
                }
                result = true;
                message = "Đơn hàng đã được tạo thành công";
            } catch (Exception e) {
                result = false;
                message = "Lỗi tạo đợn hàng "+e.getMessage();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return OrderResponse.builder().message(message).order(null).status(result).build();
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

    @Transactional
    @Override
    public OrderResponse confirmOrder(Long userID, Long idOrder) {
        Order order = orderRepository.findOrderById(idOrder);
        if (order == null || order.getId() <= 0 || !order.getStatus().getName().equals("Chờ Xác Nhận")) {
            return OrderResponse.builder().status(false).message("Đơn hàng không tồn tại hoặc đã được xác nhận").order(order).build();
        }
        for (OrderDetail item:order.getOrderDetails()) {
            if(item.getAmount()> item.getStocksDetail().getCurrent_number())
                return OrderResponse.builder().status(false).message("Sản phẩm đã hết hàng").order(order).build();
        }
        try {
            ghtkService.createOrder(order.getId());

        } catch (IOException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return OrderResponse.builder().status(false).message("Không thể giao hàng cho đối tác vận chuyển").order(order).build();
        }
        Status nextStatus = statusRepository.findByName("Đang Chuẩn Bị Hàng");
        order.setStatus(nextStatus);
        orderRepository.save(order);
        order.getOrderDetails().forEach(item -> {
            item.getStocksDetail().setCurrent_number(item.getStocksDetail().getCurrent_number() - item.getAmount());
            item.getStocksDetail().getInfoProduct().setSold(item.getStocksDetail().getInfoProduct().getSold() + item.getAmount());
        });
        return OrderResponse.builder().status(true).message("Đơn hàng đã được xác nhận").order(order).build();
    }

    @Override
    public OrderResponse deliveryOrder(Long idOrder) {
        Order order = orderRepository.findOrderById(idOrder);
        if (order == null || order.getId() <= 0 || !order.getStatus().getName().equals("Đang Chuẩn Bị Hàng")) {
            return OrderResponse.builder().status(false).message("Có lỗi! Vui lòng thử lại").order(order).build();
        }
        Status nextStatus = statusRepository.findByName("Đang Vận Chuyển");
        order.setStatus(nextStatus);
        orderRepository.save(order);

        return OrderResponse.builder().status(true).message("Đơn hàng đã được vận chuyển").order(order).build();
    }

    @Override
    public OrderResponse confirmPaidOrder(Long idOrder) {
        Order order = orderRepository.findOrderById(idOrder);
        if (order == null || order.getId() <= 0 || !order.getStatus().getName().equals("Đang Vận Chuyển")) {
            return OrderResponse.builder().status(false).message("Có lỗi! Vui lòng thử lại").order(order).build();
        }
        Status nextStatus = statusRepository.findByName("Đã Thanh Toán");
        order.setStatus(nextStatus);
        orderRepository.save(order);

        return OrderResponse.builder().status(true).message("Đơn hàng đã thanh toán thành công!").order(order).build();
    }

    @Override
    public OrderResponse confirmCancelOrder(Long idOrder) {
        Order order = orderRepository.findOrderById(idOrder);
        if (order == null || order.getId() <= 0 || order.getStatus().getName().equals("Đã Thanh Toán")) {
            return OrderResponse.builder().status(false).message("Đơn hàng không tồn tại hoặc đã được thanh toán!").order(order).build();
        }
        Status nextStatus = statusRepository.findByName("Đã Hủy");
        order.setStatus(nextStatus);
        orderRepository.save(order);

        return OrderResponse.builder().status(true).message("Đơn hàng đã bị hủy").order(order).build();
    }

    @Override
    public OrderResponse cancelOrderByUser(Long idOrder) {
        Order order = orderRepository.findOrderById(idOrder);
        if (order == null || order.getId() <= 0) {
            return OrderResponse.builder().status(false).message("Đơn hàng không tồn tại").order(order).build();
        }
        if(!order.getStatus().getName().equals("Chờ Xác Nhận") && !order.getStatus().getName().equals("Đang Chuẩn Bị Hàng")){
            // Khách hàng chỉ đuọc huy đơn khi đơn đang trạng thái chờ xác nhân hoặc đang chuẩn bị hàng
            return OrderResponse.builder().status(false).message("Đơn hàng "+order.getStatus().getName()+" nên không thể hủy").order(order).build();
        }

        Status nextStatus = statusRepository.findByName("Đã Hủy");
        order.setStatus(nextStatus);
        if(!order.getStatus().getName().equals("Chờ Xác Nhận")){
            order.getOrderDetails().forEach(item->{
                item.getStocksDetail().setCurrent_number(item.getStocksDetail().getCurrent_number()+item.getAmount());
            });
        }
        orderRepository.save(order);

        return OrderResponse.builder().status(true).message("Đơn hàng đã bị hủy").order(order).build();
    }

    @Override
    public Page<Order> searchOrder(Date from, Date to, String query, String status, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
        return orderRepository.searchOrder(from.toInstant(), to.toInstant(), query == "" ? null : query, status == "" ? null : status, pageable);
    }
}
