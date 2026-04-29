package com.resumeiq.repository;

import com.resumeiq.entity.RoleKeyword;
import com.resumeiq.enums.KeywordType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleKeywordRepository extends JpaRepository<RoleKeyword, Long> {
    List<RoleKeyword> findByJobRoleId(Long jobRoleId);
    List<RoleKeyword> findByJobRoleIdAndType(Long jobRoleId, KeywordType type);
    void deleteByJobRoleId(Long jobRoleId);
}
