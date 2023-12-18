package com.jpabook.jpashop.service.query;

import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class OrderQueryService {

//    public List<OrderDto> ordersV3() {
//        List<Order> orders = orderRepository.findAllWithItem();
//        List<OrderDto> result = orders.stream()
//                .map(o -> new OrderDto(o))
//                .collect(Collectors.toList());
//        return result;
//    }
}
