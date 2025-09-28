package com.devteria.identity_service.repository;

import com.devteria.identity_service.entity.Resources;
import com.devteria.identity_service.enums.ResourcesStatus;
import com.devteria.identity_service.enums.ResourcesType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourcesRepository extends JpaRepository<Resources, String> {
  List<Resources> findResourcesByResourcesStatus(ResourcesStatus status);

  Resources findResourceByResourcesID(String resourcesId);

  List<Resources> findResourcesByResourcesType(ResourcesType resourcesType);
}
