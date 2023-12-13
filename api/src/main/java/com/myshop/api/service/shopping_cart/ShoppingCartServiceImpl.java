package com.myshop.api.service.shopping_cart;

import com.myshop.api.payload.request.shopping_cart.ShoppingCartRequest;
import com.myshop.api.service.product.ProductService;
import com.myshop.common.http.ApiResponse;
import com.myshop.repositories.product.entities.ProductDetail;
import com.myshop.repositories.product.repos.ColorRepository;
import com.myshop.repositories.product.repos.ProductDetailRepository;
import com.myshop.repositories.product.repos.ProductRepository;
import com.myshop.repositories.product.repos.SizeRepository;
import com.myshop.repositories.shopping_cart.entities.ShoppingCart;
import com.myshop.repositories.shopping_cart.repos.ShoppingCartRepository;
import com.myshop.repositories.user.entities.Customer;
import com.myshop.repositories.user.repos.AccountRepository;
import com.myshop.repositories.user.repos.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Transactional
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;

    @Autowired
    SizeRepository sizeRepository;
    @Autowired
    ColorRepository colorRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductService productService;
    @Autowired
    ProductDetailRepository productDetailRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    CustomerRepository customerRepository;


    public ShoppingCartServiceImpl(ShoppingCartRepository shoppingCartRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
    }


    @Override
    public ApiResponse<Object> addToCart(String userId, ShoppingCartRequest item) {
        Optional<Customer> customer = customerRepository.findByEmail(userId);
        if (!customer.isPresent()) {
            return ApiResponse.builder().message("Tài khoản không hợp lệ").status(505).build();
        }
        Optional<ProductDetail> productDetail = productDetailRepository.findById(item.getProductDetailID());
        if (!productDetail.isPresent()) {
            return ApiResponse.builder().message("Sản phẩm không tồn tại").status(505).build();
        }
        if (item.getQuantity() < 0 ) {
            return ApiResponse.builder().message("Số lượng không hợp lệ").status(505).build();
        }
        Optional<ShoppingCart> myCart = shoppingCartRepository.findShoppingCartByCustomerIdAndProductDetailId(customer.get().getId(), item.getProductDetailID());
        if (!myCart.isPresent()) {
            ShoppingCart newProduct = ShoppingCart.builder().customerId(customer.get().getId())
                    .productDetailId(productDetail.get().getId()).quantity(item.getQuantity()).build();
            shoppingCartRepository.save(newProduct);
            return ApiResponse.builder().status(200).message("Sản phẩm đã được thêm vào giỏ hàng").data(newProduct).build();
        } else {
            if(item.getQuantity()==0)
                shoppingCartRepository.delete(myCart.get());
            else
                myCart.get().setQuantity(item.getQuantity());
            return ApiResponse.builder().message("Giỏ hàng đã được cập nhật").status(200).build();
        }
    }


//    @Override
//    public ApiResponse<Object> deleteItem(Long userId, Long productID,Str) {
//    Color color = colorRepository.findById(colorName)
//    ProductDetail productDetail = productDetailRepository.findByProductAndColorAndSize()
////        ShoppingCart shoppingCart = shoppingCartRepository.findShoppingCartByUserInfo_IdAndProductDetail_Id(userId, productID);
////        if (shoppingCart == null || shoppingCart.getId() <= 0)
////            return ApiResponse.builder().status(101).message("cannot find this item in your cart").data(null).build();
////        shoppingCartRepository.delete(shoppingCart);
//        return ApiResponse.builder().status(200).message("update cart successful").data(Mono.just(null)).build();
//    }
//
    @Override
    public ApiResponse<?> getShoppingCart(String userId) {
        Optional<Customer> customer = customerRepository.findByEmail(userId);
        if (!customer.isPresent()) {
            return ApiResponse.builder().status(505).data(null).message("Không tìm thấy thông tin khách hàng").build();
        }
        List<ShoppingCart> listCart= shoppingCartRepository.findAllByCustomerId(customer.get().getId());
        List<Map<String,Object>> resCart= new ArrayList<>();
        for (ShoppingCart cart:listCart) {
            Map<String,Object> item= new HashMap<>();
            ProductDetail pdt=productDetailRepository.findById(cart.getProductDetailId()).get();
            ApiResponse res=productService.getDetailInventory(pdt.getProduct());
            item.put("color",colorRepository.findById(pdt.getColor()).get().getColorName());
            item.put("size",sizeRepository.findById(pdt.getSize()).get().getSizeName());
            item.put("product",res.getData());
            item.put("quantity",cart.getQuantity());
            resCart.add(item);
        }
        return ApiResponse.builder().status(200).data(resCart).message("ok").build();
    }
}
