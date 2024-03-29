package project.userFeaturePortal.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import project.userFeaturePortal.model.entity.Log;
import project.userFeaturePortal.model.entity.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author - EugenFriesen 12.02.2021
 */
@Repository
public interface LogRepository extends JpaRepository<Log, Integer> {

  @Query(
      "SELECT log FROM Log log"
          + " WHERE (:severity is null or log.severity = :severity)"
          + " AND (:message is null or log.message like concat('%',:message,'%'))"
          + " AND (:startDate is null or log.timestamp > :startDate)"
          + " AND (:endDate is null or log.timestamp < :endDate)"
          + " AND (:user is null or log.user = :user)")
  List<Log> findLogs(
      String severity, String message, LocalDateTime startDate, LocalDateTime endDate, User user);

  List<Log> deleteBySeverity(String severity);

  List<Log> findByUser(User user);
}
