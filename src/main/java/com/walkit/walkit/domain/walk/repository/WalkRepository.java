package com.walkit.walkit.domain.walk.repository;


import com.walkit.walkit.domain.walk.entity.Walk;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface WalkRepository extends JpaRepository<Walk, Long> {
    Optional<Walk> findByIdAndUser_Id(Long walkId, Long userId);

    @Query("""
select w from Walk w
left join fetch w.points p
where w.id = :walkId and w.user.id = :userId
""")
    Optional<Walk> findDetailByIdAndUserId(Long walkId, Long userId);  // points까지 같이 조인해서 한 번에 가져옴

    // 주간 걸음수 합
    @Query("""
    select coalesce(sum(w.stepCount), 0)
    from Walk w
    where w.user.id = :userId
      and w.startTime >= :startMillis
      and w.startTime < :endMillis
""")
    long sumStepsBetween(
            @Param("userId") Long userId,
            @Param("startMillis") Long startMillis,
            @Param("endMillis") Long endMillis
    );

    // startTime(millis) 기준으로 기간 내 산책 시작 '날짜' 목록
    @Query("SELECT w FROM Walk w " +
            "WHERE w.user.id = :userId " +
            "AND w.startTime >= :startMillis " +
            "AND w.startTime < :endMillis " +
            "ORDER BY w.startTime")
    List<Walk> findWalksBetween(
            @Param("userId") Long userId,
            @Param("startMillis") Long startMillis,
            @Param("endMillis") Long endMillis
    );


    // 사용자 산책 기록 횟수 조회
    long countByUser_Id(Long userId);

    // 사용자 전체 산책 시간 조회
    @Query("""
        select coalesce(sum(w.totalTime), 0)
        from Walk w
        where w.user.id = :userId
    """)
    long sumTotalTimeByUserId(@Param("userId") Long userId);


    // 최근 N개 산책 기록 조회
    @Query("""
select w from Walk w
where w.user.id = :userId
order by w.startTime desc
""")
    List<Walk> findTopByUserIdOrderByStartTimeDesc(Long userId, Pageable pageable);

    // 오늘 산책 걸음수 조회
    @Query("""
select coalesce(sum(w.stepCount), 0)
from Walk w
where w.user.id = :userId
  and w.startTime >= :startMillis
  and w.startTime < :endMillis
""")
    Integer sumTodaySteps(@Param("userId") Long userId,
                          @Param("startMillis") long startMillis,
                          @Param("endMillis") long endMillis);

    // 사용자 전체 산책 기록 조회
    @Query("""
        select distinct w
        from Walk w
        left join fetch w.points p
        where w.user.id = :userId
        order by w.startTime desc
    """)
    List<Walk> findAllDetailByUserId(@Param("userId") Long userId);

    Optional<Walk> findFirstByUserIdOrderByCreatedDateDesc(Long userId);

    @Query("""
        select w from Walk w
        left join fetch w.points
        where w.user.id = :userId
        order by w.createdDate desc
    """)
    List<Walk> findTopWithPointsByUserId(@Param("userId") Long userId, Pageable pageable);

    List<Walk> findAllByUser_IdIn(List<Long> userIds);

}
