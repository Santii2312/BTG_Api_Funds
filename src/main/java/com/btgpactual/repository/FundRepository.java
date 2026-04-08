package com.btgpactual.repository;

import com.btgpactual.domain.Fund;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FundRepository extends MongoRepository<Fund, String> {
}
