package backend.backend.implement.busImplement;

import backend.backend.dao.Bus.BusCategoryDAO;
import backend.backend.dao.Bus.RouteBusCategoryPriceDAO;
import backend.backend.dao.Bus.RouteDAO;
import backend.backend.dto.BusDTO.*;
import backend.backend.entity.BusCategory;
import backend.backend.entity.Route;
import backend.backend.entity.RouteBusCategoryPrice;
import backend.backend.service.busService.RouteBusCategoryPriceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class RouteBusCategoryPriceServiceImpl implements RouteBusCategoryPriceService {


    private final RouteBusCategoryPriceDAO routeBusCategoryPriceDAO;
    private final RouteDAO routeDAO;
    private final BusCategoryDAO busCategoryDAO;

    // Helper methods to convert entities to DTOs
    private RouteResponse convertToRouteResponse(Route route) {
        return new RouteResponse(route);
    }

    private BusCategoryResponse convertToBusCategoryResponse(BusCategory busCategory) {
        return new BusCategoryResponse(busCategory);
    }

    private RouteBusCategoryPriceResponse convertToRouteBusCategoryPriceResponse(RouteBusCategoryPrice priceRule) {
        return RouteBusCategoryPriceResponse.builder()
                .id(priceRule.getId())
                .route(convertToRouteResponse(priceRule.getRoute()))
                .busCategory(convertToBusCategoryResponse(priceRule.getBusCategory()))
                .basePrice(priceRule.getBasePrice())
                .promotionPrice(priceRule.getPromotionPrice())
                .validFrom(priceRule.getValidFrom())
                .validTo(priceRule.getValidTo())
                .notes(priceRule.getNotes())
                .createdAt(priceRule.getCreatedAt())
                .updatedAt(priceRule.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public RouteBusCategoryPriceResponse createRouteBusCategoryPrice(CreateRouteBusCategoryPriceRequest request) {
        // Kiểm tra xung đột thời gian hiệu lực
        List<RouteBusCategoryPrice> existingRules = routeBusCategoryPriceDAO.findAll();
        for (RouteBusCategoryPrice rule : existingRules) {
            if (rule.getRoute().getId().equals(request.routeId()) &&
                    rule.getBusCategory().getId().equals(request.busCategoryId()) &&
                    !(request.validTo().isBefore(rule.getValidFrom()) || request.validFrom().isAfter(rule.getValidTo()))) {
                throw new IllegalArgumentException("Đã tồn tại quy tắc giá cho tuyến đường và loại xe này trong khoảng thời gian hiệu lực đã cho.");
            }
        }

        Route route = routeDAO.findByIdWithLocations(request.routeId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tuyến đường với ID: " + request.routeId()));
        BusCategory busCategory = busCategoryDAO.findById(request.busCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy loại xe buýt với ID: " + request.busCategoryId()));

        if (request.validFrom().isAfter(request.validTo())) {
            throw new IllegalArgumentException("Ngày 'Hiệu lực từ' không được sau ngày 'Hiệu lực đến'.");
        }
        if (request.basePrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá cơ bản không được âm.");
        }
        if (request.promotionPrice() != null && request.promotionPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá khuyến mãi không được âm.");
        }

        RouteBusCategoryPrice newPriceRule = new RouteBusCategoryPrice();
        newPriceRule.setRoute(route);
        newPriceRule.setBusCategory(busCategory);
        newPriceRule.setBasePrice(request.basePrice());
        newPriceRule.setPromotionPrice(request.promotionPrice());
        newPriceRule.setValidFrom(request.validFrom());
        newPriceRule.setValidTo(request.validTo());
        newPriceRule.setNotes(request.notes());

        RouteBusCategoryPrice savedPriceRule = routeBusCategoryPriceDAO.save(newPriceRule);
        log.info("Đã tạo quy tắc giá mới với ID: {}", savedPriceRule.getId());
        
        // ✅ FIX: Manually set the already-loaded entities to avoid lazy loading issues
        savedPriceRule.setRoute(route);
        savedPriceRule.setBusCategory(busCategory);
        
        return convertToRouteBusCategoryPriceResponse(savedPriceRule);
    }

    @Override
    @Transactional
    public RouteBusCategoryPriceResponse updateRouteBusCategoryPrice(Integer id, UpdateRouteBusCategoryPriceRequest request) {
        RouteBusCategoryPrice existingPriceRule = routeBusCategoryPriceDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy quy tắc giá với ID: " + id));

        // Kiểm tra xung đột thời gian hiệu lực với các quy tắc khác (trừ chính nó)
        List<RouteBusCategoryPrice> existingRules = routeBusCategoryPriceDAO.findAll();
        for (RouteBusCategoryPrice rule : existingRules) {
            if (!rule.getId().equals(id) &&
                    rule.getRoute().getId().equals(request.routeId() != null ? request.routeId() : existingPriceRule.getRoute().getId()) &&
                    rule.getBusCategory().getId().equals(request.busCategoryId() != null ? request.busCategoryId() : existingPriceRule.getBusCategory().getId()) &&
                    !( (request.validTo() != null ? request.validTo() : existingPriceRule.getValidTo()).isBefore(rule.getValidFrom()) ||
                            (request.validFrom() != null ? request.validFrom() : existingPriceRule.getValidFrom()).isAfter(rule.getValidTo()) ) ) {
                throw new IllegalArgumentException("Việc cập nhật này sẽ tạo ra xung đột thời gian hiệu lực với quy tắc giá hiện có.");
            }
        }

        Optional.ofNullable(request.routeId()).ifPresent(routeId -> {
            Route route = routeDAO.findByIdWithLocations(routeId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tuyến đường với ID: " + routeId));
            existingPriceRule.setRoute(route);
        });

        Optional.ofNullable(request.busCategoryId()).ifPresent(busCategoryId -> {
            BusCategory busCategory = busCategoryDAO.findById(busCategoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy loại xe buýt với ID: " + busCategoryId));
            existingPriceRule.setBusCategory(busCategory);
        });

        Optional.ofNullable(request.basePrice()).ifPresent(price -> {
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Giá cơ bản không được âm.");
            }
            existingPriceRule.setBasePrice(price);
        });
        Optional.ofNullable(request.promotionPrice()).ifPresent(price -> {
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Giá khuyến mãi không được âm.");
            }
            existingPriceRule.setPromotionPrice(price);
        });

        LocalDate newValidFrom = request.validFrom() != null ? request.validFrom() : existingPriceRule.getValidFrom();
        LocalDate newValidTo = request.validTo() != null ? request.validTo() : existingPriceRule.getValidTo();

        if (newValidFrom.isAfter(newValidTo)) {
            throw new IllegalArgumentException("Ngày 'Hiệu lực từ' không được sau ngày 'Hiệu lực đến'.");
        }

        Optional.ofNullable(request.validFrom()).ifPresent(existingPriceRule::setValidFrom);
        Optional.ofNullable(request.validTo()).ifPresent(existingPriceRule::setValidTo);
        Optional.ofNullable(request.notes()).ifPresent(existingPriceRule::setNotes);

        RouteBusCategoryPrice updatedPriceRule = routeBusCategoryPriceDAO.save(existingPriceRule);
        log.info("Đã cập nhật quy tắc giá với ID: {}", updatedPriceRule.getId());
        
        // ✅ FIX: Ensure route and busCategory are populated to avoid lazy loading issues
        // Note: updatedPriceRule should already have these from existingPriceRule, but ensure they're accessible
        if (updatedPriceRule.getRoute() != null && updatedPriceRule.getBusCategory() != null) {
            // Force initialization if they are proxies
            updatedPriceRule.getRoute().getId();
            updatedPriceRule.getBusCategory().getId();
        }
        
        return convertToRouteBusCategoryPriceResponse(updatedPriceRule);
    }

    @Override
    @Transactional
    public void deleteRouteBusCategoryPrice(Integer id) {
        if (!routeBusCategoryPriceDAO.existsById(id)) {
            throw new EntityNotFoundException("Không tìm thấy quy tắc giá với ID: " + id);
        }
        routeBusCategoryPriceDAO.deleteById(id);
        log.info("Đã xóa quy tắc giá với ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteBusCategoryPriceResponse> findAllRouteBusCategoryPrices() {
        return routeBusCategoryPriceDAO.findAllWithDetails().stream()
                .map(this::convertToRouteBusCategoryPriceResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RouteBusCategoryPriceResponse> findRouteBusCategoryPriceById(Integer id) {
        return routeBusCategoryPriceDAO.findByIdWithDetails(id)
                .map(this::convertToRouteBusCategoryPriceResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RouteBusCategoryPriceResponse> findActiveRouteBusCategoryPrice(Integer routeId, Integer busCategoryId, LocalDate date) {
        List<RouteBusCategoryPrice> activeRules = routeBusCategoryPriceDAO.findActivePriceRule(routeId, busCategoryId, date);

        if (activeRules.isEmpty()) {
            return Optional.empty();
        }
        return activeRules.stream()
                .filter(rule -> rule.getPromotionPrice() != null && rule.getPromotionPrice().compareTo(BigDecimal.ZERO) > 0)
                .findFirst()
                .or(() -> activeRules.stream().findFirst())
                .map(this::convertToRouteBusCategoryPriceResponse);
    }



}
