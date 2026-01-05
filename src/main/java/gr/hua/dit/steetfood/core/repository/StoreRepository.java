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

    Optional <Store> findById(long id);

    List <Store> findStoresByStoreType(StoreType storeType);

    Optional <Store> findByStoreAddress(String address);

    boolean existsByStoreAddress(String address);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional <Store> findByOwnerId(long id);

    List <Store> findStoresByOwnerId(long id);

    //List <Store> getAllStores ();


    //List<Store> findAllByOpen();
    //NA DW POIO DOYLEYEI
    //List <Store> findAllByOpenIsTrue();

    //List <Store> findAllByStoreType(StoreType storeType);

}
