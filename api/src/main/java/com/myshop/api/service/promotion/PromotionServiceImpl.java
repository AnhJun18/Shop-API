package com.myshop.api.service.promotion;

import com.myshop.api.payload.request.promotion.AddPrToPromotionRequest;
import com.myshop.api.payload.request.promotion.PromotionRequest;
import com.myshop.repositories.product.entities.Product;
import com.myshop.repositories.product.repos.ProductRepository;
import com.myshop.repositories.promotion.entity.Promotion;
import com.myshop.repositories.promotion.repos.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PromotionServiceImpl implements   PromotionService{
    @Autowired
    PromotionRepository promotionRepository;

    @Autowired
    ProductRepository productRepository;

    @Override
    public Promotion createPromotion(PromotionRequest promotionRequest) {
        Promotion promotion = Promotion.builder()
                .name(promotionRequest.getName())
                .description(promotionRequest.getDescription())
                .value(promotionRequest.getValue())
                .startDate(promotionRequest.getStartDate())
                .endDate(promotionRequest.getEndDate())
                .build();
        promotionRepository.save(promotion);
        return promotion;
    }

    @Override
    public List<Promotion> getAllPromotion() {
        return promotionRepository.findAll();
    }

    @Transactional
    @Override
    public Promotion addProductToPromotion(AddPrToPromotionRequest request) throws Exception {
        Optional<Promotion> promotion= promotionRepository.findById(request.getPromotionID());
        if(promotion.isPresent() ==false){
            throw new Exception("Promotion not found");
        }
        else{
            request.getListProductID().stream().forEach(item->{
               Optional<Product> product= productRepository.findById(item);
               if(!product.isPresent())
                   throw new RuntimeException("Cannot found product by id");
                product.get().getPromotions().add(promotion.get());
                product.get().setPromotions(product.get().getPromotions());
                productRepository.save(product.get());
            });



        }

        return null;
    }
}
