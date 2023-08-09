package com.myshop.security.audit;

import com.myshop.security.jwt.CustomAuthUser;
import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public @NonNull Optional<String> getCurrentAuditor() {
        try {
            if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
                CustomAuthUser customAuthUser = (CustomAuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (customAuthUser != null) {
                    return Optional.of(customAuthUser.getEmail());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.of("");
    }

}