package com.example.car.services;
import com.example.car.entities.Car;
import com.example.car.models.CarResponse;
import com.example.car.models.Client;
import com.example.car.repositories.CarRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;
@Service
public class CarService {
    private final CarRepository repository;
    private final RestTemplate restTemplate;
    private static final String CLIENT_URL = "http://localhost:8888/SERVICE-CLIENT/api/client/";
    public CarService(CarRepository repository, RestTemplate restTemplate) { this.repository = repository; this.restTemplate = restTemplate; }
    public List<CarResponse> findAll() { return repository.findAll().stream().map(this::toResponse).toList(); }
    public CarResponse findById(Long id) { return toResponse(repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voiture non trouvée : " + id))); }
    public Car save(Car car) { car.setId(null); return repository.save(car); }
    private CarResponse toResponse(Car car) {
        Client client = null;
        if (car.getClient_id() != null) {
            try { client = restTemplate.getForObject(CLIENT_URL + car.getClient_id(), Client.class); }
            catch (Exception ex) { System.err.println("Client indisponible : " + ex.getMessage()); }
        }
        return CarResponse.builder().id(car.getId()).brand(car.getBrand()).model(car.getModel()).matricule(car.getMatricule()).client(client).build();
    }
}
