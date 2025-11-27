package gr.hua.dit.steetfood.core.service.impl;

import gr.hua.dit.steetfood.core.model.Person;
import gr.hua.dit.steetfood.core.model.PersonLocation;
import gr.hua.dit.steetfood.core.repository.PersonLocationRepository;
import gr.hua.dit.steetfood.core.service.LocationService;

import org.springframework.stereotype.Service;

@Service
public class LocationServiceImpl implements LocationService {
    private final PersonLocationRepository personLocationRepository;

    public LocationServiceImpl(PersonLocationRepository personLocationRepository) {
        if (personLocationRepository == null) {throw new NullPointerException();}
        this.personLocationRepository = personLocationRepository;
    }

    @Override
    public boolean CreatePersonLocation(PersonLocation personLocation) {
        this.personLocationRepository.save(personLocation);
        //TODO LOGIKH VSTE NA MHN BAZEI KAPOIOS TO IDIO LOC DYO FORES STO ID TOY
        //for (Person person : personLocation.getPersons()) {

        //}
        return true;
    }
}
