package com.resumeiq.repository;

import com.resumeiq.entity.RoleKeyword;
import com.resumeiq.enums.KeywordType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleKeywordRepository extends MongoRepository<RoleKeyword, String> {
    List<RoleKeyword> findByJobRoleId(Long jobRoleId);
    List<RoleKeyword> findByJobRoleIdAndType(Long jobRoleId, KeywordType type);
    void deleteByJobRoleId(Long jobRoleId);
}
