package com.pragma.foodcourt.infrastructure.out.jpa.adapter;

import com.pragma.foodcourt.domain.model.Dish;
import com.pragma.foodcourt.domain.model.PagedResult;
import com.pragma.foodcourt.domain.spi.IDishPersistencePort;
import com.pragma.foodcourt.infrastructure.out.jpa.entity.DishEntity;
import com.pragma.foodcourt.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.pragma.foodcourt.infrastructure.out.jpa.repository.IDishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class DishJpaAdapter implements IDishPersistencePort {

    private static final String NAME_FIELD = "name";

    private final IDishRepository dishRepository;
    private final IDishEntityMapper dishEntityMapper;

    @Override
    public Dish saveDish(Dish dish) {
        DishEntity savedEntity = dishRepository.save(dishEntityMapper.toEntity(dish));
        return dishEntityMapper.toDish(savedEntity);
    }

    @Override
    public Dish findDishById(UUID dishId) {
        return dishRepository.findById(dishId)
                .map(dishEntityMapper::toDish)
                .orElse(null);
    }

    @Override
    public PagedResult<Dish> findActiveDishesByRestaurant(
            UUID restaurantId, String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc(NAME_FIELD).ignoreCase()));
        Page<DishEntity> entityPage = (category == null)
                ? dishRepository.findByRestaurantIdAndActiveTrue(restaurantId, pageable)
                : dishRepository.findByRestaurantIdAndCategoryIgnoreCaseAndActiveTrue(
                        restaurantId, category, pageable);
        List<Dish> content = entityPage.getContent().stream()
                .map(dishEntityMapper::toDish)
                .toList();
        return new PagedResult<>(
                content,
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages());
    }
}
