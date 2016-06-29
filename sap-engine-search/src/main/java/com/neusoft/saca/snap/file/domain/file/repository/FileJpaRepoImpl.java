package com.neusoft.saca.snap.file.domain.file.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.neusoft.saca.snap.file.domain.file.vo.FileEntry;

public class FileJpaRepoImpl {

	@PersistenceContext(unitName="enginePersistenceUnit")
	private EntityManager em;
	
	/**
	 * 根据id列表按照顺序查找文件实体信息
	 * 
	 * @param ids
	 * @return
	 */
	public List<FileEntry> findByIdList(List<String> ids){
		
		if(ids.isEmpty()){
			return new ArrayList<FileEntry>();
		}
		
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<FileEntry> query = builder.createQuery(FileEntry.class);
		Root<FileEntry> root = query.from(FileEntry.class);
		
		Path<String> idPath = root.get("id");
		query.where(idPath.in(ids));
		List<FileEntry> fileEntryList = em.createQuery(query).getResultList();
		
		if(!fileEntryList.isEmpty()){
			Map<String, FileEntry> map = new HashMap<String, FileEntry>();
			for(FileEntry entry : fileEntryList){
				map.put(entry.getId(), entry);
			}
			fileEntryList.clear();
			for(String id : ids){
				if(map.get(id) != null){
					fileEntryList.add(map.get(id));
				}
			}
		}
		
		return fileEntryList;
	}
}
