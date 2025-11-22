package gr.hua.dit.steetfood.core.repository;


import gr.hua.dit.steetfood.core.model.Store;

import gr.hua.dit.steetfood.core.model.StoreType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store,Long> {

    Optional<Store> findByStoreName(String name);

    Optional <Store> findByStoreAddress(String address);

    //List<Store> findAllByOpen();
    //NA DW POIO DOYLEYEI
    //List <Store> findAllByOpenIsTrue();

    //List <Store> findAllByStoreType(StoreType storeType);

}
