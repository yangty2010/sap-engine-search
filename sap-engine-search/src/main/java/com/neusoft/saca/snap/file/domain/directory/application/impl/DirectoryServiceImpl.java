package com.neusoft.saca.snap.file.domain.directory.application.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neusoft.saca.snap.file.constant.DirectoryConstant;
import com.neusoft.saca.snap.file.domain.directory.application.DirectoryService;
import com.neusoft.saca.snap.file.domain.directory.repository.DirectoryJpaRepo;
import com.neusoft.saca.snap.file.domain.directory.vo.DirectoryEntry;
import com.neusoft.saca.snap.file.domain.directory.vo.DirectoryPathVo;

@Service
public class DirectoryServiceImpl implements DirectoryService {

	@Autowired
	private DirectoryJpaRepo directoryJpaRepo;
	
	@Override
	@Transactional(readOnly=true)
	public List<DirectoryEntry> obtainDirectories(String parent) {
		return directoryJpaRepo.findByParentOrderByOrderAsc(parent);
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<DirectoryEntry> obtainDirectories(String parent, String userId, String groupId) {
		return directoryJpaRepo.findByParentWithPrivacy(parent, userId, groupId);
	}

	@Override
	@Transactional(readOnly=true)
	public DirectoryEntry obtain(String directoryId) {
		return directoryJpaRepo.findOne(directoryId);
	}
	
	@Override
	@Transactional(readOnly=true)
	public Map<String, DirectoryEntry> obtainDirectoriesMap(String parent) {
		Map<String, DirectoryEntry> directoryMap=new LinkedHashMap<String, DirectoryEntry>();
		List<DirectoryEntry> directories=directoryJpaRepo.findByParentOrderByOrderAsc(parent);
		for (DirectoryEntry directoryEntry : directories) {
			directoryMap.put(directoryEntry.getId(), directoryEntry);
		}
		return directoryMap;
	}

	@Override
	@Transactional(readOnly=true)
	public Map<String, DirectoryEntry> obtainDirectoriesMap(String parent, String userId, String groupId) {
		Map<String, DirectoryEntry> directoryMap=new LinkedHashMap<String, DirectoryEntry>();
		List<DirectoryEntry> directories=directoryJpaRepo.findByParentWithPrivacy(parent, userId, groupId);
		for (DirectoryEntry directoryEntry : directories) {
			directoryMap.put(directoryEntry.getId(), directoryEntry);
		}
		return directoryMap;
	}

	@Override
	@Transactional(readOnly=false)
	public void saveDirectories(List<DirectoryEntry> directories) {
		directoryJpaRepo.save(directories);
	}

	@Override
	@Transactional(readOnly=true)
	public List<DirectoryPathVo> obtainDirectoryPath(List<DirectoryPathVo> paths, String directoryId) {
		if (DirectoryConstant.ROOT_DIRECTORY.equalsIgnoreCase(directoryId)) {
			return paths;
		}
		DirectoryEntry directoryEntry=directoryJpaRepo.findOne(directoryId);
		if (directoryEntry==null) {
			return paths;
		}
		DirectoryPathVo directoryPathVo=new DirectoryPathVo();
		directoryPathVo.setId(directoryId);
		directoryPathVo.setName(directoryEntry.getName());
		if (paths==null) {
			paths=new ArrayList<DirectoryPathVo>();
		}
		paths.add(0, directoryPathVo);
		return obtainDirectoryPath(paths,directoryEntry.getParent());
	}

	@Override
	@Transactional(readOnly=false)
	public void deleteDirectories(String parent) {
		directoryJpaRepo.deleteDirectories(parent);
	}

	@Override
	@Transactional(readOnly=false)
	public void deleteDirectories(String parent, List<String> excludeDirectory) {
		if (excludeDirectory==null||excludeDirectory.size()==0) {
			directoryJpaRepo.deleteDirectories(parent);
		}else{
			directoryJpaRepo.deleteDirectories(parent, excludeDirectory);
		}
	}

}
