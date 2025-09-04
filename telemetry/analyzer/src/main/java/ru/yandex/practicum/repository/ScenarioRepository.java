package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.model.Scenario;

import java.util.List;
import java.util.Optional;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {

    // Обычные методы (без подгрузки коллекций)
    List<Scenario> findByHubId(String hubId);

    Optional<Scenario> findByHubIdAndName(String hubId, String name);

    void deleteByName(String name);

    // Сценарий сразу с conditions и actions
    @Query("""
        select distinct s
        from Scenario s
        left join fetch s.conditions
        left join fetch s.actions
        where s.id = :id
    """)
    Optional<Scenario> findByIdWithConditionsAndActions(@Param("id") Long id);

    // По hubId сразу со всеми conditions и actions
    @Query("""
           select distinct s 
           from Scenario s
           left join fetch s.conditions c
           left join fetch s.actions a
           where s.hubId = :hubId
           """)
    List<Scenario> findByHubIdWithConditionsAndActions(@Param("hubId") String hubId);

    // По hubId и name сразу со всеми conditions и actions
    @Query("""
        select distinct s
        from Scenario s
        left join fetch s.conditions
        left join fetch s.actions
        where s.hubId = :hubId and s.name = :name
    """)
    Optional<Scenario> findByHubIdAndNameWithConditionsAndActions(@Param("hubId") String hubId,
                                                                  @Param("name") String name);
}
