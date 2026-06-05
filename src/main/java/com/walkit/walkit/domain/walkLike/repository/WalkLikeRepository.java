package com.walkit.walkit.domain.walkLike.repository;

import com.walkit.walkit.domain.user.entity.User;
import com.walkit.walkit.domain.walk.entity.Walk;
import com.walkit.walkit.domain.walkLike.entity.WalkLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalkLikeRepository extends JpaRepository<WalkLike, Long> {
    Optional<WalkLike> findByUserAndWalk(User user, Walk walk);
    boolean existsByUserAndWalk(User user, Walk walk);
    boolean existsByUserIdAndWalkId(Long userId, Long walkId);
    Optional<WalkLike> findByUserIdAndWalkId(Long userId, Long walkId);
    void deleteByUserIdAndWalkId(Long userId, Long walkId);
    List<WalkLike> findByWalk(Walk walk);
    int countByWalkId(Long walkId);
}
