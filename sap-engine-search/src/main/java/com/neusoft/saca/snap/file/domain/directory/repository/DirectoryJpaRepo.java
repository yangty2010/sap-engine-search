package com.neusoft.saca.snap.file.domain.directory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.neusoft.saca.snap.file.domain.directory.vo.DirectoryEntry;

public interface DirectoryJpaRepo extends CrudRepository<DirectoryEntry, String> {

	public List<DirectoryEntry> findByParentOrderByOrderAsc(String parent);
	
	@Query(value="select d from DirectoryEntry d where d.parent=:parent and (d.privacy=3 or (d.privacy=1 and locate(:userId,d.privacyMember)<>0) or (d.privacy=2 and locate(:groupId,d.privacyGroup)<>0) or d.creator = :userId)  order by d.order asc")
	public List<DirectoryEntry> findByParentWithPrivacy(@Param("parent")String parent, @Param("userId")String userId, @Param("groupId")String groupId);

	
	public List<DirectoryEntry> findByParent(String parent);
	
	@Modifying
	@Query(value="UPDATE DirectoryEntry d SET d.fileAmount=d.fileAmount+ :fileAmount WHERE d.id=:directoryId")
	public void addFileAmount(@Param("directoryId")String directoryId,@Param("fileAmount")int fileAmount);
	
	@Modifying
	@Query(value="UPDATE DirectoryEntry d SET d.fileAmount=d.fileAmount- :fileAmount WHERE d.id=:directoryId")
	public void decreaseFileAmount(@Param("directoryId")String directoryId,@Param("fileAmount")int fileAmount);
	
	@Modifying
	@Query(value="delete from DirectoryEntry where parent=:parent")
	public void deleteDirectories(@Param("parent")String parent);
	
	@Modifying
	@Query(value="delete from DirectoryEntry where parent=:parent and id not in :excludeDirectory")
	public void deleteDirectories(@Param("parent")String parent,@Param("excludeDirectory")List<String> excludeDirectory);
	
	public List<DirectoryEntry> findByRootParent(String rootParent);
}
