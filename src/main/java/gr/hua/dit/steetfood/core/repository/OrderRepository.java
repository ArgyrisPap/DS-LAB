package gr.hua.dit.steetfood.core.repository;

import gr.hua.dit.steetfood.core.model.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findOrderById(Long id);

    List<Order> findAllByStoreId(Long storeId);

    List <Order> findAllByPersonId(Long studentId);

}
