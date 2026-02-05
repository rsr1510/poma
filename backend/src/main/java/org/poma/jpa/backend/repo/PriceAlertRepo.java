package org.poma.jpa.backend.repo;

import org.poma.jpa.backend.entity.Assets;
import org.poma.jpa.backend.entity.PriceAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceAlertRepo extends JpaRepository<PriceAlert, Long> {

    List<PriceAlert> findByIsActive(Boolean isActive);

    List<PriceAlert> findByAsset(Assets asset);

    @Query("SELECT pa FROM PriceAlert pa WHERE pa.isActive = true AND pa.asset = :asset")
    List<PriceAlert> findActiveAlertsForAsset(@Param("asset") Assets asset);

    @Query("SELECT pa FROM PriceAlert pa WHERE pa.isActive = true")
    List<PriceAlert> findAllActiveAlerts();

    Optional<PriceAlert> findByAssetAndConditionAndThresholdPrice(
            Assets asset, 
            PriceAlert.AlertCondition condition, 
            java.math.BigDecimal thresholdPrice
    );

    @Query("SELECT COUNT(pa) FROM PriceAlert pa WHERE pa.isActive = true")
    long countActiveAlerts();
}
