package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order-service")
@RequiredArgsConstructor
public class OrderController {
    private final Environment env;
    private final OrderService orderService;

    @GetMapping("/health_check")
    public String status(HttpServletRequest request) {
        return String.format("It's Working in Order Service on Port %s", request.getServerPort());
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId, @RequestBody RequestOrder orderDetails) {
        final ModelMapper modelMapper = new ModelMapper();

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); // 딱 맞아떨어져야 가능(STRICT)

        OrderDto orderDto = mapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);
        OrderDto createDto = orderService.createOrder(orderDto);
        final ResponseOrder returnValue = modelMapper.map(createDto, ResponseOrder.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
    }
    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrders(@PathVariable("userId") String userId) {
        final Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();
        orderList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseOrder.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}