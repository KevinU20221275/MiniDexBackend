package org.kmontano.minidex.infrastructure.repository;

import org.kmontano.minidex.domain.pokemonShop.TrainerShopState;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TrainerShopStateRepository extends MongoRepository<TrainerShopState, String> {
    Optional<TrainerShopState> findByTrainerIdAndShopDate(String trainerId, LocalDate today);
}
