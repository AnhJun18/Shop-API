package com.myshop.api.service.shopping_cart;

import com.myshop.api.base.CRUDBaseServiceImpl;
import com.myshop.api.payload.request.shopping_cart.ShoppingCartRequest;
import com.myshop.common.http.ApiResponse;
import com.myshop.repositories.product.entities.ProductDetail;
import com.myshop.repositories.product.repos.ProductDetailRepository;
import com.myshop.repositories.shopping_cart.entities.ShoppingCart;
import com.myshop.repositories.shopping_cart.repos.ShoppingCartRepository;
import com.myshop.repositories.user.entities.Account;
import com.myshop.repositories.user.entities.UserInfo;
import com.myshop.repositories.user.repos.AccountRepository;
import com.myshop.repositories.user.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Optional;


@Transactional
@Service
public class ShoppingCartServiceImpl extends CRUDBaseServiceImpl<ShoppingCart, ShoppingCart, ShoppingCart, Long> implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;

    @Autowired
    ProductDetailRepository productDetailRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    UserRepository userRepository;


    public ShoppingCartServiceImpl(ShoppingCartRepository shoppingCartRepository) {
        super(ShoppingCart.class, ShoppingCart.class, ShoppingCart.class, shoppingCartRepository);
        this.shoppingCartRepository = shoppingCartRepository;
    }


    @Override
    public ApiResponse<Object> addToCart(Long userId, ShoppingCartRequest item) {
        Optional<Account> account = accountRepository.findById(userId);
        if (!account.isPresent()) {
            return ApiResponse.builder().message("Account is not exists").status(0).build();
        }
        Optional<UserInfo> user = Optional.ofNullable(userRepository.findUserByAccount(account.get()));
        if (!user.isPresent()) {
            return ApiResponse.builder().message("User is not exists").status(0).build();
        }
        Optional<ProductDetail> productDetail = productDetailRepository.findById(item.getProductID());
        if (!productDetail.isPresent()) {
            return ApiResponse.builder().message("Product is not exists").status(0).build();
        }
        if (item.getAmount() < 1 || productDetail.get().getCurrent_number() < item.getAmount()) {
            return ApiResponse.builder().message("Invalid product quantity or insufficient product quantity").status(0).build();
        }
        ShoppingCart newProduct = ShoppingCart.builder().userInfo(user.get())
                .productDetail(productDetail.get()).amount(item.getAmount()).build();
        shoppingCartRepository.save(newProduct);

        return  ApiResponse.builder().status(200).message("Add to cart successful").data(Mono.just(newProduct)).build();
    }

    @Override
    public ApiResponse<Object> updateCart(Long userId, ShoppingCartRequest item) {
        ShoppingCart shoppingCart= shoppingCartRepository.findShoppingCartByUserInfo_IdAndProductDetail_Id(userId,item.getProductID());
        if(shoppingCart == null || shoppingCart.getId()<=0)
            return ApiResponse.builder().status(101).message("update cart fail").data(null).build();
        if(item.getAmount()==0)
             shoppingCartRepository.delete(shoppingCart);
        else{
            shoppingCart.setAmount(item.getAmount());
            shoppingCartRepository.save(shoppingCart);
}
        return ApiResponse.builder().status(200).message("update cart successful").data(Mono.just(shoppingCart)).build();
    }

    @Override
    public Iterable<Object> getShoppingCart(Long userID) {
        return shoppingCartRepository.findAllByUserInfo_Id(userID);
    }
}
