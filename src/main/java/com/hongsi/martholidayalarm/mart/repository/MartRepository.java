package com.hongsi.martholidayalarm.mart.repository;

import com.hongsi.martholidayalarm.mart.domain.Mart;
import com.hongsi.martholidayalarm.mart.domain.MartType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MartRepository extends JpaRepository<Mart, Long>, MartRepositoryCustom {

	Optional<Mart> findByRealIdAndMartType(String realId, MartType martType);
	Optional<Mart> findByMartTypeAndBranchName(MartType martType, String branchName);
}
