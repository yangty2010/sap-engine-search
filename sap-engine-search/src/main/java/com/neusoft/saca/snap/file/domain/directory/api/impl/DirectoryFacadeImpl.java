package com.neusoft.saca.snap.file.domain.directory.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neusoft.saca.snap.file.constant.DirectoryConstant;
import com.neusoft.saca.snap.file.domain.directory.api.DirectoryFacade;
import com.neusoft.saca.snap.file.domain.directory.application.DirectoryService;
import com.neusoft.saca.snap.file.domain.directory.repository.DirectoryJpaRepo;
import com.neusoft.saca.snap.file.domain.directory.vo.DirectoryEntry;
import com.neusoft.saca.snap.file.domain.directory.vo.DirectoryPathVo;
import com.neusoft.saca.snap.file.domain.file.application.FileDomainService;
import com.neusoft.saca.snap.file.domain.file.vo.FileEntry;

@Service
public class DirectoryFacadeImpl implements DirectoryFacade {

	@Autowired
	private FileDomainService fileDomainService;

	@Autowired
	private DirectoryService directoryService;

	@Autowired
	private DirectoryJpaRepo directoryJpaRepo;

	@Override
	@Transactional(readOnly = true)
	public List<DirectoryEntry> obtainDirectories(String parent) {
		return directoryService.obtainDirectories(parent);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DirectoryEntry> obtainDirectories(String parent, String userId,
			String groupId) {
		return directoryService.obtainDirectories(parent, userId, groupId);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<FileEntry> obtainFiles(String directory, int page, int size,
			Sort sort) {
		return fileDomainService.obtainFilesByScopeId(directory, sort, page,
				size);
	}

	private boolean checkDirectoryRemovable(DirectoryEntry directoryEntry) {
		boolean hasFile = checkDirectoryHasFile(directoryEntry);
		if (hasFile) {
			return false;
		} else {
			List<DirectoryEntry> childrenDirectories = directoryJpaRepo
					.findByParent(directoryEntry.getId());
			if (childrenDirectories == null || childrenDirectories.size() == 0) {
				return true;
			} else {
				for (DirectoryEntry directoryEntry2 : childrenDirectories) {
					boolean result = checkDirectoryRemovable(directoryEntry2);
					if (!result) {
						return false;
					}
				}
				return true;
			}
		}

	}

	@Override
	@Transactional(readOnly = true)
	public boolean checkDirectoryRemovable(String directoryId) {
		DirectoryEntry directoryEntry = directoryService.obtain(directoryId);
		if (directoryEntry == null) {
			return false;
		} else {
			return checkDirectoryRemovable(directoryEntry);
		}
	}

	private boolean checkDirectoryHasFile(DirectoryEntry directoryEntry) {
		if (directoryEntry == null) {
			return false;
		}
		if (directoryEntry.getFileAmount() > 0) {
			return true;
		}
		return false;
	}

	@Override
	@Transactional(readOnly = false)
	public void adjustDirectory(String parent, List<DirectoryEntry> directories) {
		int maxDirectoryOrder = 0;
		Map<String, DirectoryEntry> directoryMap = directoryService
				.obtainDirectoriesMap(parent);
		// 获取原有目录中存在文件的id，保证不被删除
		List<String> unRemovableDirectoryIds = new ArrayList<String>();
		for (String directoryId : directoryMap.keySet()) {
			if (directoryMap.get(directoryId).getFileAmount() > 0) {
				unRemovableDirectoryIds.add(directoryId);
			}
		}

		String rootPrarentDirectoryId = null;
		DirectoryEntry parentEntry = directoryService.obtain(parent);
		// 为目录设置根级父目录
		if (parentEntry == null) {
			throw new RuntimeException("文档目录内部数据错误！");
		} else if (parentEntry.getRootParent().equals(
				DirectoryConstant.ROOT_DIRECTORY)) {
			rootPrarentDirectoryId = parent;
		} else {
			rootPrarentDirectoryId = parentEntry.getRootParent();
		}

		List<String> newDirectoryIds = new ArrayList<String>();
		if (directories != null && directories.size() > 0) {
			maxDirectoryOrder = directories.size();
			for (DirectoryEntry directoryEntry : directories) {
				// 如果id不存在或者不在原有目录map中（即新加入的目录），为其生成UUID
				if (directoryEntry.getId() == null
						|| !directoryMap.containsKey(directoryEntry.getId())) {
					directoryEntry.setId(UUID.randomUUID().toString());
				} else {
					directoryEntry.setFileAmount(directoryMap.get(
							directoryEntry.getId()).getFileAmount());
				}
				directoryEntry.setRootParent(rootPrarentDirectoryId);
				newDirectoryIds.add(directoryEntry.getId());
			}
			unRemovableDirectoryIds.removeAll(newDirectoryIds);
		}
		// 保存那些原有目录中带有文件但是参数中没有的目录
		List<DirectoryEntry> unRemovableDirectories = new ArrayList<DirectoryEntry>();
		for (String directoryId : unRemovableDirectoryIds) {
			DirectoryEntry directoryEntry = directoryMap.get(directoryId);
			// 更新序号
			directoryEntry.setOrder(++maxDirectoryOrder);
			unRemovableDirectories.add(directoryEntry);
		}
		directories.addAll(unRemovableDirectories);
		// 保存不能被删除的目录
		directoryService.saveDirectories(directories);
		List<String> directoryIds = new ArrayList<String>();
		for (DirectoryEntry directoryEntry : directories) {
			directoryIds.add(directoryEntry.getId());
		}
		// 删除父目录下其他子目录
		directoryService.deleteDirectories(parent, directoryIds);

	}

	@Override
	@Transactional(readOnly = true)
	public List<DirectoryPathVo> obtainPathList(String directoryId) {
		List<DirectoryPathVo> paths = new ArrayList<DirectoryPathVo>();
		return directoryService.obtainDirectoryPath(paths, directoryId);
	}

	@Override
	@Transactional(readOnly = true)
	public DirectoryEntry obtainDirectory(String directoryId) {
		return directoryService.obtain(directoryId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DirectoryEntry> obtainDirectoryTree(String rootDirectory) {
		List<DirectoryEntry> resultList = new ArrayList<DirectoryEntry>();
		List<DirectoryEntry> directoryEntries = directoryJpaRepo
				.findByRootParent(rootDirectory);
		Map<String, PriorityQueue<DirectoryEntry>> direcotyMap = new HashMap<String, PriorityQueue<DirectoryEntry>>();

		if (directoryEntries.size() == 0) {
			return resultList;
		}

		for (DirectoryEntry directoryEntry : directoryEntries) {
			PriorityQueue<DirectoryEntry> queue = direcotyMap
					.get(directoryEntry.getParent());
			if (queue == null) {
				queue = new PriorityQueue<DirectoryEntry>();
				direcotyMap.put(directoryEntry.getParent(), queue);
			}
			queue.add(directoryEntry);
		}

		for (String parent : direcotyMap.keySet()) {
			PriorityQueue<DirectoryEntry> queue = direcotyMap.get(parent);
			while (true) {
				DirectoryEntry directoryEntry = queue.poll();
				if (directoryEntry == null) {
					break;
				}
				resultList.add(directoryEntry);
			}
		}

		return resultList;
	}

	@Override
	@Transactional(readOnly = false)
	public void addFileAmount(String directoryId, int fileAmount) {
		directoryJpaRepo.addFileAmount(directoryId, fileAmount);
	}

	@Override
	@Transactional(readOnly = false)
	public void decreaseFileAmount(String directoryId, int fileAmount) {
		directoryJpaRepo.decreaseFileAmount(directoryId, fileAmount);
	}

	@Override
	@Transactional(readOnly = false)
	public void create(DirectoryEntry directoryEntry) {
		String directoryId = directoryEntry.getId();
		DirectoryEntry directory1 = directoryJpaRepo.findOne(directoryId);
		if (directory1 != null) {
			throw new RuntimeException("目录已经存在，directoryId: " + directoryId);
		}
		directoryJpaRepo.save(directoryEntry);
	}

	@Override
	@Transactional(readOnly = false)
	public void batchCreate(List<DirectoryEntry> directoryEntries) {
		if (directoryEntries != null && directoryEntries.size() > 0) {
			// TODO 没有判断原来是否存在该目录
			directoryJpaRepo.save(directoryEntries);
		}
	}

	@Override
	public boolean deleteDirectory(String directoryId) {
		directoryJpaRepo.delete(directoryId);
		return true;
	}

	@Override
	public void update(DirectoryEntry directoryEntry) {
		directoryJpaRepo.save(directoryEntry);
	}

}
