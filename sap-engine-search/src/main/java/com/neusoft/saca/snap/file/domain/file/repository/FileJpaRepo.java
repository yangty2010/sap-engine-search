package com.neusoft.saca.snap.file.domain.file.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.neusoft.saca.snap.file.domain.file.vo.FileEntry;

public interface FileJpaRepo extends PagingAndSortingRepository<FileEntry, String> {

	/**
	 * 根据id列表按照顺序查找文件实体信息
	 * 
	 * @param ids
	 * @return
	 */
	public List<FileEntry> findByIdList(List<String> ids);

	public Page<FileEntry> findByPublisher(String publisher, Pageable pageable);

	/**
	 * 带有隐私的指定目录下文件列表
	 * @param scopeId
	 * @param userId
	 * @param orgIds
	 * @param pageable
	 * @return
	 */
	@Query(value="select f from FileEntry f where f.publisher=:publisher and (f.privacy=3  or (f.privacy=1 and locate(:userId,f.privacyMember)<>0) or (f.privacy=2 and f.orgId in (:orgIds)))")
	public Page<FileEntry> findByPublisherWithPrivacy(@Param("publisher")String publisher, @Param("userId")String userId, @Param("orgIds")List<String> orgIds,Pageable pageable);
	
	public Page<FileEntry> findByScopeId(String scopeId, Pageable pageable);

	/**
	 * 带有隐私的指定目录下文件列表
	 * @param scopeId
	 * @param userId
	 * @param pageable
	 * @return
	 */
	@Query(value="select f from FileEntry f where f.scopeId=:scopeId and (f.privacy=3  or f.publisher=:userId or (f.privacy=1 and locate(:userId,f.privacyMember)<>0))")
	public Page<FileEntry> findByScopeIdWithPrivacy(@Param("scopeId") String scopeId,@Param("userId")String userId,Pageable pageable);
	
	/**
	 * 带有隐私的指定目录下文件列表
	 * @param scopeId
	 * @param userId
	 * @param orgIds
	 * @param pageable
	 * @return
	 */
	@Query(value="select f from FileEntry f where f.scopeId=:scopeId and (f.privacy=3 or f.publisher=:userId or (f.privacy=1 and locate(:userId,f.privacyMember)<>0) or (f.privacy=2 and f.orgId in (:orgIds)))")
	public Page<FileEntry> findByScopeIdWithPrivacy(@Param("scopeId")String scopeId, @Param("userId")String userId, @Param("orgIds")List<String> orgIds,Pageable pageable);

	public List<FileEntry> findByScopeId(String scopeId, Sort sort);

	public Page<FileEntry> findByScopeIdAndInKnowledgeBase(String scopeId, boolean inKnowledgeBase, Pageable pageable);

	public List<FileEntry> findByScopeIdAndInKnowledgeBase(String scopeId, boolean inKnowledgeBase, Sort sort);

	public Page<FileEntry> findByScopeType(String scopeType, Pageable pageable);

	public Page<FileEntry> findByScopeTypeNot(String scopeType, Pageable pageable);
}
